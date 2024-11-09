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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.thing.Thing;


/**
 * The Thing handler for the {@code Panel Heater} thing type.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillPanelHeaterHandler extends AbstractMillThingHandler {

    /**
     * Creates a new instance using the specified parameters.
     *
     * @param thing the {@link Thing} for which to create a handler.
     * @param httpClientProvider the {@link MillHTTPClientProvider} to use.
     */
    public MillPanelHeaterHandler(
        Thing thing,
        MillHTTPClientProvider httpClientProvider
    ) {
        super(thing, httpClientProvider);
    }
}
