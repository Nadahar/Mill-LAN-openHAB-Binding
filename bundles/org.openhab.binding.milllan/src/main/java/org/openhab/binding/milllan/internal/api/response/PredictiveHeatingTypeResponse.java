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
import org.openhab.binding.milllan.internal.api.PredictiveHeatingType;
import org.openhab.binding.milllan.internal.api.ResponseStatus;

import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects from the "predictive-heating-type" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class PredictiveHeatingTypeResponse implements Response {

    /** The predictive heating type */
    @SerializedName("predictive_heating_type")
    @Nullable
    private PredictiveHeatingType predictiveHeatingType;

    /** The device API's {@code HTTP Response Status} */
    @Nullable
    private ResponseStatus status;

    /**
     * @return The {@link PredictiveHeatingType}.
     */
    @Nullable
    public PredictiveHeatingType getPredictiveHeatingType() {
        return predictiveHeatingType;
    }

    @Override
    @Nullable
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(predictiveHeatingType, status);
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
        PredictiveHeatingTypeResponse other = (PredictiveHeatingTypeResponse) obj;
        return predictiveHeatingType == other.predictiveHeatingType && status == other.status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PredictiveHeatingTypeResponse [");
        if (predictiveHeatingType != null) {
            builder.append("predictiveHeatingType=").append(predictiveHeatingType).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
