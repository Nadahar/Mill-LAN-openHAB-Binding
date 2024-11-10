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


/**
 * This class is used for deserializing JSON response objects that only contains the "status" field.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class GenericResponse implements Response { // TODO: (Nad) JavaDocs

    @Nullable
    private ResponseStatus status;

    @Override
    @Nullable
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GenericResponse other = (GenericResponse) obj;
        return status == other.status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GenericResponse [status=").append(status).append("]");
        return builder.toString();
    }
}
