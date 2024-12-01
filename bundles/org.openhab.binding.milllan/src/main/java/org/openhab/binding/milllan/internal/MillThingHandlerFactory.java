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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.configuration.MillConfigDescriptionProvider;
import org.openhab.binding.milllan.internal.http.MillHTTPClientProvider;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link MillThingHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.milllan", service = ThingHandlerFactory.class)
public class MillThingHandlerFactory extends BaseThingHandlerFactory { // TODO: (Nad) JavaDocs

    private final MillConfigDescriptionProvider configDescriptionProvider;
    private final MillHTTPClientProvider httpClientProvider;

    @Activate
    public MillThingHandlerFactory(
        @Reference MillConfigDescriptionProvider configDescriptionProvider,
        @Reference MillHTTPClientProvider httpClientProvider,
        ComponentContext componentContext
    ) {
        super.activate(componentContext);
        this.configDescriptionProvider = configDescriptionProvider;
        this.httpClientProvider = httpClientProvider;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_PANEL_HEATER.equals(thingTypeUID)) {
            return new MillPanelHeaterHandler(thing, configDescriptionProvider, httpClientProvider);
        }
        if (THING_TYPE_CONVECTION_HEATER.equals(thingTypeUID)) {
            return new MillConvectionHeaterHandler(thing, configDescriptionProvider, httpClientProvider);
        }
        if (THING_TYPE_OIL_HEATER.equals(thingTypeUID)) {
            return new MillConvectionHeaterHandler(thing, configDescriptionProvider, httpClientProvider);
        }
        if (THING_TYPE_WIFI_SOCKET.equals(thingTypeUID)) {
            return new MillWiFiSocketHandler(thing, configDescriptionProvider, httpClientProvider);
        }
        if (THING_TYPE_ALL_FUNCTIONS.equals(thingTypeUID)) {
            return new MillAllFunctionsHandler(thing, configDescriptionProvider, httpClientProvider);
        }

        return null;
    }
}
