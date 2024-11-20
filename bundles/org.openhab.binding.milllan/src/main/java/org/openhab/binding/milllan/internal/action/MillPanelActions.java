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
import org.openhab.core.automation.annotation.ActionInput;
import org.openhab.core.automation.annotation.ActionOutput;
import org.openhab.core.automation.annotation.ActionOutputs;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingActionsScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;


/**
 * @author Nadahar - Initial contribution
 */
@Component(scope = ServiceScope.PROTOTYPE, service = MillPanelActions.class)
@ThingActionsScope(name = "milllanpanel")
@NonNullByDefault
public class MillPanelActions extends MillBaseActions { // TODO: (Nad) Javadocs

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(label = "@text/actions.milllan.send-reboot.label", description = "@text/actions.milllan.send-reboot.description")
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> sendReboot() {
        return super.sendReboot();
    }

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(label = "@text/actions.milllan.set-timezone-offset.label", description = "@text/actions.milllan.set-timezone-offset.description")
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
    @RuleAction(label = "@text/actions.milllan.set-pid-parameters.label", description = "@text/actions.milllan.set-pid-parameters.description")
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

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(label = "@text/actions.milllan.set-cloud-communication.label", description = "@text/actions.milllan.set-cloud-communication.description")
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> setCloudCommunication(
        @Nullable @ActionInput(
            name = "enabled",
            label = "@text/actions-input.milllan.set-cloud-communication.enabled.label",
            description = "@text/actions-input.milllan.set-cloud-communication.enabled.description",
            required = true
        ) Boolean enabled
    ) {
        return super.setCloudCommunication(enabled);
    }

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(label = "@text/actions.milllan.set-independent-temperature.label", description = "@text/actions.milllan.set-independent-temperature.description")
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> setIndependentModeTemperature(
        @Nullable @ActionInput(
            name = "temperature",
            label = "@text/actions-input.milllan.set-independent-temperature.temperature.label",
            description = "@text/actions-input.milllan.set-independent-temperature.temperature.description",
            required = true
        ) Number temperature
    ) {
        return super.setIndependentModeTemperature(temperature);
    }

    // Methods for Rules DSL rule support

    public static void sendReboot(ThingActions actions) {
        ((MillPanelActions) actions).sendReboot();
    }

    public static void setTimeZoneOffset(ThingActions actions, Integer offset) {
        ((MillPanelActions) actions).setTimeZoneOffset(offset);
    }

    public static void setPIDParameters(
        ThingActions actions,
        Double kp,
        Double ki,
        Double kd,
        Double kdFilterN,
        Double windupLimitPct
    ) {
        ((MillPanelActions) actions).setPIDParameters(kp, ki, kd, kdFilterN, windupLimitPct);
    }

    public static void setCloudCommunication(ThingActions actions, Boolean enabled) {
        ((MillPanelActions) actions).setCloudCommunication(enabled);
    }

    public static void setIndependentModeTemperature(ThingActions actions, Double temperature) {
        ((MillPanelActions) actions).setIndependentModeTemperature(temperature);
    }
}
