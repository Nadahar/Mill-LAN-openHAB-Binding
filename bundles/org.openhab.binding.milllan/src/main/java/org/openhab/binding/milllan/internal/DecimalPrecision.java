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
package org.openhab.binding.milllan.internal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;


/**
 * This enum holds all the precision constants for decimal values of this binding.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum DecimalPrecision {

    /** The ambient temperature precision constants */
    CHANNEL_AMBIENT_TEMPERATURE(MillBindingConstants.CHANNEL_AMBIENT_TEMPERATURE, Double.valueOf(0.0001), 2),

    /** The raw ambient temperature precision constants */
    CHANNEL_RAW_AMBIENT_TEMPERATURE(MillBindingConstants.CHANNEL_RAW_AMBIENT_TEMPERATURE, Double.valueOf(0.0001), 2),

    /** The set temperature precision constants */
    CHANNEL_SET_TEMPERATURE(MillBindingConstants.CHANNEL_SET_TEMPERATURE, Double.valueOf(0.0001), 2),

    /** The temperature calibration offset precision constants */
    CHANNEL_TEMPERATURE_CALIBRATION_OFFSET(
        MillBindingConstants.CHANNEL_TEMPERATURE_CALIBRATION_OFFSET, Double.valueOf(0.0001), 2
    ),

    /** The proportional gain factor precision constants */
    CONFIG_PARAM_PID_KP(MillBindingConstants.CONFIG_PARAM_PID_KP, Double.valueOf(0.0001), 2),

    /** The integral gain factor precision constants */
    CONFIG_PARAM_PID_KI(MillBindingConstants.CONFIG_PARAM_PID_KI, Double.valueOf(0.000001), 4),

    /** The derivative gain factor precision constants */
    CONFIG_PARAM_PID_KD(MillBindingConstants.CONFIG_PARAM_PID_KD, Double.valueOf(0.0001), 2),

    /** The derivative filter time coefficient precision constants */
    CONFIG_PARAM_PID_KD_FILTER_N(MillBindingConstants.CONFIG_PARAM_PID_KD_FILTER_N, Double.valueOf(0.0001), 2),

    /** The wind-up limit for the integral part precision constants */
    CONFIG_PARAM_PID_WINDUP_LIMIT_PCT(
        MillBindingConstants.CONFIG_PARAM_PID_WINDUP_LIMIT_PCT,
        Double.valueOf(0.001),
        1
    ),

    /** The hysteresis upper limit precision constants */
    CONFIG_PARAM_HYSTERESIS_UPPER(MillBindingConstants.CONFIG_PARAM_HYSTERESIS_UPPER, Double.valueOf(0.0001), 2),

    /** The hysteresis lower limit precision constants */
    CONFIG_PARAM_HYSTERESIS_LOWER(MillBindingConstants.CONFIG_PARAM_HYSTERESIS_LOWER, Double.valueOf(0.0001), 2),

    /** The commercial lock minimum temperature precision constants */
    CONFIG_PARAM_COMMERCIAL_LOCK_MIN(MillBindingConstants.CONFIG_PARAM_COMMERCIAL_LOCK_MIN, Double.valueOf(0.0001), 2),

    /** The commercial lock maximum temperature precision constants */
    CONFIG_PARAM_COMMERCIAL_LOCK_MAX(MillBindingConstants.CONFIG_PARAM_COMMERCIAL_LOCK_MAX, Double.valueOf(0.0001), 2),

    /** The open window drop temperature threshold precision constants */
    CONFIG_PARAM_OPEN_WINDOW_DROP_TEMP_THR(
        MillBindingConstants.CONFIG_PARAM_OPEN_WINDOW_DROP_TEMP_THR,
        Double.valueOf(0.0001),
        2
    ),

    /** The open window increase temperature threshold precision constants */
    CONFIG_PARAM_OPEN_WINDOW_INC_TEMP_THR(
        MillBindingConstants.CONFIG_PARAM_OPEN_WINDOW_INC_TEMP_THR,
        Double.valueOf(0.0001),
        2
    ),

    /** The precision constants for integers */
    INTEGER("Integer", Double.valueOf(0.0), 0);

    private final String name;
    private final Double delta;
    private final int roundingScale;

    private DecimalPrecision(String name, Double delta, int roundingScale) {
        this.name = name;
        this.delta = delta;
        this.roundingScale = roundingScale;
    }

    /**
     * @return The name/ID.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The delta value used for comparison/equality.
     */
    public Double getDelta() {
        return delta;
    }

    /**
     * Rounds the specified number with the predetermined precision.
     *
     * @param value the {@link Number} to round.
     * @return The rounded result.
     */
    public BigDecimal round(Number value) {
        BigDecimal bcValue = value instanceof BigDecimal ? (BigDecimal) value : MillUtil.toBigDecimal(value);
        return bcValue.setScale(roundingScale, RoundingMode.HALF_UP);
    }

    /**
     * @return The number of digits to keep after the decimal point.
     */
    public int getRoundingScale() {
        return roundingScale;
    }

    @Override
    public String toString() {
        return name + " (delta=" + delta + ", roundingScale=" + roundingScale + ")";
    }

    /**
     * Looks for a {@link DecimalPrecision} that matches the specified name/ID.
     *
     * @param name the parameter name/ID.
     * @return The matching {@link DecimalPrecision} or {@link #INTEGER} if no match was found.
     */
    public static DecimalPrecision typeOf(@Nullable String name) {
        if (name == null) {
            return INTEGER;
        }
        for (DecimalPrecision parameter : values()) {
            if (parameter.name.equals(name)) {
                return parameter;
            }
        }
        switch (name) {
            case MillBindingConstants.CHANNEL_NORMAL_SET_TEMPERATURE:
            case MillBindingConstants.CHANNEL_COMFORT_SET_TEMPERATURE:
            case MillBindingConstants.CHANNEL_SLEEP_SET_TEMPERATURE:
            case MillBindingConstants.CHANNEL_AWAY_SET_TEMPERATURE:
            case MillBindingConstants.CHANNEL_INDEPENDENT_MODE_SET_TEMPERATURE:
                return DecimalPrecision.CHANNEL_SET_TEMPERATURE;
            default:
                return INTEGER;
        }
    }
}
