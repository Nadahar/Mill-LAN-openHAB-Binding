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
}
