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
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link MillBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillBindingConstants { // TODO: (Nad) JAvaDocs

    public static final String BINDING_ID = "milllan";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_PANEL_HEATER = new ThingTypeUID(BINDING_ID, "panel-heater");
    public static final ThingTypeUID THING_TYPE_ALL_FUNCTIONS = new ThingTypeUID(BINDING_ID, "all-functions");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(
        THING_TYPE_PANEL_HEATER,
        THING_TYPE_ALL_FUNCTIONS
    );

    // List of all Channel ids
    public static final String AMBIENT_TEMPERATURE = "ambient-temperature";
    public static final String RAW_AMBIENT_TEMPERATURE = "raw-ambient-temperature";
    public static final String CURRENT_POWER = "current-power";
    public static final String CONTROL_SIGNAL = "control-signal";
    public static final String LOCK_STATUS = "lock-status";
    public static final String OPEN_WINDOW_STATUS = "open-window-status";
    public static final String SET_TEMPERATURE = "set-temperature";
    public static final String CONNECTED_CLOUD = "connected-to-cloud";
    public static final String OPERATION_MODE = "operation-mode";
    public static final String TEMPERATURE_CALIBRATION_OFFSET = "temperature-calibration-offset";
    public static final String COMMERCIAL_LOCK = "commercial-lock";
    public static final String CHILD_LOCK = "child-lock";
    public static final String DISPLAY_UNIT = "display-unit";
    public static final String NORMAL_SET_TEMPERATURE = "normal-set-temperature";
    public static final String COMFORT_SET_TEMPERATURE = "comfort-set-temperature";
    public static final String SLEEP_SET_TEMPERATURE = "sleep-set-temperature";
    public static final String AWAY_SET_TEMPERATURE = "away-set-temperature";
    public static final String LIMITED_HEATING_POWER = "limited-heating-power";
    public static final String CONTROLLER_TYPE = "controller-type";
    public static final String PREDICTIVE_HEATING_TYPE = "predictive-heating-type";
    public static final String OIL_HEATER_POWER = "oil-heater-power";

    // Propery constants
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_CUSTOM_NAME = "customName";
    public static final String PROPERTY_OPERATION_KEY = "operationKey";

    public static final Set<String> PROPERTIES_DYNAMIC = Set.of(
        PROPERTY_NAME, PROPERTY_CUSTOM_NAME, Thing.PROPERTY_FIRMWARE_VERSION, PROPERTY_OPERATION_KEY
    );

    // Configuration parameter constants
    public static final String CONFIG_PARAM_HOSTNAME = "hostname";
    public static final String CONFIG_PARAM_API_KEY = "apiKey";
    public static final String CONFIG_PARAM_REFRESH_INTERVAL = "refreshInterval";
    public static final String CONFIG_PARAM_INFREQUENT_REFRESH_INTERVAL = "infrequentRefreshInterval";
    public static final String CONFIG_PARAM_TIMEZONE_OFFSET = "timeZoneOffset";

    public static final Set<String> CONFIG_DYNAMIC_PARAMETERS = Set.of(
        CONFIG_PARAM_TIMEZONE_OFFSET
    );

    private MillBindingConstants() {
        // Not to be instantiated
    }
}
