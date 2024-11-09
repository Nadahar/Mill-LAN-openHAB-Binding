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
 * This enum represents the device API's {@code ELockStatus}.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum LockStatus {

    /** All buttons are active */
    @SerializedName("No lock")
    NO_LOCK("All buttons are active"),

    /** The buttons on the device are not active */
    @SerializedName("Child lock")
    CHILD_LOCK("The buttons on the device are not active"),

    /** The buttons on the device have limited functionality */
    @SerializedName("Commercial lock")
    COMMERCIAL_LOCK("The buttons on the device have limited functionality");

    private final String description;

    private LockStatus(String description) {
        this.description = description;
    }

    /**
     * @return The human-readable name/description.
     */
    public String getDescription() {
        return description;
    }
}
