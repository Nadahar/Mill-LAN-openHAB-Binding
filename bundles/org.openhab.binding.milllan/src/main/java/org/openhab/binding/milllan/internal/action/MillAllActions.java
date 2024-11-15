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

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.automation.Action;
import org.openhab.core.automation.annotation.ActionInput;
import org.openhab.core.automation.annotation.ActionOutput;
import org.openhab.core.automation.annotation.ActionOutputs;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingActionsScope;
import org.openhab.core.thing.type.ThingType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;


/**
 * This class annotates the {@link Action}s for the "all functions" {@link ThingType}.
 *
 * @author Nadahar - Initial contribution
 */
@Component(scope = ServiceScope.PROTOTYPE, service = MillAllActions.class)
@ThingActionsScope(name = "milllanall")
@NonNullByDefault
public class MillAllActions extends MillBaseActions {

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(
        label = "@text/actions.milllan.send-reboot.label",
        description = "@text/actions.milllan.send-reboot.description"
    )
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> sendReboot() {
        return super.sendReboot();
    }

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(
        label = "@text/actions.milllan.set-timezone-offset.label",
        description = "@text/actions.milllan.set-timezone-offset.description"
    )
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> setTimeZoneOffset(
        @Nullable @ActionInput(
            name = "offset",
            label = "@text/actions-input.milllan.set-timezone-offset.offset.label",
            description = "@text/actions-input.milllan.set-timezone-offset.offset.description",
            required = true
        ) Integer offset
    ) {
        return super.setTimeZoneOffset(offset);
    }

    // Methods for Rules DSL rule support

    /**
     * Attempts to send a {@code reboot} command to the device.
     *
     * @param actions the {@link ThingActions} instance.
     */
    public static void sendReboot(ThingActions actions) {
        ((MillAllActions) actions).sendReboot();
    }

    /**
     * Attempts to set the {@code time zone offset} in the device.
     *
     * @param actions the {@link ThingActions} instance.
     * @param offset the offset from UTC in minutes.
     */
    public static void setTimeZoneOffset(ThingActions actions, Integer offset) {
        ((MillAllActions) actions).setTimeZoneOffset(offset);
    }
}
