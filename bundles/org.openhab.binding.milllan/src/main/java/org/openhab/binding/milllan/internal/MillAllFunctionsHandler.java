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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.milllan.internal.api.TemperatureType;
import org.openhab.binding.milllan.internal.exception.MillException;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.thing.Thing;


/**
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillAllFunctionsHandler extends AbstractMillThingHandler { //TODO: (Nad) JavaDocs

    public MillAllFunctionsHandler(
        Thing thing,
        MillHTTPClientProvider httpClientProvider
    ) {
        super(thing, httpClientProvider);
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
                pollOilHeaterPower();
                pollCommercialLock();
            } catch (MillException e) {
                setOffline(e);
            }
        }
    }
}
