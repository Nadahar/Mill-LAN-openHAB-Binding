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

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.annotations.SerializedName;


/**
 * This enum represents the device API's {@code HTTP Response Status}.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public enum ResponseStatus {

    /** The request was successful */
    @SerializedName("ok")
    OK("The request was successful"),

    /** The request body is incorrect or the parameters are invalid */
    @SerializedName("Failed to parse message body")
    PARSE_FAILED("The request body is incorrect or the parameters are invalid"),

    /** There was a problem with the processing request */
    @SerializedName("Failed to execute the request")
    REQUEST_FAILED("There was a problem with the processing request"),

    /** The length of the request body is too long */
    @SerializedName("Length of request body too long")
    TOO_LONG("The length of the request body is too long"),

    /** There was a problem when creating the response */
    @SerializedName("Failed to create response body")
    RESPONSE_FAILED("There was a problem when creating the response");

    private final String description;

    private ResponseStatus(String description) {
        this.description = description;
    }

    /**
     * @return The human-readable name/description.
     */
    public String getDescription() {
        return description;
    }
}
