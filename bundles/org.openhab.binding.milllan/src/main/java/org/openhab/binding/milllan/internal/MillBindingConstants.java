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

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.semantics.Property;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.type.ThingType;


/**
 * The {@link MillBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillBindingConstants {

    /** The binding ID */
    public static final String BINDING_ID = "milllan";

    // List of all Thing Type UIDs

    /** The {@code Panel Heater} {@link ThingTypeUID} */
    public static final ThingTypeUID THING_TYPE_PANEL_HEATER = new ThingTypeUID(BINDING_ID, "panel-heater");

    /** The {@code Convection Heater} {@link ThingTypeUID} */
    public static final ThingTypeUID THING_TYPE_CONVECTION_HEATER = new ThingTypeUID(BINDING_ID, "convection-heater");

    /** The {@code Oil Heater} {@link ThingTypeUID} */
    public static final ThingTypeUID THING_TYPE_OIL_HEATER = new ThingTypeUID(BINDING_ID, "oil-heater");

    /** The {@code Wi-Fi Socket} {@link ThingTypeUID} */
    public static final ThingTypeUID THING_TYPE_WIFI_SOCKET = new ThingTypeUID(BINDING_ID, "wifi-socket");

    /** The {@code All Functions} {@link ThingTypeUID} */
    public static final ThingTypeUID THING_TYPE_ALL_FUNCTIONS = new ThingTypeUID(BINDING_ID, "all-functions");

    /** The {@link Set} of supported {@link ThingType}s */
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(
        THING_TYPE_PANEL_HEATER,
        THING_TYPE_CONVECTION_HEATER,
        THING_TYPE_OIL_HEATER,
        THING_TYPE_WIFI_SOCKET,
        THING_TYPE_ALL_FUNCTIONS
    );

    // mDNS property names

    /** The {@code mDNS} {@code id} property key */
    public static final String MDNS_PROPERTY_ID = "id";

    /** The {@code mDNS} {@code name} property key */
    public static final String MDNS_PROPERTY_NAME = "name";

    // List of all Channel IDs

    /** The Ambient Temperature {@link Channel} */
    public static final String CHANNEL_AMBIENT_TEMPERATURE = "ambient-temperature";

    /** The Raw Ambient Temperature {@link Channel} */
    public static final String CHANNEL_RAW_AMBIENT_TEMPERATURE = "raw-ambient-temperature";

    /** The Current Power {@link Channel} */
    public static final String CHANNEL_CURRENT_POWER = "current-power";

    /** The Control Signal {@link Channel} */
    public static final String CHANNEL_CONTROL_SIGNAL = "control-signal";

    /** The Lock Status {@link Channel} */
    public static final String CHANNEL_LOCK_STATUS = "lock-status";

    /** The Open Window Status {@link Channel} */
    public static final String CHANNEL_OPEN_WINDOW_STATUS = "open-window-status";

    /** The Set Temperature {@link Channel} */
    public static final String CHANNEL_SET_TEMPERATURE = "set-temperature";

    /** The Connected to Cloud {@link Channel} */
    public static final String CHANNEL_CONNECTED_CLOUD = "connected-to-cloud";

    /** The Operation Mode {@link Channel} */
    public static final String CHANNEL_OPERATION_MODE = "operation-mode";

    /** The Temperature Calibration Offset {@link Channel} */
    public static final String CHANNEL_TEMPERATURE_CALIBRATION_OFFSET = "temperature-calibration-offset";

    /** The Commercial Lock {@link Channel} */
    public static final String CHANNEL_COMMERCIAL_LOCK = "commercial-lock";

    /** The Child Lock {@link Channel} */
    public static final String CHANNEL_CHILD_LOCK = "child-lock";

    /** The Display Unit {@link Channel} */
    public static final String CHANNEL_DISPLAY_UNIT = "display-unit";

    /** The Normal Set Temperature {@link Channel} */
    public static final String CHANNEL_NORMAL_SET_TEMPERATURE = "normal-set-temperature";

    /** The Comfort Set Temperature {@link Channel} */
    public static final String CHANNEL_COMFORT_SET_TEMPERATURE = "comfort-set-temperature";

    /** The Sleep Set Temperature {@link Channel} */
    public static final String CHANNEL_SLEEP_SET_TEMPERATURE = "sleep-set-temperature";

    /** The Away Set Temperature {@link Channel} */
    public static final String CHANNEL_AWAY_SET_TEMPERATURE = "away-set-temperature";

    /** The Limited Heating Power {@link Channel} */
    public static final String CHANNEL_LIMITED_HEATING_POWER = "limited-heating-power";

    /** The Controller Type {@link Channel} */
    public static final String CHANNEL_CONTROLLER_TYPE = "controller-type";

    /** The Predictive Heating Type {@link Channel} */
    public static final String CHANNEL_PREDICTIVE_HEATING_TYPE = "predictive-heating-type";

    /** The Oil Heater Power {@link Channel} */
    public static final String CHANNEL_OIL_HEATER_POWER = "oil-heater-power";

    /** The Open Window Active {@link Channel} */
    public static final String CHANNEL_OPEN_WINDOW_ACTIVE = "open-window-active";

    /** The Open Window Enabled {@link Channel} */
    public static final String CHANNEL_OPEN_WINDOW_ENABLED = "open-window-enabled";

    // Property constants

    /** The {@code name} {@link Property} */
    public static final String PROPERTY_NAME = "name";

    /** The {@code customName} {@link Property} */
    public static final String PROPERTY_CUSTOM_NAME = "customName";

    /** The {@code operationKey} {@link Property} */
    public static final String PROPERTY_OPERATION_KEY = "operationKey";

    /** The {@link Set} of dynamic {@link Property} constants */
    public static final Set<String> PROPERTIES_DYNAMIC = Set.of(
        PROPERTY_NAME, PROPERTY_CUSTOM_NAME, Thing.PROPERTY_FIRMWARE_VERSION, PROPERTY_OPERATION_KEY
    );

    // Configuration parameter constants

    /** The hostname or IP address configuration parameter */
    public static final String CONFIG_PARAM_HOSTNAME = "hostname";

    /** The API key configuration parameter */
    public static final String CONFIG_PARAM_API_KEY = "apiKey";

    /** The refresh interval configuration parameter */
    public static final String CONFIG_PARAM_REFRESH_INTERVAL = "refreshInterval";

    /** The infrequent refresh interval configuration parameter */
    public static final String CONFIG_PARAM_INFREQUENT_REFRESH_INTERVAL = "infrequentRefreshInterval";

    /** The time zone offset configuration parameter */
    public static final String CONFIG_PARAM_TIMEZONE_OFFSET = "timeZoneOffset";

    /** The proportional gain factor configuration parameter */
    public static final String CONFIG_PARAM_PID_KP = "pidKp";

    /** The integral gain factor configuration parameter */
    public static final String CONFIG_PARAM_PID_KI = "pidKi";

    /** The derivative gain factor configuration parameter */
    public static final String CONFIG_PARAM_PID_KD = "pidKd";

    /** The derivative filter time coefficient configuration parameter */
    public static final String CONFIG_PARAM_PID_KD_FILTER_N = "pidKdFilterN";

    /** The wind-up limit for the integral part from 0 to 100 configuration parameter */
    public static final String CONFIG_PARAM_PID_WINDUP_LIMIT_PCT = "pidWindupLimitPct";

    /** The cloud communication configuration parameter */
    public static final String CONFIG_PARAM_CLOUD_COMMUNICATION = "cloudCommunication";

    /** The hysteresis upper limit configuration parameter */
    public static final String CONFIG_PARAM_HYSTERESIS_UPPER = "hysteresisUpper";

    /** The hysteresis lower limit configuration parameter */
    public static final String CONFIG_PARAM_HYSTERESIS_LOWER = "hysteresisLower";

    /** The commercial lock minimum temperature configuration parameter */
    public static final String CONFIG_PARAM_COMMERCIAL_LOCK_MIN = "commercialLockMin";

    /** The commercial lock maximum temperature configuration parameter */
    public static final String CONFIG_PARAM_COMMERCIAL_LOCK_MAX = "commercialLockMax";

    /** The open window drop temperature threshold configuration parameter */
    public static final String CONFIG_PARAM_OPEN_WINDOW_DROP_TEMP_THR = "openWindowDropTemperatureThreshold";

    /** The open window drop time range configuration parameter */
    public static final String CONFIG_PARAM_OPEN_WINDOW_DROP_TIME_RANGE = "openWindowDropTimeRange";

    /** The open window increase temperature threshold configuration parameter */
    public static final String CONFIG_PARAM_OPEN_WINDOW_INC_TEMP_THR = "openWindowIncreaseTemperatureThreshold";

    /** The open window increase time range configuration parameter */
    public static final String CONFIG_PARAM_OPEN_WINDOW_INC_TIME_RANGE = "openWindowIncreaseTimeRange";

    /** The open window maximum time configuration parameter */
    public static final String CONFIG_PARAM_OPEN_WINDOW_MAX_TIME = "openWindowMaxTime";

    /** The {@link Set} of dynamic configuration parameters */
    public static final Set<String> CONFIG_DYNAMIC_PARAMETERS = Set.of(
        CONFIG_PARAM_TIMEZONE_OFFSET, CONFIG_PARAM_PID_KP, CONFIG_PARAM_PID_KI, CONFIG_PARAM_PID_KD,
        CONFIG_PARAM_PID_KD_FILTER_N, CONFIG_PARAM_PID_WINDUP_LIMIT_PCT, CONFIG_PARAM_CLOUD_COMMUNICATION,
        CONFIG_PARAM_HYSTERESIS_UPPER, CONFIG_PARAM_HYSTERESIS_LOWER, CONFIG_PARAM_COMMERCIAL_LOCK_MIN,
        CONFIG_PARAM_COMMERCIAL_LOCK_MAX, CONFIG_PARAM_OPEN_WINDOW_DROP_TEMP_THR,
        CONFIG_PARAM_OPEN_WINDOW_DROP_TIME_RANGE, CONFIG_PARAM_OPEN_WINDOW_INC_TEMP_THR,
        CONFIG_PARAM_OPEN_WINDOW_INC_TIME_RANGE, CONFIG_PARAM_OPEN_WINDOW_MAX_TIME
    );

    private MillBindingConstants() {
        // Not to be instantiated
    }
}
