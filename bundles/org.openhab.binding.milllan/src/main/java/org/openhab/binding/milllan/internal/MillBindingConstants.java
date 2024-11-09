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
import org.openhab.core.thing.Channel;
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

    // Configuration parameter constants

    /** The hostname or IP address configuration parameter */
    public static final String CONFIG_PARAM_HOSTNAME = "hostname";

    /** The refresh interval configuration parameter */
    public static final String CONFIG_PARAM_REFRESH_INTERVAL = "refreshInterval";

    private MillBindingConstants() {
        // Not to be instantiated
    }
}
