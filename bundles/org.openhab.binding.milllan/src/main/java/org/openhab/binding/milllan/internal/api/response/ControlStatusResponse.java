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
import org.openhab.binding.milllan.internal.api.LockStatus;
import org.openhab.binding.milllan.internal.api.OpenWindowStatus;
import org.openhab.binding.milllan.internal.api.OperationMode;
import org.openhab.binding.milllan.internal.api.ResponseStatus;

import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects from the "control-status" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class ControlStatusResponse implements Response {

    /** The temperature measured by the sensor in °C (with calibration offset value included) */
    @SerializedName("ambient_temperature")
    @Nullable
    private Double ambientTemperature;

    /** The current heating power in W */
    @SerializedName("current_power")
    @Nullable
    private Double currentPower;

    /** The control signal of the PID regulator (0-100%) */
    @SerializedName("control_signal")
    @Nullable
    private Double controlSignal;

    /** The lock status */
    @SerializedName("lock_active")
    @Nullable
    private LockStatus lockStatus;

    /** The open widows status */
    @SerializedName("open_window_active_now")
    @Nullable
    private OpenWindowStatus openWindowStatus;

    /** The temperature measured by the sensor °C degrees without the calibration offset value */
    @SerializedName("raw_ambient_temperature")
    @Nullable
    private Double rawAmbientTemperature;

    /** The current set temperature in °C */
    @SerializedName("set_temperature")
    @Nullable
    private Double setTemperature;

    /**
     * The API documentation claims that this is supposed to be: {@code true} if the device
     * is switched on - whether it is set to working, with heating.
     * <p>
     * In practice however, it always seems to return {@code false}.
     */
    @SerializedName("switched_on")
    @Nullable
    private Boolean switchedOn;

    /** Whether the device is allowed to connect with the cloud */
    @SerializedName("connected_to_cloud")
    @Nullable
    private Boolean connectedToCloud;

    /** The current mode of operation */
    @SerializedName("operation_mode")
    @Nullable
    private OperationMode operatingMode;

    /** The device API's {@code HTTP Response Status} */
    @Nullable
    private ResponseStatus status;

    /**
     * @return The temperature measured by the sensor in °C (with calibration offset value included).
     */
    @Nullable
    public Double getAmbientTemperature() {
        return ambientTemperature;
    }

    /**
     * @return The current heating power in W.
     */
    @Nullable
    public Double getCurrentPower() {
        return currentPower;
    }

    /**
     * @return The control signal of the PID regulator (0-100%).
     */
    @Nullable
    public Double getControlSignal() {
        return controlSignal;
    }

    /**
     * @return The {@link LockStatus}.
     */
    @Nullable
    public LockStatus getLockStatus() {
        return lockStatus;
    }

    /**
     * @return The {@link OpenWindowStatus}.
     */
    @Nullable
    public OpenWindowStatus getOpenWindowStatus() {
        return openWindowStatus;
    }

    /**
     * @return The temperature measured by the sensor °C degrees without the calibration offset value.
     */
    @Nullable
    public Double getRawAmbientTemperature() {
        return rawAmbientTemperature;
    }

    /**
     * @return The current set temperature in °C.
     */
    @Nullable
    public Double getSetTemperature() {
        return setTemperature;
    }

    /**
     * The API documentation claims that this is supposed to be: {@code true} if the device
     * is switched on - whether it is set to working, with heating.
     * <p>
     * In practice however, it always seems to return {@code false}.
     *
     * @return {@code false}.
     */
    @Nullable
    public Boolean getSwitchedOn() {
        return switchedOn;
    }

    /**
     * @return {@code true} if the device is allowed to connect with the cloud.
     */
    @Nullable
    public Boolean getConnectedToCloud() {
        return connectedToCloud;
    }

    /**
     * @return The {@link OperationMode}.
     */
    @Nullable
    public OperationMode getOperatingMode() {
        return operatingMode;
    }

    @Override
    @Nullable
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ambientTemperature, connectedToCloud, controlSignal, currentPower, lockStatus,
                openWindowStatus, operatingMode, rawAmbientTemperature, setTemperature, status, switchedOn);
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
        ControlStatusResponse other = (ControlStatusResponse) obj;
        return Objects.equals(ambientTemperature, other.ambientTemperature) &&
            Objects.equals(connectedToCloud, other.connectedToCloud) &&
            Objects.equals(controlSignal, other.controlSignal) &&
            Objects.equals(currentPower, other.currentPower) && lockStatus == other.lockStatus &&
            openWindowStatus == other.openWindowStatus && operatingMode == other.operatingMode &&
            Objects.equals(rawAmbientTemperature, other.rawAmbientTemperature) &&
            Objects.equals(setTemperature, other.setTemperature) && status == other.status &&
            Objects.equals(switchedOn, other.switchedOn);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StatusResponse [");
        if (ambientTemperature != null) {
            builder.append("ambientTemperature=").append(ambientTemperature).append(", ");
        }
        if (currentPower != null) {
            builder.append("currentPower=").append(currentPower).append(", ");
        }
        if (controlSignal != null) {
            builder.append("controlSignal=").append(controlSignal).append(", ");
        }
        if (lockStatus != null) {
            builder.append("lockActive=").append(lockStatus).append(", ");
        }
        if (openWindowStatus != null) {
            builder.append("openWindowStatus=").append(openWindowStatus).append(", ");
        }
        if (rawAmbientTemperature != null) {
            builder.append("rawAmbientTemperature=").append(rawAmbientTemperature).append(", ");
        }
        if (setTemperature != null) {
            builder.append("setTemperature=").append(setTemperature).append(", ");
        }
        if (switchedOn != null) {
            builder.append("switchedOn=").append(switchedOn).append(", ");
        }
        if (connectedToCloud != null) {
            builder.append("connectedToCloud=").append(connectedToCloud).append(", ");
        }
        if (operatingMode != null) {
            builder.append("operatingMode=").append(operatingMode).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
