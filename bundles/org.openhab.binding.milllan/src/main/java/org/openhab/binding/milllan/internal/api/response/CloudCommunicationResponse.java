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
package org.openhab.binding.milllan.internal.api.response;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.api.ResponseStatus;

import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects from the "cloud-communication" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class CloudCommunicationResponse implements Response {

    /** Whether cloud communication is enabled */
    @Nullable
    @SerializedName("value")
    private Boolean enabled;

    /** The device API's {@code HTTP Response Status} */
    @Nullable
    private ResponseStatus status;

    /**
     * @return {@code true} if cloud communication is enabled.
     */
    @Nullable
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    @Nullable
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, status);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CloudCommunicationResponse other = (CloudCommunicationResponse) obj;
        return Objects.equals(enabled, other.enabled) && status == other.status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CloudCommunicationResponse [");
        if (enabled != null) {
            builder.append("enabled=").append(enabled).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
