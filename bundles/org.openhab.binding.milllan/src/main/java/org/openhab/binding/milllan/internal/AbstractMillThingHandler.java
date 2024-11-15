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
package org.openhab.binding.milllan.internal;

import static org.openhab.binding.milllan.internal.MillBindingConstants.*;
import static org.openhab.binding.milllan.internal.MillUtil.isBlank;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.milllan.internal.api.ControllerType;
import org.openhab.binding.milllan.internal.api.DisplayUnit;
import org.openhab.binding.milllan.internal.api.LockStatus;
import org.openhab.binding.milllan.internal.api.MillAPITool;
import org.openhab.binding.milllan.internal.api.OpenWindowStatus;
import org.openhab.binding.milllan.internal.api.OperationMode;
import org.openhab.binding.milllan.internal.api.PredictiveHeatingType;
import org.openhab.binding.milllan.internal.api.ResponseStatus;
import org.openhab.binding.milllan.internal.api.TemperatureType;
import org.openhab.binding.milllan.internal.api.response.ChildLockResponse;
import org.openhab.binding.milllan.internal.api.response.CommercialLockResponse;
import org.openhab.binding.milllan.internal.api.response.ControlStatusResponse;
import org.openhab.binding.milllan.internal.api.response.ControllerTypeResponse;
import org.openhab.binding.milllan.internal.api.response.DisplayUnitResponse;
import org.openhab.binding.milllan.internal.api.response.LimitedHeatingPowerResponse;
import org.openhab.binding.milllan.internal.api.response.OilHeaterPowerResponse;
import org.openhab.binding.milllan.internal.api.response.OperationModeResponse;
import org.openhab.binding.milllan.internal.api.response.PredictiveHeatingTypeResponse;
import org.openhab.binding.milllan.internal.api.response.Response;
import org.openhab.binding.milllan.internal.api.response.SetTemperatureResponse;
import org.openhab.binding.milllan.internal.api.response.StatusResponse;
import org.openhab.binding.milllan.internal.api.response.TemperatureCalibrationOffsetResponse;
import org.openhab.binding.milllan.internal.api.response.TimeZoneOffsetResponse;
import org.openhab.binding.milllan.internal.configuration.MillConfigDescriptionProvider;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.exception.MillHTTPResponseException;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.config.core.status.ConfigStatusCallback;
import org.openhab.core.config.core.status.ConfigStatusMessage;
import org.openhab.core.config.core.status.ConfigStatusProvider;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingConfigStatusSource;
import org.openhab.core.thing.binding.ThingHandlerCallback;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AbstractMillThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractMillThingHandler extends BaseThingHandler implements ConfigStatusProvider { // TODO: (Nad) JavaDocs

    private final Logger logger = LoggerFactory.getLogger(AbstractMillThingHandler.class);

    @Nullable
    protected ConfigStatusCallback configStatusCallback;

    protected final MillConfigDescriptionProvider configDescriptionProvider;

    protected final MillHTTPClientProvider httpClientProvider;

    /** The object used for synchronization of class fields */
    protected final Object lock = new Object();

    /** Current online state, must be synchronized on {@link #lock} */
    protected boolean isOnline;

    // must be synched on lock
    protected boolean onlineWithError;

    // Must be synced on lock
    @Nullable
    protected ScheduledFuture<?> frequentPollTask;

    // Must be synced on lock
    @Nullable
    protected ScheduledFuture<?> infrequentPollTask;

    // Must be synced on lock
    @Nullable
    protected ScheduledFuture<?> offlinePollTask;

    // Must be synced on lock
    protected boolean isDisposed = true;

    protected final MillAPITool apiTool;

    public AbstractMillThingHandler(
        Thing thing,
        MillConfigDescriptionProvider configDescriptionProvider,
        MillHTTPClientProvider httpClientProvider
    ) {
        super(thing);
        this.configDescriptionProvider = configDescriptionProvider;
        this.httpClientProvider = httpClientProvider;
        this.apiTool = new MillAPITool(this.httpClientProvider);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            switch (channelUID.getId()) {
                case AMBIENT_TEMPERATURE:
                case RAW_AMBIENT_TEMPERATURE:
                case CURRENT_POWER:
                case CONTROL_SIGNAL:
                case SET_TEMPERATURE:
                case LOCK_STATUS:
                case OPEN_WINDOW_STATUS:
                case CONNECTED_CLOUD:
                    if (command instanceof RefreshType) {
                        pollControlStatus();
                    }
                    break;
                case OPERATION_MODE:
                    if (command instanceof RefreshType) {
                        pollOperationMode();
                    } else if (command instanceof StringType) {
                        setOperationMode(command.toString());
                    }
                    break;
                case TEMPERATURE_CALIBRATION_OFFSET:
                    if (command instanceof RefreshType) {
                        pollTemperatureCalibrationOffset();
                    } else if (command instanceof QuantityType) {
                        @SuppressWarnings("unchecked")
                        QuantityType<?> celsiusOffset = ((QuantityType<Temperature>) command).toUnitRelative(SIUnits.CELSIUS);
                        if (celsiusOffset == null) {
                            logger.warn("Failed to set temperature calibration offset: Could not convert {} to degrees celsius", command);
                        } else {
                            setTemperatureCalibrationOffset(celsiusOffset.toBigDecimal());
                        }
                    }
                    break;
                case COMMERCIAL_LOCK:
                    if (command instanceof RefreshType) {
                        pollCommercialLock();
                    } else if (command instanceof OnOffType) {
                        setCommercialLock(command == OnOffType.ON);
                    }
                    break;
                case CHILD_LOCK:
                    if (command instanceof RefreshType) {
                        pollChildLock();
                    } else if (command instanceof OnOffType) {
                        setChildLock(command == OnOffType.ON);
                    }
                    break;
                case DISPLAY_UNIT:
                    if (command instanceof RefreshType) {
                        pollDisplayUnit();
                    } else if (command instanceof StringType) {
                        setDisplayUnit(command.toString());
                    }
                    break;
                case NORMAL_SET_TEMPERATURE:
                    if (command instanceof RefreshType) {
                        pollSetTemperature(NORMAL_SET_TEMPERATURE, TemperatureType.NORMAL);
                    } else if (command instanceof QuantityType) {
                        @SuppressWarnings("unchecked")
                        QuantityType<?> celsiusValue = ((QuantityType<Temperature>) command).toUnit(SIUnits.CELSIUS);
                        if (celsiusValue == null) {
                            logger.warn("Failed to set \"normal\" set-temperature: Could not convert {} to degrees celsius", command);
                        } else {
                            setSetTemperature(NORMAL_SET_TEMPERATURE, TemperatureType.NORMAL, celsiusValue.toBigDecimal());
                        }
                    }
                    break;
                case COMFORT_SET_TEMPERATURE:
                    if (command instanceof RefreshType) {
                        pollSetTemperature(COMFORT_SET_TEMPERATURE, TemperatureType.COMFORT);
                    } else if (command instanceof QuantityType) {
                        @SuppressWarnings("unchecked")
                        QuantityType<?> celsiusValue = ((QuantityType<Temperature>) command).toUnit(SIUnits.CELSIUS);
                        if (celsiusValue == null) {
                            logger.warn("Failed to set \"comfort\" set-temperature: Could not convert {} to degrees celsius", command);
                        } else {
                            setSetTemperature(COMFORT_SET_TEMPERATURE, TemperatureType.COMFORT, celsiusValue.toBigDecimal());
                        }
                    }
                    break;
                case SLEEP_SET_TEMPERATURE:
                    if (command instanceof RefreshType) {
                        pollSetTemperature(SLEEP_SET_TEMPERATURE, TemperatureType.SLEEP);
                    } else if (command instanceof QuantityType) {
                        @SuppressWarnings("unchecked")
                        QuantityType<?> celsiusValue = ((QuantityType<Temperature>) command).toUnit(SIUnits.CELSIUS);
                        if (celsiusValue == null) {
                            logger.warn("Failed to set \"sleep\" set-temperature: Could not convert {} to degrees celsius", command);
                        } else {
                            setSetTemperature(SLEEP_SET_TEMPERATURE, TemperatureType.SLEEP, celsiusValue.toBigDecimal());
                        }
                    }
                    break;
                case AWAY_SET_TEMPERATURE:
                    if (command instanceof RefreshType) {
                        pollSetTemperature(AWAY_SET_TEMPERATURE, TemperatureType.AWAY);
                    } else if (command instanceof QuantityType) {
                        @SuppressWarnings("unchecked")
                        QuantityType<?> celsiusValue = ((QuantityType<Temperature>) command).toUnit(SIUnits.CELSIUS);
                        if (celsiusValue == null) {
                            logger.warn("Failed to set \"away\" set-temperature: Could not convert {} to degrees celsius", command);
                        } else {
                            setSetTemperature(AWAY_SET_TEMPERATURE, TemperatureType.AWAY, celsiusValue.toBigDecimal());
                        }
                    }
                    break;
                case LIMITED_HEATING_POWER:
                    if (command instanceof RefreshType) {
                        pollLimitedHeatingPower();
                    } else if (command instanceof Number) {
                        int i = ((Number) command).intValue();
                        if (i < 10 || i > 100) {
                            logger.warn("Failed to set limited heating power: {} is outside valid range 10-100", i);
                        } else {
                            setLimitedHeatingPower(Integer.valueOf(i));
                        }
                    }
                    break;
                case CONTROLLER_TYPE:
                    if (command instanceof RefreshType) {
                        pollControllerType();
                    } else if (command instanceof StringType) {
                        setControllerType(command.toString());
                    }
                    break;
                case PREDICTIVE_HEATING_TYPE:
                    if (command instanceof RefreshType) {
                        pollPredictiveHeatingType();
                    } else if (command instanceof StringType) {
                        setPredictiveHeatingType(command.toString());
                    }
                    break;
                case OIL_HEATER_POWER:
                    if (command instanceof RefreshType) {
                        pollOilHeaterPower();
                    } else if (command instanceof Number) {
                        int i = ((Number) command).intValue();
                        if (i != 40 && i != 60 && i != 100) {
                            logger.warn("Failed to set limited heating power: {} is outside valid range 40,60,100", i);
                        } else {
                            setOilHeaterPower(Integer.valueOf(i));
                        }
                    }
                    break;

            }
        } catch (MillException e) {
            setOffline(e);
        }
    }

    @Override
    public void initialize() {
        if (logger.isTraceEnabled()) {
            logger.trace("Initializing Thing handler for {}", getThing().getUID());
        }
        synchronized (lock) {
            isDisposed = false;
        }
        updateStatus(ThingStatus.UNKNOWN);
        scheduler.execute(createInitializeTask());
    }

    @Override
    public void dispose() {
        if (logger.isTraceEnabled()) {
            logger.trace("Disposing of Thing handler for {}", getThing().getUID());
        }
        configDescriptionProvider.disableDescriptions(getThing().getUID());
        ScheduledFuture<?> frequentFuture, infrequentFuture, offlineFuture;
        synchronized (lock) {
            frequentFuture = frequentPollTask;
            frequentPollTask = null;
            infrequentFuture = infrequentPollTask;
            infrequentPollTask = null;
            offlineFuture = offlinePollTask;
            offlinePollTask = null;
            isDisposed = true;
            isOnline = false;
            onlineWithError = false;
        }
        if (frequentFuture != null) {
            frequentFuture.cancel(true);
        }
        if (infrequentFuture != null) {
            infrequentFuture.cancel(true);
        }
        if (offlineFuture != null) {
            offlineFuture.cancel(true);
        }
    }

    public void pollStatus() throws MillException { //TODO: (Nad) Remember to run: mvn i18n:generate-default-translations
        StatusResponse statusResponse = apiTool.getStatus(getHostname());
        setOnline();
        Map<String, String> properties = editProperties();
        boolean changed = false;
        boolean removed = false;
        String s = statusResponse.getName();
        if (s == null || isBlank(s)) {
            removed |= properties.remove(PROPERTY_NAME) != null;
        } else if (!s.equals(properties.get(PROPERTY_NAME))) {
            properties.put(PROPERTY_NAME, s);
            changed |= true;
        }
        s = statusResponse.getCustomName();
        if (s == null || isBlank(s)) {
            removed |= properties.remove(PROPERTY_CUSTOM_NAME) != null;
        } else if (!s.equals(properties.get(PROPERTY_CUSTOM_NAME))) {
            properties.put(PROPERTY_CUSTOM_NAME, s);
            changed |= true;
        }
        s = statusResponse.getVersion();
        if (s == null || isBlank(s)) {
            removed |= properties.remove(Thing.PROPERTY_FIRMWARE_VERSION) != null;
        } else if (!s.equals(properties.get(Thing.PROPERTY_FIRMWARE_VERSION))) {
            properties.put(Thing.PROPERTY_FIRMWARE_VERSION, s);
            changed |= true;
        }
        s = statusResponse.getOperationKey();
        if (s == null || isBlank(s)) {
            removed |= properties.remove(PROPERTY_OPERATION_KEY) != null;
        } else if (!s.equals(properties.get(PROPERTY_OPERATION_KEY))) {
            properties.put(PROPERTY_OPERATION_KEY, s);
            changed |= true;
        }
        s = statusResponse.getMacAddress();
        if (s == null || isBlank(s)) {
            removed |= properties.remove(Thing.PROPERTY_MAC_ADDRESS) != null;
        } else if (!s.equals(properties.get(Thing.PROPERTY_MAC_ADDRESS))) {
            properties.put(Thing.PROPERTY_MAC_ADDRESS, s);
            changed |= true;
        }
        if (removed) {
            updateProperties(null);
        }
        if (changed || removed) {
            updateProperties(properties);
        }
    }

    public void pollControlStatus() throws MillException {
        ControlStatusResponse controlStatusResponse = apiTool.getControlStatus(getHostname());
        setOnline();
        Double d;
        if ((d = controlStatusResponse.getAmbientTemperature()) != null) {
            updateState(AMBIENT_TEMPERATURE, new QuantityType<>(d, SIUnits.CELSIUS));
        }
        if ((d = controlStatusResponse.getCurrentPower()) != null) {
            updateState(CURRENT_POWER, new QuantityType<>(d, Units.WATT));
        }
        if ((d = controlStatusResponse.getControlSignal()) != null) {
            updateState(CONTROL_SIGNAL, new QuantityType<>(d, Units.PERCENT));
        }
        if ((d = controlStatusResponse.getRawAmbientTemperature()) != null) {
            updateState(RAW_AMBIENT_TEMPERATURE, new QuantityType<>(d, SIUnits.CELSIUS));
        }
        LockStatus ls;
        if ((ls = controlStatusResponse.getLockActive()) != null) {
            updateState(LOCK_STATUS, new StringType(ls.name()));
            updateState(CHILD_LOCK, ls == LockStatus.CHILD_LOCK ? OnOffType.ON : OnOffType.OFF);
        }
        OpenWindowStatus ows;
        if ((ows = controlStatusResponse.getOpenWindowStatus()) != null) {
            updateState(OPEN_WINDOW_STATUS, new StringType(ows.name()));
        }
        if ((d = controlStatusResponse.getSetTemperature()) != null) {
            updateState(SET_TEMPERATURE, new QuantityType<>(d, SIUnits.CELSIUS));
        }
        Boolean b;
        if ((b = controlStatusResponse.getConnectedToCloud()) != null) {
            updateState(CONNECTED_CLOUD, b.booleanValue() ? OnOffType.ON : OnOffType.OFF);
        }
        OperationMode om;
        if ((om = controlStatusResponse.getOperatingMode()) != null) {
            updateState(OPERATION_MODE, new StringType(om.name()));
        }
    }

    public void pollOperationMode() throws MillException {
        OperationModeResponse operationModeResponse;
        try {
            operationModeResponse = apiTool.getOperationMode(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support operation mode", getThing().getUID());
                return;
            }
            throw e;
        }
        OperationMode om;
        if ((om = operationModeResponse.getMode()) != null) {
            updateState(OPERATION_MODE, new StringType(om.name()));
        }
    }

    public void setOperationMode(@Nullable String modeValue) throws MillException {
        OperationMode mode = OperationMode.typeOf(modeValue);
        if (mode == null) {
            logger.warn("setOperationMode() received an invalid operation mode {} - ignoring", modeValue);
            return;
        }

        Response response = apiTool.setOperationMode(getHostname(), mode);
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set operation mode to \"{}\": {}",
                mode,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollTemperatureCalibrationOffset() throws MillException {
        TemperatureCalibrationOffsetResponse calibrationOffsetResponse = apiTool.getTemperatureCalibrationOffset(getHostname());
        setOnline();
        Double d;
        if ((d = calibrationOffsetResponse.getValue()) != null) {
            updateState(TEMPERATURE_CALIBRATION_OFFSET, new QuantityType<>(d, SIUnits.CELSIUS));
        }
    }

    public void setTemperatureCalibrationOffset(BigDecimal offset) throws MillException {
        Response response = apiTool.setTemperatureCalibrationOffset(getHostname(), offset);
        pollTemperatureCalibrationOffset();
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set temperature calibration offset to \"{}\": {}",
                offset,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollCommercialLock() throws MillException {
        CommercialLockResponse commercialLockResponse;
        try {
            commercialLockResponse = apiTool.getCommercialLock(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support commercial lock", getThing().getUID());
                return;
            }
            throw e;
        }
        Boolean b;
        if ((b = commercialLockResponse.getValue()) != null) {
            updateState(COMMERCIAL_LOCK, b.booleanValue() ? OnOffType.ON : OnOffType.OFF);
        }
    }

    public void setCommercialLock(Boolean value) throws MillException {
        Response response = apiTool.setCommercialLock(getHostname(), value);
        pollCommercialLock();
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set commercial-lock to \"{}\": {}",
                value,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollChildLock() throws MillException {
        ChildLockResponse childLockResponse;
        try {
            childLockResponse = apiTool.getChildLock(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support child lock", getThing().getUID());
                return;
            }
            throw e;
        }
        Boolean b;
        if ((b = childLockResponse.getValue()) != null) {
            updateState(CHILD_LOCK, b.booleanValue() ? OnOffType.ON : OnOffType.OFF);
        }
    }

    public void setChildLock(Boolean value) throws MillException {
        Response response = apiTool.setChildLock(getHostname(), value);
        pollChildLock();
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set child-lock to \"{}\": {}",
                value,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollDisplayUnit() throws MillException {
        DisplayUnitResponse displayUnitResponse;
        try {
            displayUnitResponse = apiTool.getDisplayUnit(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support display unit", getThing().getUID());
                return;
            }
            throw e;
        }
        DisplayUnit du;
        if ((du = displayUnitResponse.getDisplayUnit()) != null) {
            updateState(DISPLAY_UNIT, new StringType(du.name()));
        }
    }

    public void setDisplayUnit(@Nullable String unitValue) throws MillException {
        DisplayUnit displayUnit = DisplayUnit.typeOf(unitValue);
        if (displayUnit == null) {
            logger.warn("setDisplayUnit() received an invalid unit value {} - ignoring", unitValue);
            return;
        }

        Response response = apiTool.setDisplayUnit(getHostname(), displayUnit);
        pollDisplayUnit();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set display unit to \"{}\": {}",
                displayUnit,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollSetTemperature(String channel, TemperatureType temperatureType) throws MillException {
        SetTemperatureResponse setTemperatureResponse = apiTool.getSetTemperature(getHostname(), temperatureType);
        setOnline();
        BigDecimal bd;
        if ((bd = setTemperatureResponse.getSetTemperature()) != null) {
            updateState(channel, new QuantityType<>(bd, SIUnits.CELSIUS));
        }
    }

    public void setSetTemperature(String channel, TemperatureType temperatureType, BigDecimal value) throws MillException {
        Response response = apiTool.setSetTemperature(getHostname(), temperatureType, value);
        pollSetTemperature(channel, temperatureType);
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set {} set-temperature to \"{}\": {}",
                temperatureType.name(),
                value,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollLimitedHeatingPower() throws MillException {
        LimitedHeatingPowerResponse heatingPowerResponse;
        try {
            heatingPowerResponse = apiTool.getLimitedHeatingPower(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support limited heating power", getThing().getUID());
                return;
            }
            throw e;
        }
        Integer i;
        if ((i = heatingPowerResponse.getValue()) != null) {
            updateState(LIMITED_HEATING_POWER, new PercentType(i.intValue()));
        }
    }

    public void setLimitedHeatingPower(Integer value) throws MillException {
        Response response = apiTool.setLimitedHeatingPower(getHostname(), value);
        pollLimitedHeatingPower();
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set limited heating power to \"{}\": {}",
                value,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollControllerType() throws MillException {
        ControllerTypeResponse controllerTypeResponse;
        try {
            controllerTypeResponse = apiTool.getControllerType(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support controller type", getThing().getUID());
                return;
            }
            throw e;
        }
        ControllerType ct;
        if ((ct = controllerTypeResponse.getControllerType()) != null) {
            updateState(CONTROLLER_TYPE, new StringType(ct.name()));
        }
    }

    public void setControllerType(@Nullable String controllerTypeValue) throws MillException {
        ControllerType controllerType = ControllerType.typeOf(controllerTypeValue);
        if (controllerType == null) {
            logger.warn("setControllerType() received an invalid controller type value {} - ignoring", controllerTypeValue);
            return;
        }

        Response response = apiTool.setControllerType(getHostname(), controllerType);
        pollControllerType();
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set controller type to \"{}\": {}",
                controllerType,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollPredictiveHeatingType() throws MillException {
        PredictiveHeatingTypeResponse response;
        try {
            response = apiTool.getPredictiveHeatingType(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support predictive heating type", getThing().getUID());
                return;
            }
            throw e;
        }
        PredictiveHeatingType pht;
        if ((pht = response.getPredictiveHeatingType()) != null) {
            updateState(PREDICTIVE_HEATING_TYPE, new StringType(pht.name()));
        }
    }

    public void setPredictiveHeatingType(@Nullable String typeValue) throws MillException {
        PredictiveHeatingType type = PredictiveHeatingType.typeOf(typeValue);
        if (type == null) {
            logger.warn("setPredictiveHeatingType() received an invalid predictive heating type value {} - ignoring", typeValue);
            return;
        }

        Response response = apiTool.setPredictiveHeatingType(getHostname(), type);
        pollPredictiveHeatingType();
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set predictive heating type to \"{}\": {}",
                type,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    public void pollOilHeaterPower() throws MillException {
        OilHeaterPowerResponse heatingPowerResponse;
        try {
            heatingPowerResponse = apiTool.getOilHeaterPower(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support oil heater power", getThing().getUID());
                return;
            }
            throw e;
        }
        Integer i;
        if ((i = heatingPowerResponse.getValue()) != null) {
            updateState(OIL_HEATER_POWER, new PercentType(i.intValue()));
        }
    }

    public void setOilHeaterPower(Integer value) throws MillException {
        Response response = apiTool.setLimitedHeatingPower(getHostname(), value);
        pollOilHeaterPower();
        pollControlStatus();

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set limited heating power to \"{}\": {}",
                value,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
    }

    @Nullable
    public Integer pollTimeZoneOffset(boolean updateConfiguration) throws MillException {
        TimeZoneOffsetResponse offset;
        try {
            offset = apiTool.getTimeZoneOffset(getHostname());
            setOnline();
        } catch (MillHTTPResponseException e) {
            // API function not implemented
            if (HttpStatus.isClientError(e.getHttpStatus())) {
                logger.warn("Thing \"{}\" doesn't seem to support timezone offset", getThing().getUID());
                return null;
            }
            throw e;
        }
        Integer i;
        if ((i = offset.getOffset()) != null) {
            Thing thing = getThing();
            configDescriptionProvider.enableDescriptions(thing.getUID(), CONFIG_PARAM_TIMEZONE_OFFSET);
            if (updateConfiguration) {
                Configuration configuration = editConfiguration();
                Object object = configuration.get(CONFIG_PARAM_TIMEZONE_OFFSET);
                if (!(object instanceof Number) || ((Number) object).intValue() != i.intValue()) {
                    configuration.put(CONFIG_PARAM_TIMEZONE_OFFSET, BigDecimal.valueOf(i));
                    updateConfiguration(configuration);
                }
            }
        }
        return i;
    }

    @Nullable
    public Integer setTimeZoneOffset(Integer value, boolean updateConfiguration) throws MillException {
        Response response = apiTool.setTimeZoneOffset(getHostname(), value);
        Integer result = pollTimeZoneOffset(updateConfiguration);

        // Set status after polling, or it will be overwritten
        ResponseStatus responseStatus;
        if ((responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to set timezone offset to \"{}\": {}",
                value,
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOnline();
        }
        return result;
    }

    public void sendReboot() throws MillException {
        Response response = null;
        try {
            response = apiTool.sendReboot(getHostname());
        } catch (MillException e) {
            if (!(e.getCause() instanceof TimeoutException)) {
                throw e;
            }
        }
        ResponseStatus responseStatus;
        if (response != null && (responseStatus = response.getStatus()) != ResponseStatus.OK) {
            logger.warn(
                "Failed to send reboot command to \"{}\": {}",
                getThing().getUID(),
                responseStatus == null ? null : responseStatus.getDescription()
            );
            setOnline(
                ThingStatusDetail.COMMUNICATION_ERROR,
                responseStatus == null ? null : responseStatus.getDescription()
            );
        } else {
            setOffline(ThingStatusDetail.CONFIGURATION_PENDING, "Device is rebooting");

            // The devices reboots relatively quickly, so let's do a couple off one-off
            // offline polls to set it online again quickly
            InetAddress[] addresses = resolveOfflineAddresses();
            if (addresses != null) {
                scheduler.schedule(createOfflineTask(addresses), 8L, TimeUnit.SECONDS);
                scheduler.schedule(createOfflineTask(addresses), 12L, TimeUnit.SECONDS);
            }
        }
    }

    protected boolean isOnline() {
        synchronized (lock) {
            return isOnline;
        }
    }

    protected void setOnline() {
        setOnline(null, null);
    }

    protected void setOnline(@Nullable ThingStatusDetail statusDetail, @Nullable String description) {
        boolean isError = statusDetail != null && statusDetail != ThingStatusDetail.NONE;
        synchronized (lock) {
            // setOnline is called a lot, and most of the times there's nothing to do, so we want a quick escape early
            if (isOnline && !isError && !onlineWithError) {
                return;
            }
        }

        int refreshInterval;
        try {
            refreshInterval = getRefreshInterval();
        } catch (MillException e) {
            logger.error(
                "Unable to schedule polling for Mill device \"{}\" because the refresh interval is missing",
                getThing().getUID()
            );
            ThingStatusDetail tsd = e.getThingStatusDetail();
            String desc = e.getThingStatusDescription();
            updateStatus(
                ThingStatus.ONLINE,
                tsd != null ? tsd : ThingStatusDetail.CONFIGURATION_ERROR,
                desc != null ? desc : "Missing refresh interval"
            );
            refreshInterval = -1;
        }

        int infrequentRefreshInterval;
        try {
            infrequentRefreshInterval = getInfrequentRefreshInterval();
        } catch (MillException e) {
            logger.error(
                "Unable to schedule infrequent polling for Mill device \"{}\" because the refresh interval is missing",
                getThing().getUID()
            );
            ThingStatusDetail tsd = e.getThingStatusDetail();
            String desc = e.getThingStatusDescription();
            updateStatus(
                ThingStatus.ONLINE,
                tsd != null ? tsd : ThingStatusDetail.CONFIGURATION_ERROR,
                desc != null ? desc : "Missing infrequent refresh interval"
            );
            infrequentRefreshInterval = -1;
        }

        ScheduledFuture<?> frequentFuture, infrequentFuture, offlineFuture;
        boolean wasOnline;
        synchronized (lock) {
            wasOnline = isOnline;
            isOnline = true;
            onlineWithError = isError;
            frequentFuture = frequentPollTask;
            if (!isDisposed && refreshInterval > 0) {
                frequentPollTask = scheduler.scheduleWithFixedDelay(
                    createFrequentTask(),
                    0L,
                    refreshInterval,
                    TimeUnit.SECONDS
                );
            } else {
                frequentPollTask = null;
            }
            infrequentFuture = infrequentPollTask;
            if (!isDisposed && infrequentRefreshInterval > 0) {
                infrequentPollTask = scheduler.scheduleWithFixedDelay(
                    createInfrequentTask(),
                    700L,
                    infrequentRefreshInterval * 1000L,
                    TimeUnit.MILLISECONDS
                );
            } else {
                infrequentPollTask = null;
            }
            offlineFuture = offlinePollTask;
            offlinePollTask = null;
        }
        if (frequentFuture != null) {
            frequentFuture.cancel(true);
        }
        if (infrequentFuture != null) {
            infrequentFuture.cancel(true);
        }
        if (offlineFuture != null) {
            offlineFuture.cancel(true);
        }

        if (!wasOnline) {
            if (refreshInterval > 0) {
                logger.debug("Mill device \"{}\" is online, starting polling", getThing().getUID());
            }

            // Clear dynamic configuration parameters and properties
            Map<String, String> properties = editProperties();
            for (String property : PROPERTIES_DYNAMIC) {
                properties.remove(property);
            }
            updateProperties(properties);
            Configuration configuration = editConfiguration();
            for (String parameter : CONFIG_DYNAMIC_PARAMETERS) {
                configuration.remove(parameter);
            }
            updateConfiguration(configuration);
        }

        if (refreshInterval > 0) {
            if (isError && statusDetail != null) {
                updateStatus(ThingStatus.ONLINE, statusDetail, description);
            } else {
                updateStatus(ThingStatus.ONLINE);
            }
        }
    }

    protected void setOffline(MillException e) {
        Object object;
        if (e.getCause() instanceof ConnectException) {
            setOffline(ThingStatusDetail.CONFIGURATION_ERROR, "Connection refused: Verify hostname and API key");
        } else if (
            e instanceof MillHTTPResponseException &&
            ((MillHTTPResponseException) e).getHttpStatus() == 500 &&
            (object = getConfig().get(CONFIG_PARAM_API_KEY)) != null &&
            object instanceof String && ((String) object).length() > 0
        ) {
            setOffline(ThingStatusDetail.CONFIGURATION_ERROR, "Request rejected: Verify API key");
        } else {
            setOffline(e.getThingStatusDetail(), e.getThingStatusDescription());
        }
    }

    protected void setOffline(@Nullable ThingStatusDetail statusDetail, @Nullable String description) {
        ThingStatusDetail detail = statusDetail;
        String desc = description;
        int refreshInterval;
        try {
            refreshInterval = getRefreshInterval();
        } catch (MillException e) {
            refreshInterval = -1;
            logger.warn(
                "Unable to poll offline Mill device \"{}\" because the configuration is missing or invalid: {}",
                getThing().getUID(),
                e.getMessage()
            );
            if (e.getThingStatusDetail() != null) {
                detail = e.getThingStatusDetail();
            }
            if (!isBlank(e.getThingStatusDescription())) {
                desc = e.getThingStatusDescription();
            }
        }

        InetAddress[] addresses = resolveOfflineAddresses();
        ScheduledFuture<?> frequentFuture, infrequentFuture, offlineFuture;
        boolean wasOnline;
        synchronized (lock) {
            wasOnline = isOnline || offlinePollTask == null;
            isOnline = false;
            frequentFuture = frequentPollTask;
            frequentPollTask = null;
            infrequentFuture = infrequentPollTask;
            infrequentPollTask = null;
            offlineFuture = offlinePollTask;
            if (
                !isDisposed &&
                addresses != null &&
                refreshInterval > 0
            ) {
                logger.debug("Mill device \"{}\" is offline, starting offline polling", getThing().getUID());
                offlinePollTask = scheduler.scheduleWithFixedDelay(
                    createOfflineTask(addresses),
                    1L,
                    refreshInterval,
                    TimeUnit.SECONDS
                );
            } else {
                offlinePollTask = null;
                if (logger.isDebugEnabled()) {
                    if (isDisposed) {
                        logger.debug(
                            "Not starting offline polling for Mill device \"{}\" because the handler is disposed",
                            getThing().getUID()
                        );
                    } else if (addresses == null) {
                        logger.debug(
                            "Not starting offline polling for Mill device \"{}\" because an IP address could not be resolved",
                            getThing().getUID()
                        );
                    } else {
                        logger.debug(
                            "Not starting offline polling for Mill device \"{}\" because the refresh interval is invalid",
                            getThing().getUID()
                        );
                    }
                }
            }
        }
        if (frequentFuture != null) {
            frequentFuture.cancel(true);
        }
        if (infrequentFuture != null) {
            infrequentFuture.cancel(true);
        }
        if (offlineFuture != null) {
            offlineFuture.cancel(true);
        }

        // Set the status regardless of the previous online state, in case the "reason" changed
        updateStatus(
            ThingStatus.OFFLINE,
            detail == null ? ThingStatusDetail.NONE : detail,
            isBlank(desc) ? null : desc
        );

        if (wasOnline) {
            configDescriptionProvider.disableDescriptions(getThing().getUID());
        }
    }

    protected String getHostname() throws MillException {
        Object object = getConfig().get(CONFIG_PARAM_HOSTNAME);
        if (!(object instanceof String)) {
            logger.warn("Configuration parameter hostname is \"{}\"", object);
            throw new MillException(
                "Invalid configuration: hostname must be a string",
                ThingStatusDetail.CONFIGURATION_ERROR
            );
        }
        String result = (String) object;
        if (isBlank(result)) {
            logger.warn("Configuration parameter hostname is blank");
            throw new MillException(
                "Invalid configuration: hostname can't be blank",
                ThingStatusDetail.CONFIGURATION_ERROR
            );
        }
        return result;
    }

    protected int getRefreshInterval() throws MillException {
        Object object = getConfig().get(CONFIG_PARAM_REFRESH_INTERVAL);
        if (!(object instanceof Number)) {
            logger.warn("Configuration parameter refresh interval is \"{}\"", object);
            throw new MillException(
                "Invalid configuration: refresh interval must be a number",
                ThingStatusDetail.CONFIGURATION_ERROR
            );
        }
        int i = ((Number) object).intValue();
        if (i <= 0) {
            logger.warn("Configuration parameter refresh interval must be positive ({})", object);
            throw new MillException(
                "Invalid configuration: refresh interval must be positive",
                ThingStatusDetail.CONFIGURATION_ERROR
            );
        }
        return i;
    }

    protected int getInfrequentRefreshInterval() throws MillException {
        Object object = getConfig().get(CONFIG_PARAM_INFREQUENT_REFRESH_INTERVAL);
        if (!(object instanceof Number)) {
            logger.warn("Configuration parameter infrequent refresh interval is \"{}\"", object);
            throw new MillException(
                "Invalid configuration: infrequent refresh interval must be a number",
                ThingStatusDetail.CONFIGURATION_ERROR
            );
        }
        int i = ((Number) object).intValue();
        if (i <= 0) {
            logger.warn("Configuration parameter infrequent refresh interval must be positive ({})", object);
            throw new MillException(
                "Invalid configuration: infrequent refresh interval must be positive",
                ThingStatusDetail.CONFIGURATION_ERROR
            );
        }
        return i;
    }

    protected InetAddress @Nullable [] resolveOfflineAddresses() {
        String hostname;
        try {
            hostname = getHostname();
        } catch (MillException e) {
            logger.warn(
                "Unable to poll offline Mill device \"{}\" because the configuration is missing or invalid: {}",
                getThing().getUID(),
                e.getMessage()
            );
            return null;
        }
        InetAddress[] result = null;
        if (isBlank(hostname)) {
            logger.warn(
                "Unable to poll offline Mill device \"{}\" because the hostname is blank",
                getThing().getUID()
            );
        } else {
            try {
                result = InetAddress.getAllByName(hostname);
            } catch (UnknownHostException e) {
                logger.warn(
                    "Unable to poll offline Mill device \"{}\" because the hostname ({}) is unresolvable: {}",
                    getThing().getUID(),
                    hostname,
                    e.getMessage()
                );
            }
        }
        return result;
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        Configuration configuration = editConfiguration();
        Set<String> modifiedParameters = getModifiedParameters(configuration, configurationParameters);
        if (modifiedParameters.isEmpty()) {
            return;
        }

        ThingHandlerCallback callback = getCallback();
        if (callback == null) {
            logger.warn("Unable to update configuration since the callback is null");
            return;
        }
        callback.validateConfigurationParameters(getThing(), configurationParameters);

        boolean online = isOnline();
        if (modifiedParameters.contains(CONFIG_PARAM_HOSTNAME)) {
            configuration.put(CONFIG_PARAM_HOSTNAME, configurationParameters.get(CONFIG_PARAM_HOSTNAME));
        }
        if (modifiedParameters.contains(CONFIG_PARAM_API_KEY)) {
            configuration.put(CONFIG_PARAM_API_KEY, configurationParameters.get(CONFIG_PARAM_API_KEY));
        }
        if (modifiedParameters.contains(CONFIG_PARAM_REFRESH_INTERVAL)) {
            configuration.put(CONFIG_PARAM_REFRESH_INTERVAL, configurationParameters.get(CONFIG_PARAM_REFRESH_INTERVAL));
        }
        if (modifiedParameters.contains(CONFIG_PARAM_INFREQUENT_REFRESH_INTERVAL)) {
            configuration.put(CONFIG_PARAM_INFREQUENT_REFRESH_INTERVAL, configurationParameters.get(CONFIG_PARAM_INFREQUENT_REFRESH_INTERVAL));
        }
        if (modifiedParameters.contains(CONFIG_PARAM_TIMEZONE_OFFSET)) {
            handleTimeZoneOffsetUpdate(configuration, configurationParameters, online);
        }

        if ((
                modifiedParameters.contains(CONFIG_PARAM_HOSTNAME) ||
                modifiedParameters.contains(CONFIG_PARAM_API_KEY) ||
                modifiedParameters.contains(CONFIG_PARAM_REFRESH_INTERVAL) ||
                modifiedParameters.contains(CONFIG_PARAM_INFREQUENT_REFRESH_INTERVAL)
            ) &&
            isInitialized()
        ) {
            // Persist new configuration and reinitialize handler
            dispose();
            updateConfiguration(configuration);
            initialize();
        } else {
            // Persist new configuration and notify Thing Manager
            updateConfiguration(configuration);
            callback.configurationUpdated(getThing());
        }

        ConfigStatusCallback confStatusCallback = configStatusCallback;
        if (confStatusCallback != null) { //TODO: (Nad) Is this needed? This is already sent in updateConfiguration above
            confStatusCallback.configUpdated(new ThingConfigStatusSource(getThing().getUID().getAsString()));
        }
    }

    protected void handleTimeZoneOffsetUpdate(
        Configuration config,
        Map<String, Object> newParameters,
        boolean online
    ) {
        if (online) {
            try {
                Integer newValue;
                Object object = newParameters.get(CONFIG_PARAM_TIMEZONE_OFFSET);
                if (object instanceof Number) {
                    newValue = Integer.valueOf(((Number) object).intValue());
                } else {
                    logger.warn(
                        "Ignoring invalid new time zone offset {} for Thing \"{}\"",
                        object,
                        getThing().getUID()
                    );
                    return;
                }
                Integer result = setTimeZoneOffset(newValue, false);
                if (result == null) {
                    logger.warn(
                        "A null timezone offset value was received when attempting to set ({})",
                        newValue
                    );
                } else if (!result.equals(newValue)) {
                    logger.warn(
                        "The device returned a different timezone offset value ({}) than " +
                        "what was attempted set ({})",
                        result,
                        newValue
                    );
                } else {
                    config.put(CONFIG_PARAM_TIMEZONE_OFFSET, newValue);
                }
            } catch (MillException e) {
                logger.warn(
                    "An error occurred when trying to send time zone offset to {}: {}",
                    getThing().getUID(),
                    e.getMessage()
                );
            }
        } else {
            config.remove(CONFIG_PARAM_TIMEZONE_OFFSET);
        }
        return;
    }

    @Override
    protected void updateConfiguration(Configuration configuration) {
        super.updateConfiguration(configuration);
        ConfigStatusCallback confStatusCallback = configStatusCallback;
        if (confStatusCallback != null) {
            confStatusCallback.configUpdated(new ThingConfigStatusSource(getThing().getUID().getAsString()));
        }
    }

    @Override
    public abstract Collection<Class<? extends ThingHandlerService>> getServices();

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        return Collections.emptySet();
    }

    protected Set<String> getModifiedParameters(
        Configuration configuration,
        Map<String, Object> configurationParameters
    ) {
        Set<String> result = new HashSet<>();
        Object oldObject, newObject;
        for (Entry<String, Object> entry : configurationParameters.entrySet()) {
            newObject = entry.getValue();
            if (!Objects.equals(oldObject = configuration.get(entry.getKey()), newObject)) {
                if (oldObject instanceof BigDecimal && newObject instanceof BigDecimal) {
                    if (((BigDecimal) oldObject).compareTo((BigDecimal) newObject) != 0) {
                        result.add(entry.getKey());
                    }
                } else {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

    @Override
    public boolean supportsEntity(String entityId) {
        return getThing().getUID().getAsString().equals(entityId);
    }

    @Override
    public void setConfigStatusCallback(@Nullable ConfigStatusCallback configStatusCallback) {
        this.configStatusCallback = configStatusCallback;
    }

    protected Runnable createInitializeTask() {
        return new Initializer();
    }

    protected abstract Runnable createFrequentTask();

    protected abstract Runnable createInfrequentTask();

    protected Runnable createOfflineTask(InetAddress[] addresses) {
        return new PingOffline(addresses);
    }

    protected class Initializer implements Runnable {

        @Override
        public void run() {
            try {
                pollStatus();
            } catch (MillException e) {
                setOffline(e);
            }
        }
    }

    protected class PingOffline implements Runnable {

        private final InetAddress[] addresses;

        public PingOffline(InetAddress[] addresses) {
            this.addresses = addresses;
        }

        @Override
        public void run() {
            for (InetAddress address : addresses) {
                try {
                    if (address.isReachable(1000)) {
                        logger.debug(
                            "Mill device \"{}\" is reachable on {}, attempting to contact API",
                            getThing().getUID(),
                            address.getHostAddress()
                        );
                        scheduler.execute(() -> {
                            try {
                                pollControlStatus();
                            } catch (MillException e) {
                                logger.debug(
                                    "Attempt to contact API for Mill device \"{}\" failed: {}",
                                    getThing().getUID(),
                                    e.getMessage()
                                );
                            }
                        });
                    } else {
                        logger.debug(
                            "Mill device \"{}\" is not reachable on {}",
                            getThing().getUID(),
                            address.getHostAddress()
                        );
                    }
                } catch (IOException e) {
                    logger.warn(
                        "An IOException occurred while pinging offline Mill device {}: {}",
                        getThing().getLabel(),
                        e.getMessage()
                    );
                }
            }
        }
    }
}
