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
package org.openhab.binding.milllan.internal.exception;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatusDetail;


/**
 * A {@link MillException} implementation that also carries a {@code HTTP} status.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillHTTPResponseException extends MillException {

    private static final long serialVersionUID = 1L;

    private final int httpStatus;

    /**
     * Creates a new instance with the specified {@code HTTP} status.
     *
     * @param httpStatus the {@code HTTP} status to use.
     */
    public MillHTTPResponseException(int httpStatus) {
        super(httpStatus + " - " + HttpStatus.getMessage(httpStatus));
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param message the message to use.
     * @param httpStatus the {@code HTTP} status to use.
     */
    public MillHTTPResponseException(@Nullable String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param httpStatus the {@code HTTP} status to use.
     * @param cause the {@link Throwable} that caused this {@link Exception}.
     */
    public MillHTTPResponseException(int httpStatus, @Nullable Throwable cause) {
        super(httpStatus + " - " + HttpStatus.getMessage(httpStatus), cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param message the message to use.
     * @param httpStatus the {@code HTTP} status to use.
     * @param cause the {@link Throwable} that caused this {@link Exception}.
     */
    public MillHTTPResponseException(@Nullable String message, int httpStatus, @Nullable Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     */
    public MillHTTPResponseException(int httpStatus, @Nullable ThingStatusDetail thingStatusDetail) {
        super(httpStatus + " - " + HttpStatus.getMessage(httpStatus), thingStatusDetail);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param message the message to use.
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     */
    public MillHTTPResponseException(
        @Nullable String message,
        int httpStatus,
        @Nullable ThingStatusDetail thingStatusDetail
    ) {
        super(message, thingStatusDetail);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     * @param cause the {@link Throwable} that caused this {@link Exception}.
     */
    public MillHTTPResponseException(
        int httpStatus,
        @Nullable ThingStatusDetail thingStatusDetail,
        @Nullable Throwable cause
    ) {
        super(httpStatus + " - " + HttpStatus.getMessage(httpStatus), thingStatusDetail, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param message the message to use.
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     * @param cause the {@link Throwable} that caused this {@link Exception}.
     */
    public MillHTTPResponseException(
        @Nullable String message,
        int httpStatus,
        @Nullable ThingStatusDetail thingStatusDetail,
        @Nullable Throwable cause
    ) {
        super(message, thingStatusDetail, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     * @param thingStatusDescription the {@link Thing} status description to use.
     */
    public MillHTTPResponseException(
        int httpStatus,
        @Nullable ThingStatusDetail thingStatusDetail,
        @Nullable String thingStatusDescription
    ) {
        super(httpStatus + " - " + HttpStatus.getMessage(httpStatus), thingStatusDetail, thingStatusDescription);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param message the message to use.
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     * @param thingStatusDescription the {@link Thing} status description to use.
     */
    public MillHTTPResponseException(
        @Nullable String message,
        int httpStatus,
        @Nullable ThingStatusDetail thingStatusDetail,
        @Nullable String thingStatusDescription
    ) {
        super(message, thingStatusDetail, thingStatusDescription);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     * @param thingStatusDescription the {@link Thing} status description to use.
     * @param cause the {@link Throwable} that caused this {@link Exception}.
     */
    public MillHTTPResponseException(
        int httpStatus,
        @Nullable ThingStatusDetail thingStatusDetail,
        @Nullable String thingStatusDescription,
        @Nullable Throwable cause
    ) {
        super(
            httpStatus + " - " + HttpStatus.getMessage(httpStatus),
            thingStatusDetail,
            thingStatusDescription,
            cause
        );
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new instance with the specified details.
     *
     * @param message the message to use.
     * @param httpStatus the {@code HTTP} status to use.
     * @param thingStatusDetail the {@link ThingStatusDetail} to use.
     * @param thingStatusDescription the {@link Thing} status description to use.
     * @param cause the {@link Throwable} that caused this {@link Exception}.
     */
    public MillHTTPResponseException(
        @Nullable String message,
        int httpStatus,
        @Nullable ThingStatusDetail thingStatusDetail,
        @Nullable String thingStatusDescription,
        @Nullable Throwable cause
    ) {
        super(message, thingStatusDetail, thingStatusDescription, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * @return The {@code HTTP} status.
     */
    public int getHttpStatus() {
        return httpStatus;
    }
}
