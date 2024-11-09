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
public enum OpenWindowStatus { // TODO: (Nad) JavaDocs

    @SerializedName("Disabled not active now")
    DISABLED("Open Window functionality is disabled.", false),

    @SerializedName("Enabled active now")
    ENABLED_ACTIVE(
        "Open Window functionality is active, and the window is detected as open, so the heater is not heating.",
        true
    ),

    @SerializedName("Enabled not active now")
    ENABLED_INACTIVE(
        "Open Window functionality is active, but the window is not detected as open, so the heater operates normally.",
        true
    );

    private final String description;

    private final boolean enabled;

    private OpenWindowStatus(String description, boolean enabled) {
        this.description = description;
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
