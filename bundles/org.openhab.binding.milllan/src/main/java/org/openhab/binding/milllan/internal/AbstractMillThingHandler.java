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

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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
import org.openhab.binding.milllan.internal.api.response.OperationModeResponse;
import org.openhab.binding.milllan.internal.api.response.PredictiveHeatingTypeResponse;
import org.openhab.binding.milllan.internal.api.response.Response;
import org.openhab.binding.milllan.internal.api.response.SetTemperatureResponse;
import org.openhab.binding.milllan.internal.api.response.StatusResponse;
import org.openhab.binding.milllan.internal.api.response.TemperatureCalibrationOffsetResponse;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.exception.MillHTTPResponseException;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
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
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The {@link AbstractMillThingHandler} is the common {@link ThingHandler} for all {@link ThingType}s of this
 * binding, where most of the communications between openHAB and the device takes place.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractMillThingHandler extends BaseThingHandler implements ConfigStatusProvider {

    private final Logger logger = LoggerFactory.getLogger(AbstractMillThingHandler.class);

    /** The {@link ConfigStatusCallback} */
    @Nullable
    protected ConfigStatusCallback configStatusCallback;

    /** The {@link MillHTTPClientProvider} */
    protected final MillHTTPClientProvider httpClientProvider;

    /** The object used for synchronization of most class fields */
    protected final Object lock = new Object();

    /** Current online state, <b>must be synchronized</b> on {@link #lock}! */
    protected boolean isOnline;

    /** Whether the current online state is with an error, <b>must be synchronized</b> on {@link #lock}! */
    protected boolean onlineWithError;

    /** The {@link MillAPITool} instance */
    protected final MillAPITool apiTool;

    /**
     * Creates a new instance using the specified parameters.
     *
     * @param thing the {@link Thing} for which to create a handler.
     * @param httpClientProvider the {@link MillHTTPClientProvider} to use.
     */
    public AbstractMillThingHandler(Thing thing, MillHTTPClientProvider httpClientProvider) {
        super(thing);
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
                        QuantityType<?> celsiusOffset =
                            ((QuantityType<Temperature>) command).toUnitRelative(SIUnits.CELSIUS);
                        if (celsiusOffset == null) {
                            logger.warn(
                                "Failed to set temperature calibration offset: Could not convert {} to degrees celsius",
                                command
                            );
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
                            logger.warn(
                                "Failed to set \"normal\" set-temperature: Could not convert {} to degrees celsius",
                                command
                            );
                        } else {
                            setSetTemperature(
                                NORMAL_SET_TEMPERATURE,
                                TemperatureType.NORMAL,
                                celsiusValue.toBigDecimal()
                            );
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
                            logger.warn(
                                "Failed to set \"comfort\" set-temperature: Could not convert {} to degrees celsius",
                                command
                            );
                        } else {
                            setSetTemperature(
                                COMFORT_SET_TEMPERATURE,
                                TemperatureType.COMFORT,
                                celsiusValue.toBigDecimal()
                            );
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
                            logger.warn(
                                "Failed to set \"sleep\" set-temperature: Could not convert {} to degrees celsius",
                                command
                            );
                        } else {
                            setSetTemperature(
                                SLEEP_SET_TEMPERATURE,
                                TemperatureType.SLEEP,
                                celsiusValue.toBigDecimal()
                            );
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
                            logger.warn(
                                "Failed to set \"away\" set-temperature: Could not convert {} to degrees celsius",
                                command
                            );
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
        updateStatus(ThingStatus.UNKNOWN);
        scheduler.execute(() -> {
            try {
                pollStatus();
                pollControlStatus();
                pollTemperatureCalibrationOffset();
                pollCommercialLock();
                pollDisplayUnit();
                pollSetTemperature(NORMAL_SET_TEMPERATURE, TemperatureType.NORMAL);
                pollSetTemperature(COMFORT_SET_TEMPERATURE, TemperatureType.COMFORT);
                pollSetTemperature(SLEEP_SET_TEMPERATURE, TemperatureType.SLEEP);
                pollSetTemperature(AWAY_SET_TEMPERATURE, TemperatureType.AWAY);
                pollLimitedHeatingPower();
                pollControllerType();
            } catch (MillException e) {
                setOffline(e);
            }
    });
    }

    @Override
    public void dispose() {
        if (logger.isTraceEnabled()) {
            logger.trace("Disposing of Thing handler for {}", getThing().getUID());
        }
    }

    /**
     * Retrieves the device status and updates the affected properties if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
    public void pollStatus() throws MillException {
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

    /**
     * Retrieves the device control status and updates the {@link Channel}s if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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
        if ((ls = controlStatusResponse.getLockStatus()) != null) {
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

    /**
     * Retrieves the {@link OperationMode} and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends the operation mode value to the device and immediately queries the device for
     * the same value, so that the result of the operation is known.
     *
     * @param modeValue the operation mode value {@link String}. Must be a valid {@link OperationMode}
     *                  or no action is taken.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Retrieves the temperature calibration offset and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
    public void pollTemperatureCalibrationOffset() throws MillException {
        TemperatureCalibrationOffsetResponse calibrationOffsetResponse = apiTool.getTemperatureCalibrationOffset(
            getHostname()
        );
        setOnline();
        Double d;
        if ((d = calibrationOffsetResponse.getValue()) != null) {
            updateState(TEMPERATURE_CALIBRATION_OFFSET, new QuantityType<>(d, SIUnits.CELSIUS));
        }
    }

    /**
     * Sends the specified temperature calibration offset value to the device and immediately queries
     * the device for the same value, so that the result of the operation is known.
     *
     * @param offset the temperature calibration offset in °C.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Retrieves the commercial lock state and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends the specified commercial lock enabled value to the device and immediately queries the
     * device for the same value, so that the result of the operation is known.
     *
     * @param value the commercial lock enabled value.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Retrieves the child lock state and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends the specified child lock enabled value value to the device and immediately queries the device for
     * the same value, so that the result of the operation is known.
     *
     * @param value the child lock enabled value.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Retrieves the {@link DisplayUnit} and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends the specified display unit value to the device and immediately queries the device for
     * the same value, so that the result of the operation is known.
     *
     * @param unitValue the display unit value {@link String}. Must be a valid {@link DisplayUnit}
     *                  or no action is taken.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Retrieves the set-temperature value in °C and updates the {@link Channel} if necessary.
     *
     * @param channel the ID of the {@link Channel} to update.
     * @param temperatureType the {@link TemperatureType} to retrieve.
     * @throws MillException If an error occurs during the operation.
     */
    public void pollSetTemperature(String channel, TemperatureType temperatureType) throws MillException {
        SetTemperatureResponse setTemperatureResponse = apiTool.getSetTemperature(getHostname(), temperatureType);
        setOnline();
        BigDecimal bd;
        if ((bd = setTemperatureResponse.getSetTemperature()) != null) {
            updateState(channel, new QuantityType<>(bd, SIUnits.CELSIUS));
        }
    }

    /**
     * Sends the specified set-temperature and {@link TemperatureType} values to the device and immediately
     * queries the device for the same value, so that the result of the operation is known.
     *
     * @param channel the ID of the {@link Channel} to update.
     * @param temperatureType the {@link TemperatureType} to set.
     * @param value the new set-temperature in °C.
     * @throws MillException If an error occurs during the operation.
     */
    public void setSetTemperature(
        String channel,
        TemperatureType temperatureType,
        BigDecimal value
    ) throws MillException {
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

    /**
     * Retrieves the limited heating power value and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends the specified limited heating power value to the device and immediately queries the device for
     * the same value, so that the result of the operation is known.
     *
     * @param value the limited heating power percentage value.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Retrieves the {@link ControllerType} and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends the specified controller type value to the device and immediately queries the device for
     * the same value, so that the result of the operation is known.
     *
     * @param controllerTypeValue the controller type value {@link String}. Must be a valid
     *                  {@link ControllerType} or no action is taken.
     * @throws MillException If an error occurs during the operation.
     */
    public void setControllerType(@Nullable String controllerTypeValue) throws MillException {
        ControllerType controllerType = ControllerType.typeOf(controllerTypeValue);
        if (controllerType == null) {
            logger.warn(
                "setControllerType() received an invalid controller type value {} - ignoring",
                controllerTypeValue
            );
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

    /**
     * Retrieves the {@link PredictiveHeatingType} and updates the {@link Channel} if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends the specified predictive heating type value to the device and immediately queries the device for
     * the same value, so that the result of the operation is known.
     *
     * @param typeValue the predictive heating type value {@link String}. Must be a valid
     *                  {@link PredictiveHeatingType} or no action is taken.
     * @throws MillException If an error occurs during the operation.
     */
    public void setPredictiveHeatingType(@Nullable String typeValue) throws MillException {
        PredictiveHeatingType type = PredictiveHeatingType.typeOf(typeValue);
        if (type == null) {
            logger.warn(
                "setPredictiveHeatingType() received an invalid predictive heating type value {} - ignoring",
                typeValue
            );
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

    /**
     * Sets the {@link Thing} status to online without errors.
     */
    protected void setOnline() {
        setOnline(null, null);
    }

    /**
     * Sets the {@link Thing} status to online with errors.
     *
     * @param statusDetail the {@link ThingStatusDetail} to set.
     * @param description the error description to set.
     */
    protected void setOnline(@Nullable ThingStatusDetail statusDetail, @Nullable String description) {
        boolean isError = statusDetail != null && statusDetail != ThingStatusDetail.NONE;
        synchronized (lock) {
            // setOnline is called a lot, and most of the times there's nothing to do, so we want a quick escape early
            if (isOnline && !isError && !onlineWithError) {
                return;
            }
            isOnline = true;
            onlineWithError = isError;
        }

        // Clear dynamic configuration parameters and properties
        Map<String, String> properties = editProperties();
        for (String property : PROPERTIES_DYNAMIC) {
            properties.remove(property);
        }
        updateProperties(properties);

        if (isError && statusDetail != null) {
            updateStatus(ThingStatus.ONLINE, statusDetail, description);
        } else {
            updateStatus(ThingStatus.ONLINE);
        }
    }

    /**
     * Sets the {@link Thing} status to offline, retrieving the details from the specified {@link MillException}.
     *
     * @param e the {@link MillException} that caused the {@link Thing} to go offline.
     */
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

    /**
     * Sets the {@link Thing} status to offline with the specified details.
     *
     * @param statusDetail the {@link ThingStatusDetail} to set.
     * @param description the error description to set.
     */
    protected void setOffline(@Nullable ThingStatusDetail statusDetail, @Nullable String description) {
        ThingStatusDetail detail = statusDetail;
        String desc = description;
        boolean wasOnline;
        synchronized (lock) {
            wasOnline = isOnline;
            isOnline = false;
        }

        // Set the status regardless of the previous online state, in case the "reason" changed
        updateStatus(
            ThingStatus.OFFLINE,
            detail == null ? ThingStatusDetail.NONE : detail,
            isBlank(desc) ? null : desc
        );

        if (wasOnline) {
            // Update configuration parameters, properties etc
        }
    }

    /**
     * Gets the hostname from the current {@link Configuration}.
     *
     * @return The hostname.
     * @throws MillException If the hostname can't be retrieved or is invalid.
     */
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

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        return Collections.emptySet();
    }

    @Override
    public boolean supportsEntity(String entityId) {
        return getThing().getUID().getAsString().equals(entityId);
    }

    @Override
    public void setConfigStatusCallback(@Nullable ConfigStatusCallback configStatusCallback) {
        this.configStatusCallback = configStatusCallback;
    }
}
