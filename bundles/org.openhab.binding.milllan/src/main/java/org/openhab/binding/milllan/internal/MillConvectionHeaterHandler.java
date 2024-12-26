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

import java.util.Collection;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.milllan.internal.action.MillConvectionActions;
import org.openhab.binding.milllan.internal.api.TemperatureType;
import org.openhab.binding.milllan.internal.configuration.MillConfigDescriptionProvider;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.ThingHandlerService;


/**
 * The Thing handler for the {@code Convection Heater} thing type.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillConvectionHeaterHandler extends AbstractMillThingHandler {

    /**
     * Creates a new instance using the specified parameters.
     *
     * @param thing the {@link Thing} for which to create a handler.
     * @param configDescriptionProvider the {@link MillConfigDescriptionProvider} to use.
     * @param httpClientProvider the {@link MillHTTPClientProvider} to use.
     */
    public MillConvectionHeaterHandler(
        Thing thing,
        MillConfigDescriptionProvider configDescriptionProvider,
        MillHTTPClientProvider httpClientProvider
    ) {
        super(thing, configDescriptionProvider, httpClientProvider);
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(MillConvectionActions.class);
    }

    @Override
    protected Runnable createFrequentTask() {
        return new PollFrequent();
    }

    @Override
    protected Runnable createInfrequentTask() {
        return new PollInfrequent();
    }

    /**
     * The {@link Runnable} used for frequent polls.
     */
    protected class PollFrequent implements Runnable {

        @Override
        public void run() {
            try {
                pollControlStatus();
                pollSetTemperature(CHANNEL_NORMAL_SET_TEMPERATURE, TemperatureType.NORMAL);
                pollSetTemperature(CHANNEL_COMFORT_SET_TEMPERATURE, TemperatureType.COMFORT);
                pollSetTemperature(CHANNEL_SLEEP_SET_TEMPERATURE, TemperatureType.SLEEP);
                pollSetTemperature(CHANNEL_AWAY_SET_TEMPERATURE, TemperatureType.AWAY);
            } catch (MillException e) {
                setOffline(e);
            }
        }
    }

    /**
     * The {@link Runnable} used for infrequent polls.
     */
    protected class PollInfrequent implements Runnable {

        @Override
        public void run() {
            try {
                pollStatus();
                pollTemperatureCalibrationOffset();
                pollDisplayUnit();
                pollPredictiveHeatingType();
                pollTimeZoneOffset(true);
                pollCloudCommunication(true);
                pollHysteresisParameters(true);
                pollCommercialLock();
                /*
                 * Commercial lock functionality seems to be completely broken, at least in firmware
                 * 0x230630. It's thus commented out here as trying to use it will only
                 * lead to frustration. If this changes in the future, a logic that
                 * looks at the firmware and enables it in working versions could be implemented> here.
                 *
                 * The disabled call is: pollCommercialLockCustomization(true);
                 * If enabled, pollCommercialLock() can be disabled, as the commercial lock state is also
                 * fetched in pollCommercialLockCustomization()
                 */
                pollOpenWindowParameters(true);
            } catch (MillException e) {
                setOffline(e);
            }
        }
    }
}
