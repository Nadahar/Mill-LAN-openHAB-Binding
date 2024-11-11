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
package org.openhab.binding.milllan.internal.api;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.annotations.SerializedName;


/**
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum TemperatureType { // TODO: (Nad) JavaDocs

    @SerializedName("Off")
    OFF("The set-temperature is fixed at zero."),

    @SerializedName("Normal")
    NORMAL("The set-temperature used for \"Independent device\" and timers."),

    @SerializedName("Comfort")
    COMFORT("The set-temperature used in \"Comfort\" mode."),

    @SerializedName("Sleep")
    SLEEP("The set-temperature used in \"Sleep\" mode."),

    @SerializedName("Away")
    AWAY("The set-temperature used in \"Away\" mode."),

    @SerializedName("AlwaysHeating")
    ALWAYS_HEATING("The device always heats.");

    private final String description;

    private TemperatureType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
