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
package org.openhab.binding.milllan.internal.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.AbstractMillThingHandler;
import org.openhab.binding.milllan.internal.MillUtil;
import org.openhab.binding.milllan.internal.api.ResponseStatus;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.exception.MillHTTPResponseException;
import org.openhab.core.automation.Action;
import org.openhab.core.automation.annotation.ActionOutput;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The common {@link Action} class for this binding where the actual implementation
 * of the {@link Action}s is.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillBaseActions implements ThingActions {

    private final Logger logger = LoggerFactory.getLogger(MillBaseActions.class);

    /** The {@link ThingHandler} instance */
    @Nullable
    protected AbstractMillThingHandler thingHandler;

    @Override
    public void setThingHandler(@Nullable ThingHandler thingHandler) {
        this.thingHandler = (AbstractMillThingHandler) thingHandler;
    }

    @Override
    @Nullable
    public ThingHandler getThingHandler() {
        return thingHandler;
    }

    /**
     * Attempts to send a {@code reboot} command to the device and returns the result of the {@link Action}.
     *
     * @return The resulting {@link ActionOutput} {@link Map}.
     */
    protected Map<String, Object> sendReboot() {
        Map<String, Object> result = new HashMap<>();
        AbstractMillThingHandler handlerInst = thingHandler;
        if (handlerInst == null) {
            logger.warn("Call to sendReboot Action failed because the thingHandler was null");
            result.put("result", "Failed: The Thing handler is null");
            return result;
        }
        try {
            handlerInst.sendReboot();
            result.put("result", "The device is rebooting.");
            return result;
        } catch (MillException e) {
            logger.warn(
                "Failed to execute sendReboot Action on Thing {}: {}",
                handlerInst.getThing().getUID(),
                e.getMessage()
            );
            result.put("result", "Failed to execute sendReboot Action: " + e.getMessage());
            return result;
        }
    }

    /**
     * Attempts to set the {@code time zone offset} in the device and returns the result of the {@link Action}.
     *
     * @param offset the offset from UTC in minutes.
     * @return The resulting {@link ActionOutput} {@link Map}.
     */
    protected Map<String, Object> setTimeZoneOffset(@Nullable Integer offset) {
        Map<String, Object> result = new HashMap<>();
        AbstractMillThingHandler handlerInst = thingHandler;
        if (handlerInst == null) {
            logger.warn("Call to setTimeZoneOffset Action failed because the thingHandler was null");
            result.put("result", "Failed: The Thing handler is null");
            return result;
        }
        if (offset == null) {
            logger.warn("Call to setTimeZoneOffset Action failed because the offset was null");
            result.put("result", "The time zone offset must be specified!");
            return result;
        }
        try {
            handlerInst.setTimeZoneOffset(offset, true);
            result.put("result", "The time zone offset was set to " + offset + '.');
            return result;
        } catch (MillException e) {
            logger.warn(
                "Failed to execute setTimeZoneOffset Action on Thing {}: {}",
                handlerInst.getThing().getUID(),
                e.getMessage()
            );
            result.put("result", "Failed to execute setTimeZoneOffset Action: " + e.getMessage());
            return result;
        }
    }

    /**
     * Attempts to set the {@code PID parameters} in the device and returns the result of the {@link Action}.
     *
     * @param kp the proportional gain factor.
     * @param ki the integral gain factor.
     * @param kd the derivative gain factor.
     * @param kdFilterN the derivative filter time coefficient.
     * @param windupLimitPct the wind-up limit for integral part from 0 to 100.
     * @return The resulting {@link ActionOutput} {@link Map}.
     */
    public Map<String, Object> setPIDParameters(
        @Nullable Double kp,
        @Nullable Double ki,
        @Nullable Double kd,
        @Nullable Double kdFilterN,
        @Nullable Double windupLimitPct
    ) {
        Map<String, Object> result = new HashMap<>();
        AbstractMillThingHandler handlerInst = thingHandler;
        if (handlerInst == null) {
            logger.warn("Call to setPIDParameters Action failed because the thingHandler was null");
            result.put("result", "Failed: The Thing handler is null");
            return result;
        }
        if (kp == null || ki == null || kd == null || kdFilterN == null || windupLimitPct == null) {
            logger.warn("Call to setPIDParameters Action failed because some parameters were null");
            result.put("result", "All PID parameters must be specified!");
            return result;
        }
        try {
            handlerInst.setPIDParameters(kp, ki, kd, kdFilterN, windupLimitPct, true);
            result.put("result", "The PID parameters were set.");
            return result;
        } catch (MillException e) {
            logger.warn(
                "Failed to execute setPIDParameters Action on Thing {}: {}",
                handlerInst.getThing().getUID(),
                e.getMessage()
            );
            result.put("result", "Failed to execute setPIDParameters Action: " + e.getMessage());
            return result;
        }
    }

    /**
     * Attempts to set whether {@code cloud communication} is enabled in the device and returns the
     * result of the {@link Action}.
     *
     * @param enabled {@code true} to enabled cloud communication, {@code false} otherwise.
     * @return The resulting {@link ActionOutput} {@link Map}.
     */
    public Map<String, Object> setCloudCommunication(@Nullable Boolean enabled) {
        Map<String, Object> result = new HashMap<>();
        AbstractMillThingHandler handlerInst = thingHandler;
        if (handlerInst == null) {
            logger.warn("Call to setCloudCommunication Action failed because the thingHandler was null");
            result.put("result", "Failed: The Thing handler is null");
            return result;
        }
        try {
            handlerInst.setCloudCommunication(enabled == null ? Boolean.FALSE : enabled, true);
            result.put(
                "result",
                "The cloud communicaton was " + (enabled == null || !enabled.booleanValue() ? "disabled" : "enabled") +
                ". The device is rebooting."
            );
            try {
                handlerInst.sendReboot();
            } catch (MillException e) {
                logger.warn(
                    "Failed to reboot device after setting cloud communication on Thing {}: {}",
                    handlerInst.getThing().getUID(),
                    e.getMessage()
                );
                result.put("result", "Failed to execute reboot: " + e.getMessage());
            }
            return result;
        } catch (MillException e) {
            logger.warn(
                "Failed to execute setCloudCommunication Action on Thing {}: {}",
                handlerInst.getThing().getUID(),
                e.getMessage()
            );
            result.put("result", "Failed to execute setCloudCommunication Action: " + e.getMessage());
            return result;
        }
    }

    /**
     * Attempts to set the {@code hysteresis parameters} in the device and returns the result of the {@link Action}.
     *
     * @param upper the upper hysteresis limit in °C.
     * @param lower the lower hysteresis limit in °C.
     * @return The resulting {@link ActionOutput} {@link Map}.
     */
    public Map<String, Object> setHysteresisParameters(@Nullable Double upper, @Nullable Double lower) {
        Map<String, Object> result = new HashMap<>();
        AbstractMillThingHandler handlerInst = thingHandler;
        if (handlerInst == null) {
            logger.warn("Call to setHysteresisParameters Action failed because the thingHandler was null");
            result.put("result", "Failed: The Thing handler is null");
            return result;
        }
        if (upper == null || lower == null) {
            logger.warn("Call to setHysteresisParameters Action failed because some parameters were null");
            result.put("result", "All hysteresis parameters must be specified!");
            return result;
        }
        try {
            handlerInst.setHysteresisParameters(upper, lower, true);
            result.put("result", "The hysteresis parameters were set.");
            try {
                handlerInst.sendReboot();
            } catch (MillException e) {
                logger.warn(
                    "Failed to reboot device after setting hysteresis parameters on Thing {}: {}",
                    handlerInst.getThing().getUID(),
                    e.getMessage()
                );
                result.put("result", "Failed to execute reboot: " + e.getMessage());
            }
            return result;
        } catch (MillException e) {
            logger.warn(
                "Failed to execute setHysteresisParameters Action on Thing {}: {}",
                handlerInst.getThing().getUID(),
                e.getMessage()
            );
            result.put("result", "Failed to execute setHysteresisParameters Action: " + e.getMessage());
            return result;
        }
    }

    /**
     * Attempts to set the {@code set-temperature} in "independent device" mode in the device and
     * returns the result of the {@link Action}.
     * <p>
     * <b>Note:</b> This command will <i>only</i> work if the device is in "independent device" mode.
     *
     * @param temperature the set-temperature in °C.
     * @return The resulting {@link ActionOutput} {@link Map}.
     */
    public Map<String, Object> setIndependentModeTemperature(@Nullable Number temperature) {
        Map<String, Object> result = new HashMap<>();
        if (temperature == null) {
            logger.warn("Call to setIndependentModeTemperature Action aborted because 'temperature' was null");
            result.put("result", "Failed: The temperature parameter is null");
            return result;
        }
        AbstractMillThingHandler handlerInst = thingHandler;
        if (handlerInst == null) {
            logger.warn("Call to setIndependentModeTemperature Action failed because the thingHandler was null");
            result.put("result", "Failed: The Thing handler is null");
            return result;
        }
        try {
            BigDecimal bdValue;
            if (temperature instanceof BigDecimal) {
                bdValue = (BigDecimal) temperature;
            } else if (
                temperature instanceof BigInteger || temperature instanceof Integer || temperature instanceof Long
            ) {
                bdValue = BigDecimal.valueOf(temperature.intValue());
            } else {
                bdValue = BigDecimal.valueOf(temperature.doubleValue());
            }
            ResponseStatus responseStatus = handlerInst.setTemperatureInIndependentMode(bdValue);
            if (responseStatus != ResponseStatus.OK) {
                result.put(
                    "result",
                    "Failed: " + (responseStatus == null ? "Missing response" : responseStatus.getDescription())
                );
            } else {
                result.put("result", "The \"independent device\" mode temperature was set to " + temperature);
            }
        } catch (MillHTTPResponseException e) {
            if (e.getHttpStatus() == 503) {
                logger.debug(
                    "Execution of setIndependentModeTemperature Action on Thing {} resulted in HTTP status 503 - " +
                    "Verify that the device is in \"independent device\" mode.",
                    handlerInst.getThing().getUID()
                );
                result.put("result", "Failed: Verify that the device is in \"independent device\" mode.");
            } else {
                logger.warn(
                    "Failed to execute setIndependentModeTemperature Action on Thing {}: {}",
                    handlerInst.getThing().getUID(),
                    e.getMessage()
                );
                result.put("result", "Failed to execute setIndependentModeTemperature Action: " + e.getMessage());
            }
        } catch (MillException e) {
            logger.warn(
                "Failed to execute setIndependentModeTemperature Action on Thing {}: {}",
                handlerInst.getThing().getUID(),
                e.getMessage()
            );
            result.put("result", "Failed to execute setIndependentModeTemperature Action: " + e.getMessage());
        }
        return result;
    }

    /**
     * Attempts to set the {@code custom name} of the device and returns the result of the {@link Action}.
     *
     * @param customName the new custom name.
     * @return The resulting {@link ActionOutput} {@link Map}.
     */
    public Map<String, Object> setCustomName(@Nullable String customName) {
        Map<String, Object> result = new HashMap<>();
        AbstractMillThingHandler handlerInst = thingHandler;
        if (handlerInst == null) {
            logger.warn("Call to setCustomName Action failed because the thingHandler was null");
            result.put("result", "Failed: The Thing handler is null");
            return result;
        }
        try {
            ResponseStatus responseStatus = handlerInst.setCustomName(MillUtil.isBlank(customName) ? "" : customName);
            if (responseStatus != ResponseStatus.OK) {
                result.put(
                    "result",
                    "Failed: " + (responseStatus == null ? "Missing response" : responseStatus.getDescription())
                );
            } else {
                if (MillUtil.isBlank(customName)) {
                    result.put("result", "The custom device name was removed");
                } else {
                    result.put("result", "The custom device name was set to " + customName);
                }
            }
        } catch (MillException e) {
            logger.warn(
                "Failed to execute setCustomName Action on Thing {}: {}",
                handlerInst.getThing().getUID(),
                e.getMessage()
            );
            result.put("result", "Failed to execute Action: " + e.getMessage());
        }
        return result;
    }
}
