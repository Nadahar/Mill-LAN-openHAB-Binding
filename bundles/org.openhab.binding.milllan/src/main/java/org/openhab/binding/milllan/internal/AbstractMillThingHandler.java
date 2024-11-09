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

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.milllan.internal.api.MillAPITool;
import org.openhab.binding.milllan.internal.api.response.ControlStatusResponse;
import org.openhab.binding.milllan.internal.api.response.StatusResponse;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The {@link AbstractMillThingHandler} is the common {@link ThingHandler} for all {@link ThingType}s of this
 * binding, where most of the communications between openHAB and the device takes place.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractMillThingHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(AbstractMillThingHandler.class);

    /** The {@link MillHTTPClientProvider} */
    protected final MillHTTPClientProvider httpClientProvider;

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
        switch (channelUID.getId()) {
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
                ThingStatusDetail statusDetail = e.getThingStatusDetail();
                String statusDescription = e.getThingStatusDescription();
                if (statusDetail == null) {
                    statusDetail = ThingStatusDetail.NONE;
                }
                if (isBlank(statusDescription)) {
                    statusDescription = null;
                }
                updateStatus(ThingStatus.OFFLINE, statusDetail, statusDescription);
            }
        });
    }

    /**
     * Retrieves the device status and updates the affected properties if necessary.
     *
     * @throws MillException If an error occurs during the operation.
     */
    public void pollStatus() throws MillException {
        StatusResponse statusResponse = apiTool.getStatus(getHostname());
        Map<String, String> properties = editProperties();
        boolean changed = false;
        boolean removed = false;
        String s = statusResponse.getName();
        s = statusResponse.getCustomName();
        s = statusResponse.getVersion();
        if (s == null || isBlank(s)) {
            removed |= properties.remove(Thing.PROPERTY_FIRMWARE_VERSION) != null;
        } else if (!s.equals(properties.get(Thing.PROPERTY_FIRMWARE_VERSION))) {
            properties.put(Thing.PROPERTY_FIRMWARE_VERSION, s);
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
     */
    public void pollControlStatus() {
        try {
            ControlStatusResponse controlStatusResponse = apiTool.getControlStatus(getHostname());
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

            updateStatus(ThingStatus.ONLINE);
        } catch (MillException e) {
            ThingStatusDetail statusDetail = e.getThingStatusDetail();
            String statusDescription = e.getThingStatusDescription();
            if (statusDetail == null) {
                statusDetail = ThingStatusDetail.NONE;
            }
            if (isBlank(statusDescription)) {
                statusDescription = null;
            }
            updateStatus(ThingStatus.OFFLINE, statusDetail, statusDescription);
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
}
