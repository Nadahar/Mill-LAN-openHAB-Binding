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
 * This enum holds all the parameter descriptions for the "dynamic parameters" of this binding.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum ParameterDescription {

    /** The time zone offset configuration parameter */
    CONFIG_PARAM_TIMEZONE_OFFSET(
        MillBindingConstants.CONFIG_PARAM_TIMEZONE_OFFSET,
        "thing-general.config.milllan.timezone-offset.label",
        "Time Zone Offset",
        "thing-general.config.milllan.timezone-offset.description",
        "The time zone offset from UTC in minutes."
    ),

    /** The proportional gain factor configuration parameter */
    CONFIG_PARAM_PID_KP(
        MillBindingConstants.CONFIG_PARAM_PID_KP,
        "",
        "Kp",
        "thing-general.config.milllan.pid-kp.description",
        "The PID controller's proportional gain factor K<sub>p</sub>."
    ),

    /** The integral gain factor configuration parameter */
    CONFIG_PARAM_PID_KI(
        MillBindingConstants.CONFIG_PARAM_PID_KI,
        "",
        "Ki",
        "thing-general.config.milllan.pid-ki.description",
        "The PID controller's integral gain factor K<sub>i</sub>."
    ),

    /** The derivative gain factor configuration parameter */
    CONFIG_PARAM_PID_KD(
        MillBindingConstants.CONFIG_PARAM_PID_KD,
        "",
        "Kd",
        "thing-general.config.milllan.pid-kd.description",
        "The PID controller's derivative gain factor K<sub>d</sub>."
    ),

    /** The derivative filter time coefficient configuration parameter */
    CONFIG_PARAM_PID_KD_FILTER_N(
        MillBindingConstants.CONFIG_PARAM_PID_KD_FILTER_N,
        "thing-general.config.milllan.pid-kd-filter.label",
        "Kd Filter",
        "thing-general.config.milllan.pid-kd-filter.description",
        "The PID controller's derivative (K<sub>d</sub>) filter time coefficient."
    ),

    /** The wind-up limit for the integral part from 0 to 100 configuration parameter */
    CONFIG_PARAM_PID_WINDUP_LIMIT_PCT(
        MillBindingConstants.CONFIG_PARAM_PID_WINDUP_LIMIT_PCT,
        "thing-general.config.milllan.pid-kd-wind-up.label",
        "Ki Wind-up Limit",
        "thing-general.config.milllan.pid-kd-wind-up.description",
        "The PID controller's wind-up limit for integral part (K<sub>i</sub>) in percent (0 to 100)."
    ),

    /** The cloud communication configuration parameter */
    CONFIG_PARAM_CLOUD_COMMUNICATION(
        MillBindingConstants.CONFIG_PARAM_CLOUD_COMMUNICATION,
        "thing-general.config.milllan.cloud-communication.label",
        "Enable Cloud Communication",
        "thing-general.config.milllan.cloud-communication.description",
        "Whether cloud communication is enabled in the device. Changing this will reboot the device."
    ),

    /** The hysteresis upper limit configuration parameter */
    CONFIG_PARAM_HYSTERESIS_UPPER(
        MillBindingConstants.CONFIG_PARAM_HYSTERESIS_UPPER,
        "thing-general.config.milllan.hysteresis-upper.label",
        "Hysteresis Upper",
        "thing-general.config.milllan.hysteresis-upper.description",
        "The upper limit: Set temperature + upper limit = stop heating."
    ),

    /** The hysteresis lower limit configuration parameter */
    CONFIG_PARAM_HYSTERESIS_LOWER(
        MillBindingConstants.CONFIG_PARAM_HYSTERESIS_LOWER,
        "thing-general.config.milllan.hysteresis-lower.label",
        "Hysteresis Lower",
        "thing-general.config.milllan.hysteresis-lower.description",
        "The lower limit: Set temperature - lower limit = start heating."
    ),

    /** The commercial lock minimum temperature configuration parameter */
    CONFIG_PARAM_COMMERCIAL_LOCK_MIN(
        MillBindingConstants.CONFIG_PARAM_COMMERCIAL_LOCK_MIN,
        "thing-general.config.milllan.commercial-lock-min.label",
        "Commercial Lock Minimum Temperature",
        "thing-general.config.milllan.commercial-lock-min.description",
        "The minimum temperature that can be set while the commercial lock is enabled."
    ),

    /** The commercial lock maximum temperature configuration parameter */
    CONFIG_PARAM_COMMERCIAL_LOCK_MAX(
        MillBindingConstants.CONFIG_PARAM_COMMERCIAL_LOCK_MAX,
        "thing-general.config.milllan.commercial-lock-max.label",
        "Commercial Lock Maximum Temperature",
        "thing-general.config.milllan.commercial-lock-max.description",
        "The maximum temperature that can be set while the commercial lock is enabled."
    ),

    /** The open window drop temperature threshold configuration parameter */
    CONFIG_PARAM_OPEN_WINDOW_DROP_TEMP_THR(
        MillBindingConstants.CONFIG_PARAM_OPEN_WINDOW_DROP_TEMP_THR,
        "thing-general.config.milllan.open-window-drop-temp-thr.label",
        "Drop Temperature Threshold",
        "thing-general.config.milllan.open-window-drop-temp-thr.description",
        "The temperature drop required to trigger (activate) the open window function."
    ),

    /** The open window drop time range configuration parameter */
    CONFIG_PARAM_OPEN_WINDOW_DROP_TIME_RANGE(
        MillBindingConstants.CONFIG_PARAM_OPEN_WINDOW_DROP_TIME_RANGE,
        "thing-general.config.milllan.open-window-drop-time-range.label",
        "Drop Time Range",
        "thing-general.config.milllan.open-window-drop-time-range.description",
        "The time range for which a drop in temperature will be evaluated."
    ),

    /** The open window increase temperature threshold configuration parameter */
    CONFIG_PARAM_OPEN_WINDOW_INC_TEMP_THR(
        MillBindingConstants.CONFIG_PARAM_OPEN_WINDOW_INC_TEMP_THR,
        "thing-general.config.milllan.open-window-inc-temp-thr.label",
        "Increase Temperature Threshold",
        "thing-general.config.milllan.open-window-inc-temp-thr.description",
        "The temperature increase required to deactivate the open window function."
    ),

    /** The open window increase time range configuration parameter */
    CONFIG_PARAM_OPEN_WINDOW_INC_TIME_RANGE(
        MillBindingConstants.CONFIG_PARAM_OPEN_WINDOW_INC_TIME_RANGE,
        "thing-general.config.milllan.open-window-inc-time-range.label",
        "Increase Time Range",
        "thing-general.config.milllan.open-window-inc-time-range.description",
        "The time range for which an increase in temperature will be evaluated."
    ),

    /** The open window maximum time configuration parameter */
    CONFIG_PARAM_OPEN_WINDOW_MAX_TIME(
        MillBindingConstants.CONFIG_PARAM_OPEN_WINDOW_MAX_TIME,
        "thing-general.config.milllan.open-window-max-time.label",
        "Maximum Time",
        "thing-general.config.milllan.open-window-max-time.description",
        "The maximum time the open window function will remain active."
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

    /**
     * Builds and returns a localized {@link ConfigDescriptionParameter} for this {@link ParameterDescription}.
     * If either of the parameters are {@code null}, the result won't be localized.
     *
     * @param bundle the {@link Bundle} to use when retrieving localized strings.
     * @param provider the {@link TranslationProvider} to use when retrieving localized strings.
     * @param locale the {@link Locale} to localize for.
     * @return The resulting {@link ConfigDescriptionParameter}.
     * @throws IllegalStateException If there's a problem with this enum.
     */
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
            case CONFIG_PARAM_PID_KP:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withStepSize(BigDecimal.valueOf(1L))
                    .withGroupName("pid").withAdvanced(true).withDefault("70").withLabel(defaultLabel);

                if (bundle == null || provider == null || locale == null) {
                    builder.withDescription(defaultDescr);
                } else {
                    builder.withDescription(provider.getText(bundle, descrKey, defaultDescr, locale));
                }
                return builder.build();
            case CONFIG_PARAM_PID_KI:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withStepSize(BigDecimal.valueOf(0.01))
                    .withGroupName("pid").withAdvanced(true).withDefault("0.02").withLabel(defaultLabel);
                if (bundle == null || provider == null || locale == null) {
                    builder.withDescription(defaultDescr);
                } else {
                    builder.withDescription(provider.getText(bundle, descrKey, defaultDescr, locale));
                }
                return builder.build();
            case CONFIG_PARAM_PID_KD:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withStepSize(BigDecimal.valueOf(1L))
                    .withGroupName("pid").withAdvanced(true).withDefault("4500").withLabel(defaultLabel);
                if (bundle == null || provider == null || locale == null) {
                    builder.withDescription(defaultDescr);
                } else {
                    builder.withDescription(provider.getText(bundle, descrKey, defaultDescr, locale));
                }
                return builder.build();
            case CONFIG_PARAM_PID_KD_FILTER_N:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withStepSize(BigDecimal.valueOf(1L))
                    .withGroupName("pid").withAdvanced(true).withDefault("60");
                break;
            case CONFIG_PARAM_PID_WINDUP_LIMIT_PCT:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withMaximum(BigDecimal.valueOf(100L))
                    .withStepSize(BigDecimal.valueOf(1L)).withUnit("%")
                    .withGroupName("pid").withAdvanced(true).withDefault("95");
                break;
            case CONFIG_PARAM_CLOUD_COMMUNICATION:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.BOOLEAN)
                    .withGroupName("general").withAdvanced(true).withDefault("false");
                break;
            case CONFIG_PARAM_HYSTERESIS_UPPER:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withMaximum(BigDecimal.valueOf(5L))
                    .withStepSize(BigDecimal.valueOf(0.05)).withGroupName("hysteresis")
                    .withAdvanced(true).withDefault("1");
                break;
            case CONFIG_PARAM_HYSTERESIS_LOWER:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withMaximum(BigDecimal.valueOf(5L))
                    .withStepSize(BigDecimal.valueOf(0.05)).withGroupName("hysteresis")
                    .withAdvanced(true).withDefault("0.5");
                break;
            case CONFIG_PARAM_COMMERCIAL_LOCK_MIN:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withMaximum(BigDecimal.valueOf(99L))
                    .withStepSize(BigDecimal.valueOf(0.5)).withGroupName("commercialLock")
                    .withAdvanced(true).withDefault("21");
                break;
            case CONFIG_PARAM_COMMERCIAL_LOCK_MAX:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withMaximum(BigDecimal.valueOf(99L))
                    .withStepSize(BigDecimal.valueOf(0.5)).withGroupName("commercialLock")
                    .withAdvanced(true).withDefault("23");
                break;
            case CONFIG_PARAM_OPEN_WINDOW_DROP_TEMP_THR:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withMaximum(BigDecimal.valueOf(99L)).withUnit("Cel")
                    .withStepSize(BigDecimal.valueOf(0.25)).withGroupName("openWindowFunction")
                    .withAdvanced(true).withDefault("5");
                break;
            case CONFIG_PARAM_OPEN_WINDOW_DROP_TIME_RANGE:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.INTEGER)
                    .withMinimum(BigDecimal.valueOf(0L)).withUnit("s")
                    .withStepSize(BigDecimal.valueOf(1)).withGroupName("openWindowFunction")
                    .withAdvanced(true).withDefault("900");
                break;
            case CONFIG_PARAM_OPEN_WINDOW_INC_TEMP_THR:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.DECIMAL)
                    .withMinimum(BigDecimal.valueOf(0L)).withMaximum(BigDecimal.valueOf(99L)).withUnit("Cel")
                    .withStepSize(BigDecimal.valueOf(0.25)).withGroupName("openWindowFunction")
                    .withAdvanced(true).withDefault("3");
                break;
            case CONFIG_PARAM_OPEN_WINDOW_INC_TIME_RANGE:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.INTEGER)
                    .withMinimum(BigDecimal.valueOf(0L)).withUnit("s")
                    .withStepSize(BigDecimal.valueOf(1)).withGroupName("openWindowFunction")
                    .withAdvanced(true).withDefault("900");
                break;
            case CONFIG_PARAM_OPEN_WINDOW_MAX_TIME:
                builder = ConfigDescriptionParameterBuilder.create(name, Type.INTEGER)
                    .withMinimum(BigDecimal.valueOf(0L)).withUnit("s")
                    .withStepSize(BigDecimal.valueOf(1)).withGroupName("openWindowFunction")
                    .withAdvanced(true).withDefault("3600");
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

    /**
     * @return The parameter name/ID.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Looks for a {@link ParameterDescription} that matches the specified name/ID.
     *
     * @param name the parameter name/ID.
     * @return The matching {@link ParameterDescription} or {@code null} if no match was found.
     */
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
