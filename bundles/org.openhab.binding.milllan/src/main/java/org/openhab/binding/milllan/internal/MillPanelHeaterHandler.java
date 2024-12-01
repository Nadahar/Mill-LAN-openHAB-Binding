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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.milllan.internal.action.MillPanelActions;
import org.openhab.binding.milllan.internal.api.TemperatureType;
import org.openhab.binding.milllan.internal.configuration.MillConfigDescriptionProvider;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.ThingHandlerService;


/**
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillPanelHeaterHandler extends AbstractMillThingHandler { //TODO: (Nad) JavaDocs

    public MillPanelHeaterHandler(
        Thing thing,
        MillConfigDescriptionProvider configDescriptionProvider,
        MillHTTPClientProvider httpClientProvider
    ) {
        super(thing, configDescriptionProvider, httpClientProvider);
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(MillPanelActions.class);
    }

    @Override
    protected @NonNull Runnable createFrequentTask() {
        return new PollFrequent();
    }

    @Override
    protected @NonNull Runnable createInfrequentTask() {
        return new PollInfrequent();
    }

    protected class PollFrequent implements Runnable {

        @Override
        public void run() {
            try {
                pollControlStatus();
                pollSetTemperature(NORMAL_SET_TEMPERATURE, TemperatureType.NORMAL);
                pollSetTemperature(COMFORT_SET_TEMPERATURE, TemperatureType.COMFORT);
                pollSetTemperature(SLEEP_SET_TEMPERATURE, TemperatureType.SLEEP);
                pollSetTemperature(AWAY_SET_TEMPERATURE, TemperatureType.AWAY);
            } catch (MillException e) {
                setOffline(e);
            }
        }
    }

    protected class PollInfrequent implements Runnable {

        @Override
        public void run() {
            try {
                pollStatus();
                pollTemperatureCalibrationOffset();
                pollDisplayUnit();
                pollLimitedHeatingPower();
                pollControllerType();
                pollPredictiveHeatingType();
//                pollOilHeaterPower(); //TODO: (Nad) Oil only
                pollTimeZoneOffset(true);
                pollPIDParameters(true);
                pollCloudCommunication(true);
                pollHysteresisParameters(true); //TODO: (Nad) Which models?
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
                pollOpenWindowParameters(true); //TODO: (Nad) Which models?
            } catch (MillException e) {
                setOffline(e);
            }
        }
    }
}
