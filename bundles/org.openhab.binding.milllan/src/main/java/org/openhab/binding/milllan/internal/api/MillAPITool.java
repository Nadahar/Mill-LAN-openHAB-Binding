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
import org.openhab.binding.milllan.internal.api.response.ControlStatusResponse;
import org.openhab.binding.milllan.internal.api.response.GenericResponse;
import org.openhab.binding.milllan.internal.api.response.OperationModeResponse;
import org.openhab.binding.milllan.internal.api.response.Response;
import org.openhab.binding.milllan.internal.api.response.StatusResponse;
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
 * This class contains the actual calls to the device REST API.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillAPITool {

    private final Logger logger = LoggerFactory.getLogger(MillAPITool.class);
    private final MillHTTPClientProvider httpClientProvider;
    private final Gson gson = new GsonBuilder().create();

    /**
     * Creates a new instance.
     *
     * @param httpClientProvider the {@link MillHTTPClientProvider} to use.
     */
    public MillAPITool(MillHTTPClientProvider httpClientProvider) {
        this.httpClientProvider = httpClientProvider;
    }

    /**
     * Sends {@code GET/status} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @return The resulting {@link StatusResponse}.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends {@code GET/control-status} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @return The resulting {@link ControlStatusResponse}.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends {@code GET/operation-mode} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @return The resulting {@link OperationModeResponse}.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends {@code POST/operation-mode} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param mode the {@link OperationMode}.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
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

    /**
     * Sends a {@code HTTP} request using the specified parameters and returns the {@link Response}
     * or throws a {@link MillException}.
     *
     * @param <T> the {@link Response} class.
     * @param clazz the class type to deserialize the response to.
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param method the {@link HttpMethod} to use.
     * @param path the URI path to use.
     * @param content the request body or {@code null}.
     * @param timeout the timeout value.
     * @param timeUnit the timeout {@link TimeUnit}.
     * @param throwOnAPIStatus if {@code true}, an exception is thrown if the returned API {@code status}
     *                         field is anything but "ok".
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     * @throws MillHTTPResponseException If the returned {@code HTTP status} doesn't indicate success.
     */
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

    /**
     * Sends a {@code HTTP} request using the specified parameters and returns the {@link Response}
     * or throws a {@link MillException}.
     *
     * @param <T> the {@link Response} class.
     * @param clazz the class type to deserialize the response to.
     * @param uri the request {@link URI}.
     * @param method the {@link HttpMethod} to use.
     * @param headers a {@link Map} of {@code HTTP} headers or {@code null}.
     * @param content the request body or {@code null}.
     * @param timeout the timeout value.
     * @param timeUnit the timeout {@link TimeUnit}.
     * @param throwOnAPIStatus if {@code true}, an exception is thrown if the returned API {@code status}
     *                         field is anything but "ok".
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     * @throws MillHTTPResponseException If the returned {@code HTTP status} doesn't indicate success.
     */
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
                sb.append(" with Content=\"").append(content)
                    .append("\", ContentType=\"").append(contentType).append('"');
            }
            logger.debug("{}", sb.toString());
        }
        ContentResponse response = httpClientProvider.send(uri, method, headers, is, contentType, timeout, timeUnit);
        int httpStatus;
        if (HttpStatus.isClientError(httpStatus = response.getStatus())) {
            throw new MillHTTPResponseException(
                httpStatus + " - " + HttpStatus.getMessage(httpStatus) + ": " + uri.getPath(),
                httpStatus,
                ThingStatusDetail.COMMUNICATION_ERROR
            );
        }
        if (!HttpStatus.isSuccess(httpStatus)) {
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
                sb.append(" with Content=\"").append(responseBody)
                    .append("\", ContentType=\"").append(response.getMediaType()).append('"');
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
