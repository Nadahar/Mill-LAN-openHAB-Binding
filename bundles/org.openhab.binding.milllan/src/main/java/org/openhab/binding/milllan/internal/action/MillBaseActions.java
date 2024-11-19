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
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillBaseActions implements ThingActions { // TODO: (Nad) Javadocs

    private final Logger logger = LoggerFactory.getLogger(MillBaseActions.class);

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
            result.put("result", "The cloud communicaton was " + (enabled == null || !enabled.booleanValue() ? "disabled" : "enabled") + ". The device is rebooting.");
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
}
