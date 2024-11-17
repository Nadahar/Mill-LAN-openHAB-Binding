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
 * This class is used for deserializing JSON response objects from the "pid-parameters" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class PIDParametersResponse implements Response { // TODO: (Nad) JavaDocs

    /** The proportional gain factor */
    @Nullable
    private Double kp;

    /** The integral gain factor */
    @Nullable
    private Double ki;

    /** The derivative gain factor */
    @Nullable
    private Double kd;

    /** The derivative filter time coefficient */
    @Nullable
    @SerializedName("kd_filter_N")
    private Double kdFilterN;

    /** The wind-up limit for integral part from 0 to 100 */
    @Nullable
    @SerializedName("windup_limit_percentage")
    private Double windupLimitPercentage;

    @Nullable
    private ResponseStatus status;

    @Nullable
    public Double getKp() {
        return kp;
    }

    @Nullable
    public Double getKi() {
        return ki;
    }

    @Nullable
    public Double getKd() {
        return kd;
    }

    @Nullable
    public Double getKdFilterN() {
        return kdFilterN;
    }

    @Nullable
    public Double getWindupLimitPercentage() {
        return windupLimitPercentage;
    }

    @Override
    @Nullable
    public ResponseStatus getStatus() {
        return status;
    }

    public boolean isComplete() {
        return kp != null && ki != null && kd != null && kdFilterN != null && windupLimitPercentage != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kd, kdFilterN, ki, kp, status, windupLimitPercentage);
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
        PIDParametersResponse other = (PIDParametersResponse) obj;
        return Objects.equals(kd, other.kd) && Objects.equals(kdFilterN, other.kdFilterN)
                && Objects.equals(ki, other.ki) && Objects.equals(kp, other.kp) && status == other.status
                && Objects.equals(windupLimitPercentage, other.windupLimitPercentage);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PIDParametersResponse [");
        if (kp != null) {
            builder.append("kp=").append(kp).append(", ");
        }
        if (ki != null) {
            builder.append("ki=").append(ki).append(", ");
        }
        if (kd != null) {
            builder.append("kd=").append(kd).append(", ");
        }
        if (kdFilterN != null) {
            builder.append("kdFilterN=").append(kdFilterN).append(", ");
        }
        if (windupLimitPercentage != null) {
            builder.append("windupLimitPercentage=").append(windupLimitPercentage).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
