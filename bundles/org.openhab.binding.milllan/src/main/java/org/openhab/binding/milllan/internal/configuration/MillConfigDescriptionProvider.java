/*
 * Mill LAN Binding, an add-on for openHAB for controlling Mill devices which
 * exposes a local REST API. Copyright (c) 2024 Nadahar
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.milllan.internal.configuration;

import static org.openhab.binding.milllan.internal.MillBindingConstants.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.MillUtil;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.config.core.ConfigDescriptionBuilder;
import org.openhab.core.config.core.ConfigDescriptionParameter;
import org.openhab.core.config.core.ConfigDescriptionProvider;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingUID;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link ConfigDescriptionProvider} implementation that provides configuration descriptions
 * for dynamic configuration parameters.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
@Component(service = {ConfigDescriptionProvider.class, MillConfigDescriptionProvider.class})
public class MillConfigDescriptionProvider implements ConfigDescriptionProvider {

    private final Logger logger = LoggerFactory.getLogger(MillConfigDescriptionProvider.class);

    private final String uriPrefix = "thing:";
    private final String uriFilter = uriPrefix + BINDING_ID;

    private final Bundle bundle;

    private final TranslationProvider i18nProvider;

    private final LocaleProvider localeProvider;

    /** The {@link Map} of enabled parameter descriptions, <b>must be synchronized</b> on {@code this}! */
    protected final Map<URI, Map<String, ParameterDescription>> enabledParameters = new HashMap<>();

    /** The {@link Map} of cached, localized parameter descriptions, <b>must be synchronized</b> on {@code this}! */
    protected final Map<URI, Map<Locale, Map<String, ConfigDescriptionParameter>>> localizedParameters =
        new HashMap<>();

    /**
     * Creates a new instance using the specified parameters.
     *
     * @param componentContext the {@link ComponentContext} to use.
     * @param i18nProvider the {@link TranslationProvider} to use.
     * @param localeProvider the {@link LocaleProvider} to use.
     */
    @Activate
    public MillConfigDescriptionProvider(
        ComponentContext componentContext,
        @Reference TranslationProvider i18nProvider,
        @Reference LocaleProvider localeProvider
    ) {
        this.i18nProvider = i18nProvider;
        this.localeProvider = localeProvider;
        this.bundle = componentContext.getBundleContext().getBundle();
    }

    @Override
    public Collection<ConfigDescription> getConfigDescriptions(@Nullable Locale locale) {
        Locale loc = locale == null ? localeProvider.getLocale() : locale;

        List<ConfigDescription> result = new ArrayList<>();
        ConfigDescription configDescription;
        synchronized (this) {
            for (Entry<URI, Map<String, ParameterDescription>> enabledEntry : enabledParameters.entrySet()) {
                configDescription = getConfigDescription(enabledEntry.getKey(), loc);
                if (configDescription != null) {
                    result.add(configDescription);
                }
            }
        }
        return result;
    }

    @Override
    public @Nullable ConfigDescription getConfigDescription(URI uri, @Nullable Locale locale) {
        if (!uriFilter.equals(getURIParts(uri, 2))) {
            return null;
        }

        Locale loc = locale == null ? localeProvider.getLocale() : locale;
        synchronized (this) {
            Map<String, ParameterDescription> enabledMap = enabledParameters.get(uri);
            if (enabledMap == null || enabledMap.isEmpty()) {
                return null;
            }

            Map<Locale, Map<String, ConfigDescriptionParameter>> localeMap = localizedParameters.get(uri);
            if (localeMap == null) {
                localeMap = new HashMap<>();
                localizedParameters.put(uri, localeMap);
            }

            Map<String, ConfigDescriptionParameter> thingMap = localeMap.get(loc);
            if (thingMap == null) {
                thingMap = new LinkedHashMap<>();
                localeMap.put(loc, thingMap);
            }

            for (Entry<String, ParameterDescription> enabledEntry : enabledMap.entrySet()) {
                if (!thingMap.containsKey(enabledEntry.getKey())) {
                    thingMap.put(
                        enabledEntry.getKey(),
                        enabledEntry.getValue().getConfigDescriptionParameter(bundle, i18nProvider, loc)
                    );
                }
            }
            return ConfigDescriptionBuilder.create(uri).withParameters(new ArrayList<>(thingMap.values())).build();
        }
    }

    /**
     * Enables the specified configuration parameter descriptions.
     *
     * @param uid the {@link ThingUID} for which to enable configuration parameter descriptions.
     * @param configParameterNames the names/IDs of the configuration parameters whose descriptions to enable.
     */
    public void enableDescriptions(@Nullable ThingUID uid, @Nullable String... configParameterNames) {
        if (uid == null) {
            return;
        }
        enableDescriptions(URI.create(uriPrefix + uid.getAsString()), configParameterNames);
    }

    /**
     * Enables the specified configuration parameter descriptions.
     *
     * @param uri the URI identifying the {@link Thing} for which to enable configuration parameter descriptions.
     * @param configParameterNames the names/IDs of the configuration parameters whose descriptions to enable.
     */
    @SuppressWarnings("null")
    public void enableDescriptions(@Nullable URI uri, @Nullable String... configParameterNames) {
        if (uri == null || configParameterNames == null || configParameterNames.length == 0) {
            return;
        }

        synchronized (this) {
            Map<String, ParameterDescription> enabledMap = enabledParameters.get(uri);
            if (enabledMap == null) {
                enabledMap = new LinkedHashMap<>();
                enabledParameters.put(uri, enabledMap);
            }

            ParameterDescription parameter;
            for (String configParameterName : configParameterNames) {
                if (
                    configParameterName == null ||
                    MillUtil.isBlank(configParameterName) ||
                    enabledMap.containsKey(configParameterName)
                ) {
                    continue;
                }

                parameter = ParameterDescription.typeOf(configParameterName);
                if (parameter == null) {
                    logger.warn(
                        "{} was asked to enable an unimlemented configuration parameter \"{}\"",
                        getClass().getSimpleName(),
                        configParameterName
                    );
                    continue;
                }
                enabledMap.put(configParameterName, parameter);
            }
        }
    }

    /**
     * Disables the specified configuration parameter descriptions.
     *
     * @param uid the {@link ThingUID} for which to disable configuration parameter descriptions.
     * @param configParameterNames the names/IDs of the configuration parameters whose descriptions to disable.
     */
    public void disableDescriptions(@Nullable ThingUID uid, @Nullable String... configParameterNames) {
        if (uid == null) {
            return;
        }
        disableDescriptions(URI.create(uriPrefix + uid.getAsString()), configParameterNames);
    }

    /**
     * Disables the specified configuration parameter descriptions.
     *
     * @param uri the {@link URI} identifying the {@link Thing} for which to disable parameter descriptions.
     * @param configParameterNames the names/IDs of the configuration parameters whose descriptions to disable.
     */
    @SuppressWarnings("null")
    public void disableDescriptions(@Nullable URI uri, @Nullable String... configParameterNames) {
        if (uri == null || configParameterNames == null || configParameterNames.length == 0) {
            return;
        }

        synchronized (this) {
            Map<String, ParameterDescription> enabledMap = enabledParameters.get(uri);
            Map<Locale, Map<String, ConfigDescriptionParameter>> localeMap = localizedParameters.get(uri);
            Map<String, ConfigDescriptionParameter> thingMap;
            if (enabledMap != null || localeMap != null) {
                for (String configParameterName : configParameterNames) {
                    if (configParameterName == null) {
                        continue;
                    }
                    if (enabledMap != null) {
                        enabledMap.remove(configParameterName);
                    }
                    if (localeMap != null) {
                        for (Map<String, ConfigDescriptionParameter> map : localeMap.values()) {
                            map.remove(configParameterName);
                        }
                    }
                }
                if (enabledMap != null && enabledMap.isEmpty()) {
                    enabledParameters.remove(uri);
                }
                if (localeMap != null) {
                    for (
                        Iterator<Map<String, ConfigDescriptionParameter>> iterator = localeMap.values().iterator();
                        iterator.hasNext();
                    ) {
                        thingMap = iterator.next();
                        if (thingMap.isEmpty()) {
                            iterator.remove();
                        }
                    }
                    if (localeMap.isEmpty()) {
                        localizedParameters.remove(uri);
                    }
                }
            }
        }
    }

    /**
     * Disables all configuration parameter descriptiosn for the specified {@link ThingUID}.
     *
     * @param uid the {@link ThingUID} for which to disable configuration parameter descriptions.
     */
    public void disableDescriptions(@Nullable ThingUID uid) {
        if (uid == null) {
            return;
        }
        disableDescriptions(URI.create(uriPrefix + uid.getAsString()));
    }

    /**
     * Disables all configuration parameter descriptiosn for the specified {@link ThingUID}.
     *
     * @param uri the {@link URI} identifying the {@link Thing} for which to disable parameter descriptions.
     */
    public void disableDescriptions(@Nullable URI uri) {
        if (uri == null) {
            return;
        }
        synchronized (this) {
            enabledParameters.remove(uri);
            localizedParameters.remove(uri);
        }
    }

    /**
     * Returns the specified number of parts from the specified {@link URI}.
     *
     * @param uri the {@link URI} to split into parts.
     * @param parts the number of parts to return from the beginning of the {@link URI}.
     * @return The resulting {@link String}.
     */
    protected String getURIParts(URI uri, int parts) {
        if (parts == 0) {
            return "";
        }
        int remaining = parts;
        char[] fullURI = uri.toString().toCharArray();
        for (int i = 0; i < fullURI.length - 1; i++) {
            if (fullURI[i] == ':') {
                remaining--;
                if (remaining == 0) {
                    return String.copyValueOf(fullURI, 0, i);
                }
            }
        }
        return "";
    }
}
