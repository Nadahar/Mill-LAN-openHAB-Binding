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
import org.openhab.binding.milllan.internal.api.ControllerType;
import org.openhab.binding.milllan.internal.api.ResponseStatus;

import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects from the "hysteresis-parameters" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class HysteresisParametersResponse implements Response {

    /** The upper limit in °C: Set temperature + upper limit = stop heating */
    @Nullable
    @SerializedName("temp_hysteresis_upper")
    private Double upper;

    /** The lower limit in °C: Set temperature - lower limit = start heating */
    @Nullable
    @SerializedName("temp_hysteresis_lower")
    private Double lower;

    /**
     * It is unclear what this field is supposed to do - if it correlates to {@link ControllerType}
     * in some way or if it always is "hysteresis".
     */
    @Nullable
    @SerializedName("regulator_type")
    private String regulatorType;

    /** The device API's {@code HTTP Response Status} */
    @Nullable
    private ResponseStatus status;

    /**
     * @return The upper limit in °C: Set temperature + upper limit = stop heating.
     */
    @Nullable
    public Double getUpper() {
        return upper;
    }

    /**
     * @return The lower limit in °C: Set temperature - lower limit = start heating.
     */
    @Nullable
    public Double getLower() {
        return lower;
    }

    /**
     * @return A "regulator type" string whose meaning is unknown. See {@link #regulatorType}.
     */
    @Nullable
    public String getRegulatorType() {
        return regulatorType;
    }

    @Nullable
    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    /**
     * @return {@code true} if all fields are non-{@code null}.
     */
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
        return Objects.equals(lower, other.lower) &&
            Objects.equals(regulatorType, other.regulatorType) &&
            status == other.status && Objects.equals(upper, other.upper);
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
