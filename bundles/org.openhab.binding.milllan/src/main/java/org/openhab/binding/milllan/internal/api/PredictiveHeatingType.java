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
public enum PredictiveHeatingType { // TODO: (Nad) JavaDocs

    @SerializedName("Off")
    OFF("No predictive heating. The temperature will be change exactly with the weekly program timer."),

    @SerializedName("Simple")
    SIMPLE(
        "Simple predictive heating. The temperature will be change before the timer, with a fixed time " +
        "for each Celsius degree."
    ),

    @SerializedName("Advanced")
    ADVANCED(
        "Advanced predictive heating. The temperature will be change before the timer, with a time based " +
        "on the current room model. Model is estimated while running.d."
    );

    private final String description;

    private PredictiveHeatingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public static PredictiveHeatingType typeOf(@Nullable String value) {
        if (value == null || MillUtil.isBlank(value)) {
            return null;
        }

        String mValue = value.trim().toUpperCase(Locale.ROOT);
        for (PredictiveHeatingType entry : values()) {
            if (mValue.equals(entry.name())) {
                return entry;
            }
        }
        return null;
    }
}
