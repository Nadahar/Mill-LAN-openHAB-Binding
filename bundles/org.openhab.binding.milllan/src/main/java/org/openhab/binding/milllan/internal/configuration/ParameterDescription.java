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

import java.math.BigDecimal;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.MillBindingConstants;
import org.openhab.core.config.core.ConfigDescriptionParameter;
import org.openhab.core.config.core.ConfigDescriptionParameterBuilder;
import org.openhab.core.config.core.ConfigDescriptionParameter.Type;
import org.openhab.core.i18n.TranslationProvider;
import org.osgi.framework.Bundle;


/**
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum ParameterDescription { // TODO: (Nad) JavaDocs
    CONFIG_PARAM_TIMEZONE_OFFSET(
        MillBindingConstants.CONFIG_PARAM_TIMEZONE_OFFSET,
        "thing-general.config.milllan.timezone-offset.label",
        "Time Zone Offset",
        "thing-general.config.milllan.timezone-offset.description",
        "The time zone offset from UTC in minutes."
    );

    private final String name;

    private final String labelKey;
    private final String defaultLabel;
    private final String descrKey;
    private final String defaultDescr;

    private ParameterDescription(
        String name,
        String labelKey,
        String defaultLabel,
        String descrKey,
        String defaultDescr
    ) {
        this.name = name;
        this.labelKey = labelKey;
        this.defaultLabel = defaultLabel;
        this.descrKey = descrKey;
        this.defaultDescr = defaultDescr;
    }

    public ConfigDescriptionParameter getConfigDescriptionParameter(
        @Nullable Bundle bundle,
        @Nullable TranslationProvider provider,
        @Nullable Locale locale
    ) {
        ConfigDescriptionParameterBuilder builder;
        switch (this) {
            case CONFIG_PARAM_TIMEZONE_OFFSET:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.INTEGER)
                    .withUnit("min").withMinimum(BigDecimal.valueOf(-840L))
                    .withMaximum(BigDecimal.valueOf(720L)).withStepSize(BigDecimal.valueOf(15L))
                    .withGroupName("general").withAdvanced(true).withDefault("0");
                break;
            default:
                throw new IllegalStateException("Unimplemented config description parameter: " + name());

        }
        if (bundle == null || provider == null || locale == null) {
            builder.withLabel(defaultLabel).withDescription(defaultDescr);
        } else {
            builder.withLabel(provider.getText(bundle, labelKey, defaultLabel, locale));
            builder.withDescription(provider.getText(bundle, descrKey, defaultDescr, locale));
        }
        return builder.build();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Nullable
    public static ParameterDescription typeOf(@Nullable String name) {
        if (name == null) {
            return null;
        }
        for (ParameterDescription parameter : values()) {
            if (parameter.name.equals(name)) {
                return parameter;
            }
        }
        return null;
    }
}
