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

    private MillBindingConstants() {
        // Not to be instantiated
    }
}
