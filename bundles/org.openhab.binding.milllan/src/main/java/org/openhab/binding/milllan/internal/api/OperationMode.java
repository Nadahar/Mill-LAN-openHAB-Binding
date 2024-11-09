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
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.MillUtil;

import com.google.gson.annotations.SerializedName;


/**
 * This enum represents the device API's {@code EOprationMode}.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum OperationMode {

    /**
     * The device is in off mode, it is not possible to send any comands to the device. The device doesn't
     * follow neither weekly program nor it is in independent mode, nor in control individually mode.
     */
    @SerializedName("Off")
    OFF(
        "The device is in off mode, it is not possible to send any comands to the device. " +
        "The device doesn't follow neither weekly program nor it is in independent mode, " +
        "nor in control individually mode."
    ),

    /**
     * The device follows the weekly program, changing temperature by display buttons changes the
     * temperature of the current temperature mode.
     */
    @SerializedName("Weekly program")
    WEEKLY_PROGRAM(
        "The device follows the weekly program, changing temperature by display buttons changes " +
        "the temperature of the current temperature mode."
    ),

    /** The device follows the single set value, with timers enabled. */
    @SerializedName("Independent device")
    INDEPENDENT_DEVICE("The device follows the single set value, with timers enabled."),

    /** The device follows the single set value, without any timers or weekly program. */
    @SerializedName("Control individually")
    CONTROL_INDIVIDUALLY("The device follows the single set value, without any timers or weekly program."),

    /** Invalid mode */
    @SerializedName("Invalid")
    INVALID("Invalid mode");

    private final String description;

    private OperationMode(String description) {
        this.description = description;
    }

    /**
     * @return The human-readable name/description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Tries to look up a {@link OperationMode} that matches the specified string.
     *
     * @param value the {@link String} to match.
     * @return The corresponding {@link OperationMode} or {@code null}.
     */
    @Nullable
    public static OperationMode typeOf(@Nullable String value) {
        if (value == null || MillUtil.isBlank(value)) {
            return null;
        }

        for (OperationMode entry : values()) {
            if (value.equals(entry.name())) {
                return entry;
            }
        }
        return null;
    }
}
