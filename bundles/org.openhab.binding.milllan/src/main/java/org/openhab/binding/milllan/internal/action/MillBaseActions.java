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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.AbstractMillThingHandler;
import org.openhab.binding.milllan.internal.exception.MillException;
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
}
