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
import org.openhab.binding.milllan.internal.api.request.OpenWindowParameters;

import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects from the "open-window" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class OpenWindowParametersResponse extends OpenWindowParameters implements Response {

    /** Whether if the open-window function is active now */
    @Nullable
    @SerializedName("active_now")
    protected Boolean activeNow;

    /** The device API's {@code HTTP Response Status} */
    @Nullable
    protected ResponseStatus status;

    /**
     * @return {@code true} if the open-window function is active now.
     */
    @Nullable
    public Boolean getActiveNow() {
        return activeNow;
    }

    @Nullable
    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    /**
     * @return {@code true} if all fields are non-{@code null}.
     */
    @Override
    public boolean isComplete() {
        return activeNow != null && super.isComplete();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(activeNow, status);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OpenWindowParametersResponse)) {
            return false;
        }
        OpenWindowParametersResponse other = (OpenWindowParametersResponse) obj;
        return Objects.equals(activeNow, other.activeNow) && status == other.status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        if (activeNow != null) {
            builder.append("activeNow=").append(activeNow).append(", ");
        }
        if (dropTemperatureThreshold != null) {
            builder.append("dropTemperatureThreshold=").append(dropTemperatureThreshold).append(", ");
        }
        if (dropTimeRange != null) {
            builder.append("dropTimeRange=").append(dropTimeRange).append(", ");
        }
        if (enabled != null) {
            builder.append("enabled=").append(enabled).append(", ");
        }
        if (increaseTemperatureThreshold != null) {
            builder.append("increaseTemperatureThreshold=").append(increaseTemperatureThreshold).append(", ");
        }
        if (increaseTimeRange != null) {
            builder.append("increaseTimeRange=").append(increaseTimeRange).append(", ");
        }
        if (maxTime != null) {
            builder.append("maxTime=").append(maxTime).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
