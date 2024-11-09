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
import org.eclipse.jdt.annotation.Nullable;


/**
 * A general utility class.
 *
 * @author Nadahar - Initial contribution
 */
@NonNullByDefault
public class MillUtil {

    /**
     * Not to be instantiated.
     */
    private MillUtil() {
    }

    /**
     * Evaluates if the specified character sequence is {@code null}, empty or
     * only consists of whitespace.
     *
     * @param cs the {@link CharSequence} to evaluate.
     * @return {@code false} if {@code cs} is {@code null}, empty or only consists of
     *         whitespace, {@code true} otherwise.
     */
    public static boolean isNotBlank(@Nullable CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * Evaluates if the specified character sequence is {@code null}, empty or
     * only consists of whitespace.
     *
     * @param cs the {@link CharSequence} to evaluate.
     * @return {@code true} if {@code cs} is {@code null}, empty or only
     *         consists of whitespace, {@code false} otherwise.
     */
    public static boolean isBlank(@Nullable CharSequence cs) {
        if (cs == null) {
            return true;
        }
        int strLen = cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
