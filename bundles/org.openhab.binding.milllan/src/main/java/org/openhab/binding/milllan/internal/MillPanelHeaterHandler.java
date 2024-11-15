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
import org.openhab.binding.milllan.internal.configuration.MillConfigDescriptionProvider;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.thing.Thing;


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
}
