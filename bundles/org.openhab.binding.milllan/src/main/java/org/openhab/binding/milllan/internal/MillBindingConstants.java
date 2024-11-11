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

    /** The {@code All Functions} {@link ThingTypeUID} */
    public static final ThingTypeUID THING_TYPE_ALL_FUNCTIONS = new ThingTypeUID(BINDING_ID, "all-functions");

    /** The {@link Set} of supported {@link ThingType}s */
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(
        THING_TYPE_PANEL_HEATER,
        THING_TYPE_ALL_FUNCTIONS
    );

    // List of all Channel IDs

    /** The Ambient Temperature {@link Channel} */
    public static final String AMBIENT_TEMPERATURE = "ambient-temperature";

    /** The Raw Ambient Temperature {@link Channel} */
    public static final String RAW_AMBIENT_TEMPERATURE = "raw-ambient-temperature";

    /** The Current Power {@link Channel} */
    public static final String CURRENT_POWER = "current-power";

    /** The Control Signal {@link Channel} */
    public static final String CONTROL_SIGNAL = "control-signal";

    /** The Lock Status {@link Channel} */
    public static final String LOCK_STATUS = "lock-status";

    /** The Open Window Status {@link Channel} */
    public static final String OPEN_WINDOW_STATUS = "open-window-status";

    /** The Set Temperature {@link Channel} */
    public static final String SET_TEMPERATURE = "set-temperature";

    /** The Connected to Cloud {@link Channel} */
    public static final String CONNECTED_CLOUD = "connected-to-cloud";

    /** The Operation Mode {@link Channel} */
    public static final String OPERATION_MODE = "operation-mode";

    /** The Temperature Calibration Offset {@link Channel} */
    public static final String TEMPERATURE_CALIBRATION_OFFSET = "temperature-calibration-offset";

    /** The Commercial Lock {@link Channel} */
    public static final String COMMERCIAL_LOCK = "commercial-lock";

    /** The Child Lock {@link Channel} */
    public static final String CHILD_LOCK = "child-lock";

    /** The Normal Set Temperature {@link Channel} */
    public static final String NORMAL_SET_TEMPERATURE = "normal-set-temperature";

    /** The Comfort Set Temperature {@link Channel} */
    public static final String COMFORT_SET_TEMPERATURE = "comfort-set-temperature";

    /** The Sleep Set Temperature {@link Channel} */
    public static final String SLEEP_SET_TEMPERATURE = "sleep-set-temperature";

    /** The Away Set Temperature {@link Channel} */
    public static final String AWAY_SET_TEMPERATURE = "away-set-temperature";

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

    private MillBindingConstants() {
        // Not to be instantiated
    }
}
