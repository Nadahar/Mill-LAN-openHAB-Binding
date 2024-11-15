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
package org.openhab.binding.milllan.internal.api.response;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.milllan.internal.api.ResponseStatus;

import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects from the "timezone-offset" API call.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class TimeZoneOffsetResponse implements Response { // TODO: (Nad) JavaDocs

    /**
     * The time zone offset in minutes.
     */
    @Nullable
    @SerializedName("timezone_offset")
    private Integer offset;

    @Nullable
    private ResponseStatus status;

    @Nullable
    public Integer getOffset() {
        return offset;
    }

    @Override
    @Nullable
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, status);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimeZoneOffsetResponse)) {
            return false;
        }
        TimeZoneOffsetResponse other = (TimeZoneOffsetResponse) obj;
        return Objects.equals(offset, other.offset) && status == other.status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TimezoneOffsetResponse [");
        if (offset != null) {
            builder.append("offset=").append(offset).append(", ");
        }
        if (status != null) {
            builder.append("status=").append(status);
        }
        builder.append("]");
        return builder.toString();
    }
}
