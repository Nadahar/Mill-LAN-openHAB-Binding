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
 * This class is used for deserializing JSON response objects from the "status" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class StatusResponse implements Response { // TODO: (Nad) JavaDocs

    @Nullable
    private String name;

    @SerializedName("custom_name")
    @Nullable
    private String customName;

    @Nullable
    private String version;

    @SerializedName("operation_key")
    @Nullable
    private String operationKey;

    @SerializedName("mac_address")
    @Nullable
    private String macAddress;

    @Nullable
    private ResponseStatus status;

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getCustomName() {
        return customName;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    @Nullable
    public String getOperationKey() {
        return operationKey;
    }

    @Nullable
    public String getMacAddress() {
        return macAddress;
    }

    @Override
    @Nullable
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customName, macAddress, name, operationKey, status, version);
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
        StatusResponse other = (StatusResponse) obj;
        return Objects.equals(customName, other.customName) && Objects.equals(macAddress, other.macAddress)
                && Objects.equals(name, other.name) && Objects.equals(operationKey, other.operationKey)
                && status == other.status && Objects.equals(version, other.version);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StatusResponse [");
        if (name != null) {
            builder.append("name=").append(name).append(", ");
        }
        if (customName != null) {
            builder.append("customName=").append(customName).append(", ");
        }
        if (version != null) {
            builder.append("version=").append(version).append(", ");
        }
        if (operationKey != null) {
            builder.append("operationKey=").append(operationKey).append(", ");
        }
        if (macAddress != null) {
            builder.append("macAddress=").append(macAddress).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
