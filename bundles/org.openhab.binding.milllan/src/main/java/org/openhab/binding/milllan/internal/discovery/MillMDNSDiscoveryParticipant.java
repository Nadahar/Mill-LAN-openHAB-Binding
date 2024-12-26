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
package org.openhab.binding.milllan.internal.discovery;

import static org.openhab.binding.milllan.internal.MillBindingConstants.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.openhab.core.config.discovery.mdns.internal.MDNSDiscoveryService;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.type.ThingType;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This {@link MDNSDiscoveryParticipant} registers with the {@link MDNSDiscoveryService} and
 * handles the discovery of supported devices via mDNS.
 *
 * @author Nadahar - Initial contribution
 */
@Component(configurationPid = "discovery.milllan")
@NonNullByDefault
public class MillMDNSDiscoveryParticipant implements MDNSDiscoveryParticipant {

    private static final String SERVICE_TYPE = "_mill._tcp.local.";

    /** The {@link Set} of discoverable {@link ThingType}s */
    public static final Set<ThingTypeUID> DISCOVERABLE_THING_TYPES_UIDS = Set.of(
        THING_TYPE_PANEL_HEATER,
        THING_TYPE_CONVECTION_HEATER,
        THING_TYPE_OIL_HEATER,
        THING_TYPE_WIFI_SOCKET
    );

    private final Logger logger = LoggerFactory.getLogger(MillMDNSDiscoveryParticipant.class);

    private final Bundle bundle;

    private final TranslationProvider i18nProvider;

    private final LocaleProvider localeProvider;

    /**
     * Creates a new instance using the specified parameters.
     *
     * @param componentContext the {@link ComponentContext}.
     * @param i18nProvider the {@link TranslationProvider}.
     * @param localeProvider the {@link LocaleProvider}.
     */
    @Activate
    public MillMDNSDiscoveryParticipant(
        ComponentContext componentContext,
        @Reference TranslationProvider i18nProvider,
        @Reference LocaleProvider localeProvider
    ) {
        this.i18nProvider = i18nProvider;
        this.localeProvider = localeProvider;
        this.bundle = componentContext.getBundleContext().getBundle();
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return DISCOVERABLE_THING_TYPES_UIDS;
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public @Nullable DiscoveryResult createResult(ServiceInfo service) {
        ThingUID uid = getThingUID(service);
        if (uid != null) {
            String label = i18nProvider.getText(
                bundle,
                "discovery.milllan.panel-heater.label",
                "Mill Panel Heater",
                localeProvider.getLocale()
            );
            DiscoveryResultBuilder builder = DiscoveryResultBuilder.create(uid)
                .withLabel(label).withProperty(CONFIG_PARAM_HOSTNAME, resolveIPAddress(service.getInetAddresses()))
                .withProperty(Thing.PROPERTY_MAC_ADDRESS, formatMACAddress(uid.getId()))
                .withRepresentationProperty(Thing.PROPERTY_MAC_ADDRESS);
            return builder.build();
        }
        return null;
    }

    @Override
    public @Nullable ThingUID getThingUID(ServiceInfo service) {
        if (service.getInetAddresses().length == 0) {
            return null;
        }

        /*
         * There is no documentation regarding what properties the devices will broadcast, so this
         * logic must be adapted as devices are tested. So far, these properties have been seen:
         *
         * id = "mill_modular_<MAC ADDRESS WITHOUT SEPARATORS>", name = "Panel Heater Modular"
         * id = "mill_panel_heater_<MAC ADDRESS WITHOUT SEPARATORS>", name = "panel heater"
         */
        String id = service.getPropertyString(MDNS_PROPERTY_ID);
        String name = service.getPropertyString(MDNS_PROPERTY_NAME);
        if (name != null) {
            name = name.toLowerCase(Locale.ROOT);
        }
        if (id != null && id.startsWith("mill_") && name != null && name.contains("panel heater")) {
            String[] parts = id.split("_");
            if (parts.length > 0 && parts[parts.length - 1].length() == 12) {
                return new ThingUID(THING_TYPE_PANEL_HEATER, parts[parts.length - 1]);
            }
        }
        logger.warn(
            "Mill LAN discovered an unrecognized Mill device. The details should be reported to the binding " +
            "developer so that it can be recognized in the future. id=\"{}\", name=\"{}\", ip addresses=\"{}\"",
            id,
            name,
            service.getInetAddresses()
        );
        return null;
    }

    /**
     * Tries to find a reachable IP address among the supplied addresses.
     *
     * @param addresses the array of {@link InetAddress}es to look through.
     * @return The resulting IP address or an empty {@link String} if none were supplied.
     */
    protected String resolveIPAddress(InetAddress[] addresses) {
        for (InetAddress address : addresses) {
            try {
                if (address.isReachable(1000)) {
                    return address.getHostAddress();
                }
            } catch (IOException e) {
                // Move on
            }
        }
        return addresses.length > 0 ? addresses[0].getHostAddress() : "";
    }

    /**
     * Formats the MAC address found in the {@code id} field of the {@link ServiceInfo} in the same way
     * that it is formatted when returned by the device's API, to make the two comparable.
     *
     * @param unformatted the unformatted MAC address.
     * @return The formatted MAC address.
     */
    protected String formatMACAddress(String unformatted) {
        char[] c = unformatted.toUpperCase(Locale.ROOT).toCharArray();
        StringBuilder sb = new StringBuilder(17);
        for (int i = 0; i < c.length - 1; i += 2) {
            sb.append(c, i, 2);
            if (i < c.length - 2) {
                sb.append(':');
            }
        }
        return sb.toString();
    }
}
