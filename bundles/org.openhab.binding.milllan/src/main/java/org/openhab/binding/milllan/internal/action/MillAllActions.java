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

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(
        label = "@text/actions.milllan.set-pid-parameters.label",
        description = "@text/actions.milllan.set-pid-parameters.description"
    )
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> setPIDParameters(
        @Nullable @ActionInput(
            name = "kp",
            label = "Kp",
            description = "@text/actions-input.milllan.set-pid-parameters.kp.description",
            required = true
        ) Double kp,
        @Nullable @ActionInput(
            name = "ki",
            label = "Ki",
            description = "@text/actions-input.milllan.set-pid-parameters.ki.description",
            required = true
        ) Double ki,
        @Nullable @ActionInput(
            name = "kd",
            label = "Kd",
            description = "@text/actions-input.milllan.set-pid-parameters.kd.description",
            required = true
        ) Double kd,
        @Nullable @ActionInput(
            name = "kdFilterN",
            label = "@text/actions-input.milllan.set-pid-parameters.kd-filter.label",
            description = "@text/actions-input.milllan.set-pid-parameters.kd-filter.description",
            required = true
        ) Double kdFilterN,
        @Nullable @ActionInput(
            name = "windupLimitPct",
            label = "@text/actions-input.milllan.set-pid-parameters.windup-limit.label",
            description = "@text/actions-input.milllan.set-pid-parameters.windup-limit.description",
            required = true
        ) Double windupLimitPct
    ) {
        return super.setPIDParameters(kp, ki, kd, kdFilterN, windupLimitPct);
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

    /**
     * Attempts to set the {@code PID parameters} in the device.
     *
     * @param actions the {@link ThingActions} instance.
     * @param kp the proportional gain factor.
     * @param ki the integral gain factor.
     * @param kd the derivative gain factor.
     * @param kdFilterN the derivative filter time coefficient.
     * @param windupLimitPct the wind-up limit for integral part from 0 to 100.
     */
    public static void setPIDParameters(
        ThingActions actions,
        Double kp,
        Double ki,
        Double kd,
        Double kdFilterN,
        Double windupLimitPct
    ) {
        ((MillAllActions) actions).setPIDParameters(kp, ki, kd, kdFilterN, windupLimitPct);
    }
}
