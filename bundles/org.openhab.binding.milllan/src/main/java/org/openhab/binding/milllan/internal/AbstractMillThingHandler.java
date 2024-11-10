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

import java.net.ConnectException;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.api.LockStatus;
import org.openhab.binding.milllan.internal.api.MillAPITool;
import org.openhab.binding.milllan.internal.api.OpenWindowStatus;
import org.openhab.binding.milllan.internal.api.OperationMode;
import org.openhab.binding.milllan.internal.api.ResponseStatus;
import org.openhab.binding.milllan.internal.api.response.ControlStatusResponse;
import org.openhab.binding.milllan.internal.api.response.OperationModeResponse;
import org.openhab.binding.milllan.internal.api.response.Response;
import org.openhab.binding.milllan.internal.api.response.StatusResponse;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.exception.MillHTTPResponseException;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.library.types.OnOffType;
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
 * The {@link AbstractMillThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractMillThingHandler extends BaseThingHandler { // TODO: (Nad) JavaDocs

    private final Logger logger = LoggerFactory.getLogger(AbstractMillThingHandler.class);

    protected final MillHTTPClientProvider httpClientProvider;

    /** The object used for synchronization of class fields */
    protected final Object lock = new Object();

    /** Current online state, must be synchronized on {@link #lock} */
    protected boolean isOnline;

    // must be synched on lock
    protected boolean onlineWithError;

    protected final MillAPITool apiTool;

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
        OperationModeResponse operationModeResponse = apiTool.getOperationMode(getHostname());
        setOnline();
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

    protected void setOnline() {
        setOnline(null, null);
    }

    protected void setOnline(@Nullable ThingStatusDetail statusDetail, @Nullable String description) {
        boolean isError = statusDetail != null && statusDetail != ThingStatusDetail.NONE;
        synchronized (lock) {
            // setOnline is called a lot, and most of the times there's nothing to do, so we want a quick escape first
            if (isOnline && !isError && !onlineWithError) {
                return;
            }
            isOnline = true;
            onlineWithError = isError;
        }
        //TODO: (Nad) Clear hostname and refreshInterval errors..?

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
            //TODO: (Nad) Do schedulers etc
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
}
