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
import org.openhab.binding.milllan.internal.api.request.OpenWindowParameters;
import org.openhab.binding.milllan.internal.api.response.ChildLockResponse;
import org.openhab.binding.milllan.internal.api.response.CloudCommunicationResponse;
import org.openhab.binding.milllan.internal.api.response.CommercialLockCustomizationResponse;
import org.openhab.binding.milllan.internal.api.response.CommercialLockResponse;
import org.openhab.binding.milllan.internal.api.response.ControlStatusResponse;
import org.openhab.binding.milllan.internal.api.response.ControllerTypeResponse;
import org.openhab.binding.milllan.internal.api.response.DisplayUnitResponse;
import org.openhab.binding.milllan.internal.api.response.GenericResponse;
import org.openhab.binding.milllan.internal.api.response.HysteresisParametersResponse;
import org.openhab.binding.milllan.internal.api.response.LimitedHeatingPowerResponse;
import org.openhab.binding.milllan.internal.api.response.OilHeaterPowerResponse;
import org.openhab.binding.milllan.internal.api.response.OpenWindowParametersResponse;
import org.openhab.binding.milllan.internal.api.response.OperationModeResponse;
import org.openhab.binding.milllan.internal.api.response.PIDParametersResponse;
import org.openhab.binding.milllan.internal.api.response.PredictiveHeatingTypeResponse;
import org.openhab.binding.milllan.internal.api.response.Response;
import org.openhab.binding.milllan.internal.api.response.SetTemperatureResponse;
import org.openhab.binding.milllan.internal.api.response.StatusResponse;
import org.openhab.binding.milllan.internal.api.response.TemperatureCalibrationOffsetResponse;
import org.openhab.binding.milllan.internal.api.response.TimeZoneOffsetResponse;
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
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link StatusResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public StatusResponse getStatus(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            StatusResponse.class,
            hostname,
            apiKey,
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
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link ControlStatusResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public ControlStatusResponse getControlStatus(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            ControlStatusResponse.class,
            hostname,
            apiKey,
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
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link OperationModeResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public OperationModeResponse getOperationMode(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            OperationModeResponse.class,
            hostname,
            apiKey,
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
     * @param apiKey the API key or {@code null}.
     * @param mode the {@link OperationMode}.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setOperationMode(
        String hostname,
        @Nullable String apiKey,
        OperationMode mode
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.add("mode", gson.toJsonTree(mode));
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/operation-mode",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/temperature-calibration-offset} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link TemperatureCalibrationOffsetResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public TemperatureCalibrationOffsetResponse getTemperatureCalibrationOffset(
        String hostname,
        @Nullable String apiKey
    ) throws MillException {
        return request(
            TemperatureCalibrationOffsetResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/temperature-calibration-offset",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/temperature-calibration-offset} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param offset the temperature offset in °C.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setTemperatureCalibrationOffset(
        String hostname,
        @Nullable String apiKey,
        BigDecimal offset
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("value", offset);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/temperature-calibration-offset",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/commercial-lock} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link CommercialLockResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public CommercialLockResponse getCommercialLock(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            CommercialLockResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/commercial-lock",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/commercial-lock} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param value whether the commercial lock should be enabled or not.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setCommercialLock(String hostname, @Nullable String apiKey, Boolean value) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("value", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/commercial-lock",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/child-lock} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link ChildLockResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public ChildLockResponse getChildLock(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            ChildLockResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/child-lock",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/child-lock} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param value whether the child lock should be enabled or not.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setChildLock(String hostname, @Nullable String apiKey, Boolean value) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("value", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/child-lock",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/display-unit} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link DisplayUnitResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public DisplayUnitResponse getDisplayUnit(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            DisplayUnitResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/display-unit",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/display-unit} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param displayUnit the {@link DisplayUnit}.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setDisplayUnit(
        String hostname,
        @Nullable String apiKey,
        DisplayUnit displayUnit
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.add("value", gson.toJsonTree(displayUnit));
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/display-unit",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/set-temperature} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param temperatureType the {@link TemperatureType} whose set-temperature to get.
     * @return The resulting {@link SetTemperatureResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public SetTemperatureResponse getSetTemperature(
        String hostname,
        @Nullable String apiKey,
        TemperatureType temperatureType
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.add("type", gson.toJsonTree(temperatureType));
        return request(
            SetTemperatureResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/set-temperature",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/set-temperature} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param temperatureType the {@link TemperatureType} for which to set the target temperature.
     * @param value the target temperature in °C.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setSetTemperature(
        String hostname,
        @Nullable String apiKey,
        TemperatureType temperatureType,
        BigDecimal value
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.add("type", gson.toJsonTree(temperatureType));
        object.addProperty("value", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/set-temperature",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/limited-heating-power} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link LimitedHeatingPowerResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public LimitedHeatingPowerResponse getLimitedHeatingPower(
        String hostname,
        @Nullable String apiKey
    ) throws MillException {
        return request(
            LimitedHeatingPowerResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/limited-heating-power",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/limited-heating-power} to the device's REST API and returns the response.
     * <p>
     * <b>Note:</b> Has no effect on the actual output power of tested devices
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param value the maximum heating power in percentage.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setLimitedHeatingPower(
        String hostname,
        @Nullable String apiKey,
        Integer value
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("limited_heating_power", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/limited-heating-power",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/controller-type} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link ControllerTypeResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public ControllerTypeResponse getControllerType(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            ControllerTypeResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/controller-type",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/controller-type} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param controllerType the {@link ControllerType}.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setControllerType(
        String hostname, @Nullable
        String apiKey,
        ControllerType controllerType
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.add("regulator_type", gson.toJsonTree(controllerType));
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/controller-type",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/predictive-heating-type} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link PredictiveHeatingTypeResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public PredictiveHeatingTypeResponse getPredictiveHeatingType(
        String hostname,
        @Nullable String apiKey
    ) throws MillException {
        return request(
            PredictiveHeatingTypeResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/predictive-heating-type",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/predictive-heating-type} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param type the {@link PredictiveHeatingType}.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setPredictiveHeatingType(
        String hostname,
        @Nullable String apiKey,
        PredictiveHeatingType type
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.add("predictive_heating_type", gson.toJsonTree(type));
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/predictive-heating-type",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/oil-heater-power} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link OilHeaterPowerResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public OilHeaterPowerResponse getOilHeaterPower(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            OilHeaterPowerResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/oil-heater-power",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/oil-heating-power} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param value the heating power in percentage (40%, 60% or 100%).
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setOilHeaterPower(String hostname, @Nullable String apiKey, Integer value) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("heating_level_percentage", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/oil-heater-power",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/timezone-offset} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link TimeZoneOffsetResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public TimeZoneOffsetResponse getTimeZoneOffset(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            TimeZoneOffsetResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/timezone-offset",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/timezone-offset} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param value the time zone offset from UTC in minutes.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setTimeZoneOffset(String hostname, @Nullable String apiKey, Integer value) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("timezone_offset", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/timezone-offset",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/pid-parameters} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link PIDParametersResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public PIDParametersResponse getPIDParameters(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            PIDParametersResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/pid-parameters",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/pid-parameters} to the device's REST API and returns the response.
     * <p>
     * <b>Supported by panel heaters only</b>.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param kp the proportional gain factor.
     * @param ki the integral gain factor.
     * @param kd the derivative gain factor.
     * @param kdFilterN the derivative filter time coefficient.
     * @param windupLimitPercentage the wind-up limit for integral part from 0 to 100.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setPIDParameters(
        String hostname,
        @Nullable String apiKey,
        Double kp,
        Double ki,
        Double kd,
        Double kdFilterN,
        Double windupLimitPercentage
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("kp", kp);
        object.addProperty("ki", ki);
        object.addProperty("kd", kd);
        object.addProperty("kd_filter_N", kdFilterN);
        object.addProperty("windup_limit_percentage", windupLimitPercentage);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/pid-parameters",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/cloud-communication} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link CloudCommunicationResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public CloudCommunicationResponse getCloudCommunication(
        String hostname,
        @Nullable String apiKey
    ) throws MillException {
        return request(
            CloudCommunicationResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/cloud-communication",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/cloud-communication} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param value whether cloud communication is enabled or not.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setCloudCommunication(
        String hostname,
        @Nullable String apiKey,
        Boolean value
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("value", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/cloud-communication",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/hysteresis-parameters} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link HysteresisParametersResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public HysteresisParametersResponse getHysteresisParameters(
        String hostname,
        @Nullable String apiKey
    ) throws MillException {
        return request(
            HysteresisParametersResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/hysteresis-parameters",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/hysteresis-parameters} to the device's REST API and returns the response.
     * <p>
     * <b>A device reboot is required to effectuate changes.</b>
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param upper the upper hysteresis limit in °C.
     * @param lower the lower hysteresis limit in °C.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setHysteresisParameters(
        String hostname,
        @Nullable String apiKey,
        Double upper,
        Double lower
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("temp_hysteresis_upper", upper);
        object.addProperty("temp_hysteresis_lower", lower);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/hysteresis-parameters",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code POST/set-temperature-in-independent-mode-now} to the device's REST API and returns the response.
     * <p>
     * <b>Note:</b> Will fail is the device isn't in "Independent device" mode.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param value the target temperature in °C.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setTemperatureInIndependentMode(
        String hostname,
        @Nullable String apiKey,
        BigDecimal value
    ) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("temperature", value);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/set-temperature-in-independent-mode-now",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code POST/set-custom-name} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param customName the custom name to set, <b>maximum 32 bytes in {@code UTF-8}</b>.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setCustomName(String hostname, @Nullable String apiKey, String customName) throws MillException {
        JsonObject object = new JsonObject();
        object.addProperty("device_name", customName);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/set-custom-name",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/commercial-lock-customization} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link CommercialLockCustomizationResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public CommercialLockCustomizationResponse getCommercialLockCustomization(
        String hostname,
        @Nullable String apiKey
    ) throws MillException {
        return request(
            CommercialLockCustomizationResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/commercial-lock-customization",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/commercial-lock} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param min the minimum set-temperature in °C.
     * @param max the maximum set-temperature in °C.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setCommercialLockCustomization(
        String hostname,
        @Nullable String apiKey,
        Double min,
        Double max
    ) throws MillException {
        CommercialLockResponse lockState = request(
            CommercialLockResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/commercial-lock",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
        Boolean enabled = lockState.getValue();
        if (enabled == null) {
            enabled = Boolean.FALSE;
        }

        JsonObject object = new JsonObject();
        object.addProperty("enabled", enabled);
        object.addProperty("min_allowed_temp_in_commercial_lock", min);
        object.addProperty("max_allowed_temp_in_commercial_lock", max);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/commercial-lock-customization",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code GET/open-window} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @return The resulting {@link OpenWindowParametersResponse}.
     * @throws MillException If an error occurs during the operation.
     */
    public OpenWindowParametersResponse getOpenWindowParameters(
        String hostname,
        @Nullable String apiKey
    ) throws MillException {
        return request(
            OpenWindowParametersResponse.class,
            hostname,
            apiKey,
            HttpMethod.GET,
            "/open-window",
            null,
            5L,
            TimeUnit.SECONDS,
            true
        );
    }

    /**
     * Sends {@code POST/open-window} to the device's REST API and returns the response.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the API key or {@code null}.
     * @param parameters the {@link OpenWindowParameters}.
     * @return The resulting {@link Response}.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setOpenWindowParameters(
        String hostname,
        @Nullable String apiKey,
        OpenWindowParameters parameters
    ) throws MillException {
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/open-window",
            gson.toJson(parameters),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code POST/set-api-key} to the device's REST API and returns the response.
     * <p>
     * <b>WARNING: Setting an API key will switch the device to {@code HTTPS}, and the key cannot be removed
     * (only changed). To restore {@code HTTP} and/or remove the API key, a factory reset is required</b>.
     * <p>
     * <b>Note:</b> This method will time out if successful.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the existing API key or {@code null}.
     * @param newAPIKey the new API key to set, <b>maximum 63 bytes {@code UTF-8} encoded</b>.
     * @return The resulting {@link Response} if the call fails.
     * @throws MillException If an error occurs during the operation.
     */
    public Response setAPIKey(String hostname, @Nullable String apiKey, String newAPIKey) throws MillException {
        if (newAPIKey.getBytes(StandardCharsets.UTF_8).length > 63) {
            throw new MillException("Illegal API-ley - maximum length is 63 bytes");
        }
        JsonObject object = new JsonObject();
        object.addProperty("api_key", newAPIKey);
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/set-api-key",
            gson.toJson(object),
            5L,
            TimeUnit.SECONDS,
            false
        );
    }

    /**
     * Sends {@code POST/reboot} to the device's REST API.
     * <p>
     * <b>Note:</b> This method will time out if successful.
     *
     * @param hostname the hostname or IP address to contact.
     * @param apiKey the existing API key or {@code null}.
     * @return The resulting {@link Response} if the call fails.
     * @throws MillException If an error occurs during the operation.
     */
    public Response sendReboot(String hostname, @Nullable String apiKey) throws MillException {
        return request(
            GenericResponse.class,
            hostname,
            apiKey,
            HttpMethod.POST,
            "/reboot",
            null,
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
