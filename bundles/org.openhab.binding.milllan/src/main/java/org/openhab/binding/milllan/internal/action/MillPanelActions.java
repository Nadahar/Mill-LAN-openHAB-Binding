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

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(label = "@text/actions.milllan.set-custom-name.label", description = "@text/actions.milllan.set-custom-name.description")
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> setCustomName(
        @Nullable @ActionInput(
            name = "customName",
            label = "@text/actions-input.milllan.set-custom-name.custom-name.label",
            description = "@text/actions-input.milllan.set-custom-name.custom-name.description",
            required = true
        ) String customName
    ) {
        return super.setCustomName(customName);
    }

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(label = "@text/actions.milllan.set-open-window-parameters.label", description = "@text/actions.milllan.set-open-window-parameters.description")
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> setOpenWindowParameters(
        @Nullable @ActionInput(
            name = "dropTempThr",
            label = "@text/actions-input.milllan.set-open-window-parameters.drop-temp-thr.label",
            description = "@text/actions-input.milllan.set-open-window-parameters.drop-temp-thr.description",
            required = true
        ) Double dropTempThr,
        @Nullable @ActionInput(
            name = "dropTimeRange",
            label = "@text/actions-input.milllan.set-open-window-parameters.drop-time-range.label",
            description = "@text/actions-input.milllan.set-open-window-parameters.drop-time-range.description",
            required = true
        ) Integer dropTimeRange,
        @Nullable @ActionInput(
            name = "incTempThr",
            label = "@text/actions-input.milllan.set-open-window-parameters.inc-temp-thr.label",
            description = "@text/actions-input.milllan.set-open-window-parameters.inc-temp-thr.description",
            required = true
        ) Double incTempThr,
        @Nullable @ActionInput(
            name = "incTimeRange",
            label = "@text/actions-input.milllan.set-open-window-parameters.inc-time-range.label",
            description = "@text/actions-input.milllan.set-open-window-parameters.inc-time-range.description",
            required = true
        ) Integer incTimeRange,
        @Nullable @ActionInput(
            name = "maxTime",
            label = "@text/actions-input.milllan.set-open-window-parameters.max-time.label",
            description = "@text/actions-input.milllan.set-open-window-parameters.max-time.description",
            required = true
        ) Integer maxTime
    ) {
        return super.setOpenWindowParameters(dropTempThr, dropTimeRange, incTempThr, incTimeRange, maxTime);
    }

    @Override
    @ActionOutputs(value = {@ActionOutput(name = "result", type = "java.lang.String")})
    @RuleAction(label = "@text/actions.milllan.set-api-key.label", description = "@text/actions.milllan.set-api-key.description")
    public @ActionOutput(name = "result", type = "java.lang.String") Map<String, Object> setAPIKey(
        @ActionInput(
            name = "apiKey",
            label = "@text/actions-input.milllan.set-api-key.key.label",
            description = "@text/actions-input.milllan.set-api-key.key.description",
            required = true
        ) String apiKey,
        @ActionInput(
            name = "confirm",
            label = "@text/actions-input.milllan.set-api-key.confirm.label",
            description = "@text/actions-input.milllan.set-api-key.confirm.description",
            required = true
        ) String confirm
    ) {
        return super.setAPIKey(apiKey, confirm);
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

    public static void setCustomName(ThingActions actions, String customName) {
        ((MillPanelActions) actions).setCustomName(customName);
    }

    public static void setOpenWindowParameters(
        ThingActions actions,
        Double dropTempThr,
        Integer dropTimeRange,
        Double incTempThr,
        Integer incTimeRange,
        Integer maxTime
    ) {
        ((MillPanelActions) actions).setOpenWindowParameters(dropTempThr, dropTimeRange, incTempThr, incTimeRange, maxTime);
    }

    public static void setAPIKey(ThingActions actions, String apiKey, String confirm) {
        ((MillPanelActions) actions).setAPIKey(apiKey, confirm);
    }
}
