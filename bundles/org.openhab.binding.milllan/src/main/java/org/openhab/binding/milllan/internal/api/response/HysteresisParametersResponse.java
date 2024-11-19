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
 * This class is used for deserializing JSON response objects from the "hysteresis-parameters" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class HysteresisParametersResponse implements Response { // TODO: (Nad) JavaDocs

    @Nullable
    @SerializedName("temp_hysteresis_upper")
    private Double upper;

    @Nullable
    @SerializedName("temp_hysteresis_lower")
    private Double lower;

    //Doc: Probably should be ControllerType, but it's unclear exactly which devices support hysteresis and another mode
    @Nullable
    @SerializedName("regulator_type")
    private String regulatorType;

    @Nullable
    private ResponseStatus status;

    @Nullable
    public Double getUpper() {
        return upper;
    }

    @Nullable
    public Double getLower() {
        return lower;
    }

    @Nullable
    public String getRegulatorType() {
        return regulatorType;
    }

    @Nullable
    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    public boolean isComplete() {
        return upper != null && lower != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, regulatorType, status, upper);
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
        HysteresisParametersResponse other = (HysteresisParametersResponse) obj;
        return Objects.equals(lower, other.lower) && Objects.equals(regulatorType, other.regulatorType)
                && status == other.status && Objects.equals(upper, other.upper);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HysteresisParametersResponse [");
        if (upper != null) {
            builder.append("upper=").append(upper).append(", ");
        }
        if (lower != null) {
            builder.append("lower=").append(lower).append(", ");
        }
        if (regulatorType != null) {
            builder.append("regulatorType=").append(regulatorType).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
