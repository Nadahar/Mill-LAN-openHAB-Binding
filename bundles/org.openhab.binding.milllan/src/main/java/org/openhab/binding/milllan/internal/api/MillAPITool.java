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
package org.openhab.binding.milllan.internal.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.milllan.internal.MillUtil;
import org.openhab.binding.milllan.internal.api.response.ChildLockResponse;
import org.openhab.binding.milllan.internal.api.response.CommercialLockResponse;
import org.openhab.binding.milllan.internal.api.response.ControlStatusResponse;
import org.openhab.binding.milllan.internal.api.response.ControllerTypeResponse;
import org.openhab.binding.milllan.internal.api.response.DisplayUnitResponse;
import org.openhab.binding.milllan.internal.api.response.GenericResponse;
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
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


/**
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillAPITool { // TODO: (Nad) JavaDocs

    private final Logger logger = LoggerFactory.getLogger(MillAPITool.class);
    private final MillHTTPClientProvider httpClientProvider;
    private final Gson gson = new GsonBuilder().create();

    public MillAPITool(MillHTTPClientProvider httpClientProvider) {
        this.httpClientProvider = httpClientProvider;
    }

    public StatusResponse getStatus(String hostname) throws MillException {
        return request(
            StatusResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/status",
            null,
            8L,
            TimeUnit.SECONDS,
            true
        );
    }

    public ControlStatusResponse getControlStatus(String hostname) throws MillException {
        return request(
            ControlStatusResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/control-status",
            null,
            8L,
            TimeUnit.SECONDS,
            true
        );
    }

    public OperationModeResponse getOperationMode(String hostname) throws MillException {
        return request(
            OperationModeResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/operation-mode",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setOperationMode(String hostname, OperationMode mode) throws MillException {
        JsonObject object = new JsonObject();
        object.add("mode", gson.toJsonTree(mode));
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/operation-mode",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    public TemperatureCalibrationOffsetResponse getTemperatureCalibrationOffset(String hostname) throws MillException {
        return request(
            TemperatureCalibrationOffsetResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/temperature-calibration-offset",
            null,
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setTemperatureCalibrationOffset(String hostname, BigDecimal offset) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("value", offset);
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/temperature-calibration-offset",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public CommercialLockResponse getCommercialLock(String hostname) throws MillException {
        return request(
            CommercialLockResponse.class,
            hostname,
            null,
            HttpMethod.GET, "/commercial-lock",
            null,
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setCommercialLock(String hostname, Boolean value) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("value", value);
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/commercial-lock",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public ChildLockResponse getChildLock(String hostname) throws MillException {
        return request(
            ChildLockResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/child-lock",
            null,
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setChildLock(String hostname, Boolean value) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("value", value);
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/child-lock",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public DisplayUnitResponse getDisplayUnit(String hostname) throws MillException {
        return request(
            DisplayUnitResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/display-unit",
            null,
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setDisplayUnit(String hostname, DisplayUnit displayUnit) throws MillException {
        JsonObject object = new JsonObject();
        object.add("value", gson.toJsonTree(displayUnit));
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/display-unit",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public SetTemperatureResponse getSetTemperature(String hostname, TemperatureType temperatureType) throws MillException {
        JsonObject object = new JsonObject();
        object.add("type", gson.toJsonTree(temperatureType));
        return request(
            SetTemperatureResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/set-temperature",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setSetTemperature(String hostname, TemperatureType temperatureType, BigDecimal value) throws MillException {
        JsonObject object = new JsonObject();
        object.add("type", gson.toJsonTree(temperatureType));
        object.addProperty("value", value);
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/set-temperature",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public LimitedHeatingPowerResponse getLimitedHeatingPower(String hostname) throws MillException {
        return request(
            LimitedHeatingPowerResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/limited-heating-power",
            null,
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setLimitedHeatingPower(String hostname, Integer value) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("limited_heating_power", value);
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/limited-heating-power",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public ControllerTypeResponse getControllerType(String hostname) throws MillException {
        return request(
            ControllerTypeResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/controller-type",
            null,
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setControllerType(String hostname, ControllerType controllerType) throws MillException {
        JsonObject object = new JsonObject();
        object.add("regulator_type", gson.toJsonTree(controllerType));
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/controller-type",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public PredictiveHeatingTypeResponse getPredictiveHeatingType(String hostname) throws MillException {
        return request(
            PredictiveHeatingTypeResponse.class,
            hostname,
            null,
            HttpMethod.GET,
            "/predictive-heating-type",
            null,
            1L,
            TimeUnit.SECONDS,
            true
        );
    }

    public Response setPredictiveHeatingType(String hostname, PredictiveHeatingType type) throws MillException {
        JsonObject object = new JsonObject();
        object.add("predictive_heating_type", gson.toJsonTree(type));
        return request(
            GenericResponse.class,
            hostname,
            null,
            HttpMethod.POST,
            "/predictive-heating-type",
            gson.toJson(object),
            1L,
            TimeUnit.SECONDS,
            false
        );
    }

    public <T extends Response> T request(
        Class<T> clazz,
        String hostname,
        @Nullable String apiKey,
        HttpMethod method,
        String path,
        @Nullable String content,
        long timeout,
        @Nullable TimeUnit timeUnit,
        boolean throwOnAPIStatus
    ) throws MillException {
        URI uri;
        String key = apiKey == null || MillUtil.isBlank(apiKey) ? null : apiKey;
        try {
            uri = new URI(apiKey == null ? "http" : "https", hostname, path, null);
        } catch (URISyntaxException e) {
            throw new MillException(
                "Invalid hostname \"" + hostname + '"',
                ThingStatusDetail.CONFIGURATION_ERROR,
                e
            );
        }
        Map<String, String> headers = null;
        if (key != null) {
            headers = new HashMap<>();
            headers.put("Authentication", key);
        }
        return request(clazz, uri, method, headers, content, timeout, timeUnit, true);
    }

    public <T extends Response> T request(
        Class<T> clazz,
        URI uri,
        HttpMethod method,
        @Nullable Map<String, String> headers,
        @Nullable String content,
        long timeout,
        @Nullable TimeUnit timeUnit,
        boolean throwOnAPIStatus //Doc: Throws an exception if the returned status isn't ok
    ) throws MillException {
        InputStream is = null;
        String contentType = null;
        if (content != null && MillUtil.isNotBlank(content)) {
            is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            contentType = "application/json";
        }

        boolean debug = logger.isDebugEnabled();
        if (debug) {
            StringBuilder sb = new StringBuilder("Sending HTTP ")
                .append(method).append(" request to \"").append(uri).append('"');
            if (content != null) {
                sb.append(" with Content=\"").append(content).append("\", ContentType=\"").append(contentType).append('"');
            }
            logger.debug("{}", sb.toString());
        }
        ContentResponse response = httpClientProvider.send(uri, method, headers, is, contentType, timeout, timeUnit);
        if (!HttpStatus.isSuccess(response.getStatus())) {
            throw new MillHTTPResponseException(
                response.getStatus(),
                ThingStatusDetail.COMMUNICATION_ERROR
            );
        }

        String encoding = response.getEncoding() != null ?
            response.getEncoding().replace("\"", "").trim() :
            StandardCharsets.UTF_8.name();
        String responseBody;
        try {
            responseBody = new String(response.getContent(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new MillHTTPResponseException(
                "Unsupported encoding: " + encoding,
                response.getStatus(),
                ThingStatusDetail.COMMUNICATION_ERROR,
                e
            );
        }
        if (debug) {
            StringBuilder sb = new StringBuilder("Received HTTP response ").append(response.getStatus())
                .append(" from \"").append(uri.getHost()).append('"');
            if (MillUtil.isNotBlank(responseBody)) {
                sb.append(" with Content=\"").append(responseBody).append("\", ContentType=\"").append(response.getMediaType()).append('"');
            }
            logger.debug("{}", sb.toString());
        }

        try {
            @Nullable
            T responseObject = gson.fromJson(responseBody, clazz);
            if (responseObject == null) {
                throw new MillException("No response", ThingStatusDetail.COMMUNICATION_ERROR);
            }
            if (throwOnAPIStatus) {
                ResponseStatus responseStatus;
                if ((responseStatus = responseObject.getStatus()) == null) {
                    throw new MillException("No response status", ThingStatusDetail.COMMUNICATION_ERROR);
                }
                if (responseStatus != ResponseStatus.OK) {
                    throw new MillException(
                        responseStatus.getDescription(),
                        ThingStatusDetail.COMMUNICATION_ERROR
                    );
                }
            }
            return responseObject;
        } catch (JsonParseException e) {
            throw new MillException(
                "JSON parsing failed: " + e.getMessage(),
                ThingStatusDetail.COMMUNICATION_ERROR,
                e
            );
        }
    }
}
