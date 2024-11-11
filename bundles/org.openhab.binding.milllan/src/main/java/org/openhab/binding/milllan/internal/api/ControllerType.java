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

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.MillUtil;

import com.google.gson.annotations.SerializedName;


/**
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum ControllerType { // TODO: (Nad) JavaDocs

    @SerializedName("pid")
    PID("PID"),

    @SerializedName("hysteresis_or_slow_pid")
    SLOW_PID("Slow PID"),

    @SerializedName("unknown")
    UNKNOWN("Unknown");

    private final String description;

    private ControllerType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public static ControllerType typeOf(@Nullable String value) {
        if (value == null || MillUtil.isBlank(value)) {
            return null;
        }

        String mValue = value.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+|-", "_");
        for (ControllerType entry : values()) {
            if (mValue.equals(entry.name())) {
                return entry;
            }
        }
        return null;
    }
}
