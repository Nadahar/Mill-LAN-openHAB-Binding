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
package org.openhab.binding.milllan.internal.api.request;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


/**
 * This class is used for serializing JSON request objects to the "open-window" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class OpenWindowParameters { // TODO: (Nad) JavaDocs

    /** The temperature drop required to trigger (activate) the open window function (in °C) */
    @Nullable
    @SerializedName("drop_temperature_threshold")
    protected Double dropTemperatureThreshold;

    /** The time range for which a drop in temperature will be evaluated (in seconds) */
    @Nullable
    @SerializedName("drop_time_range")
    protected Integer dropTimeRange;

    /** {@code true} if the open-window function is enabled (so that it will activate if the criteria are met) */
    @Nullable
    protected Boolean enabled;

    /** The temperature increase required to deactivate the open window function (in °C) */
    @Nullable
    @SerializedName("increase_temperature_threshold")
    protected Double increaseTemperatureThreshold;

    /** The time range for which an increase in temperature will be evaluated (in seconds) */
    @Nullable
    @SerializedName("increase_time_range")
    protected Integer increaseTimeRange;

    /** The maximum time the open window function will remain active */
    @Nullable
    @SerializedName("max_time")
    protected Integer maxTime;

    @Nullable
    public Double getDropTemperatureThreshold() {
        return dropTemperatureThreshold;
    }

    public void setDropTemperatureThreshold(Double dropTemperatureThreshold) {
        this.dropTemperatureThreshold = dropTemperatureThreshold;
    }

    @Nullable
    public Integer getDropTimeRange() {
        return dropTimeRange;
    }

    public void setDropTimeRange(Integer dropTimeRange) {
        this.dropTimeRange = dropTimeRange;
    }

    @Nullable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Nullable
    public Double getIncreaseTemperatureThreshold() {
        return increaseTemperatureThreshold;
    }

    public void setIncreaseTemperatureThreshold(Double increaseTemperatureThreshold) {
        this.increaseTemperatureThreshold = increaseTemperatureThreshold;
    }

    @Nullable
    public Integer getIncreaseTimeRange() {
        return increaseTimeRange;
    }

    public void setIncreaseTimeRange(Integer increaseTimeRange) {
        this.increaseTimeRange = increaseTimeRange;
    }

    @Nullable
    public Integer getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isComplete() {
        return
            dropTemperatureThreshold != null && dropTimeRange != null &&
            enabled != null && increaseTemperatureThreshold != null && maxTime != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            dropTemperatureThreshold, dropTimeRange, enabled,
            increaseTemperatureThreshold, increaseTimeRange, maxTime
        );
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OpenWindowParameters)) {
            return false;
        }
        OpenWindowParameters other = (OpenWindowParameters) obj;
        return
            Objects.equals(dropTemperatureThreshold, other.dropTemperatureThreshold)
            && Objects.equals(dropTimeRange, other.dropTimeRange) && Objects.equals(enabled, other.enabled)
            && Objects.equals(increaseTemperatureThreshold, other.increaseTemperatureThreshold)
            && Objects.equals(increaseTimeRange, other.increaseTimeRange) && Objects.equals(maxTime, other.maxTime);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
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
            builder.append("maxTime=").append(maxTime);
        }
        builder.append("]");
        return builder.toString();
    }
}
