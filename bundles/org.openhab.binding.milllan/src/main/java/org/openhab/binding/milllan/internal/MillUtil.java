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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DecimalType;


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

    /**
     * Checks if the two specified {@link Number}s have the same value.
     *
     * @param n1 the first {@link Number} to compare.
     * @param n2 the second {@link Number} to compare.
     * @return {@code true} if {@code n1} is numerically equal to {@code n2},
     *         {@code false} otherwise.
     */
    public static boolean sameValue(@Nullable Number n1, @Nullable Number n2) {
        return compareNumber(n1, n2, null) == 0;
    }

    /**
     * Checks if the two specified {@link Number}s have the same value. A delta
     * value can be specified which if the specified {@link Number} values
     * differ by only the delta value or less, they will be considered the same
     * value.
     *
     * @param n1 the first {@link Number} to compare.
     * @param n2 the second {@link Number} to compare.
     * @param delta the the value difference that can exist between {@code n1} and
     *        {@code n2} while they will still be considered of equal value, or
     *        {@code null}.
     * @return {@code true} if {@code n1} is numerically equal to {@code n2},
     *         {@code false} otherwise.
     */
    public static boolean sameValue(@Nullable Number n1, @Nullable Number n2, @Nullable Double delta) {
        return compareNumber(n1, n2, delta) == 0;
    }

    /**
     * Compares the two specified {@link Number} values. The sign of the
     * integer value returned indicates which has a larger value or if they
     * have an equal value.
     *
     * @param n1 the first {@link Number} to compare.
     * @param n2 the second {@link Number} to compare.
     * @return {@code 0} if {@code n1} is numerically equal to {@code n2}; a
     *         value less than {@code 0} if {@code n1} is numerically less than
     *         {@code n2}; and a value greater than {@code 0} if {@code n1} is
     *         numerically greater than {@code n2}.
     */
    public static int compareNumber(@Nullable Number n1, @Nullable Number n2) {
        return compareNumber(n1, n2, null);
    }

    /**
     * Compares the two specified {@link Number} values. The sign of the
     * integer value returned indicates which has a larger value or if they
     * have an equal value. A delta value can be specified which if the
     * specified {@link Number} values differ by only the delta value or less,
     * they will be considered of equal value.
     *
     * @param n1 the first {@link Number} to compare.
     * @param n2 the second {@link Number} to compare.
     * @param delta the the value difference that can exist between {@code n1} and
     *        {@code n2} while they will still be considered of equal value, or
     *        {@code null}.
     * @return {@code 0} if {@code n1} is numerically equal to {@code n2}; a
     *         value less than {@code 0} if {@code n1} is numerically less than
     *         {@code n2}; and a value greater than {@code 0} if {@code n1} is
     *         numerically greater than {@code n2}.
     */
    public static int compareNumber(@Nullable Number n1, @Nullable Number n2, @Nullable Double delta) {
        if (n1 == null && n2 == null) {
            return 0;
        }
        if (n1 == null) {
            return -1;
        }
        if (n2 == null) {
            return 1;
        }

        double d = delta == null ? 0.0 : delta.doubleValue();
        NumberType t1 = getNumberType(n1);
        NumberType t2 = getNumberType(n2);
        NumberType lt = t1.ordinal() >= t2.ordinal() ? t1 : t2;
        int result;
        switch (lt) {
            case BIG_INTEGER:
                // Both must be BigInteger in this case
                return ((BigInteger) n1).compareTo((BigInteger) n2);
            case LONG:
                return Long.compare(n1.longValue(), n2.longValue());
            case INT:
                return Integer.compare(n1.intValue(), n2.intValue());
            case SHORT:
                return Short.compare(n1.shortValue(), n2.shortValue());
            case BYTE:
                return Byte.compare(n1.byteValue(), n2.byteValue());
            case BIG_DECIMAL:
            case DECIMAL_TYPE:
                BigDecimal tn1, tn2;
                if (n1 instanceof BigDecimal) {
                    tn1 = (BigDecimal) n1;
                } else if (n1 instanceof DecimalType) {
                    tn1 = ((DecimalType) n1).toBigDecimal();
                } else {
                    tn1 = BigDecimal.valueOf(n1.doubleValue());
                }
                if (n2 instanceof BigDecimal) {
                    tn2 = (BigDecimal) n2;
                } else if (n2 instanceof DecimalType) {
                    tn2 = ((DecimalType) n2).toBigDecimal();
                } else {
                    tn2 = BigDecimal.valueOf(n2.doubleValue());
                }
                result = tn1.compareTo(tn2);
                if (result == 0 || d == 0.0) {
                    return result;
                }
                return tn1.subtract(tn2).abs().doubleValue() <= d ? 0 : result;
            case FLOAT:
                return compareFloat(n1.floatValue(), n2.floatValue(), (float) d);
            case DOUBLE:
            case OTHER:
            default:
                return compareDouble(n1.doubleValue(), n2.doubleValue(), d);
        }
    }

    private static NumberType getNumberType(Number n) {
        if (n instanceof BigInteger) {
            return NumberType.BIG_INTEGER;
        }
        if (n instanceof Long) {
            return NumberType.LONG;
        }
        if (n instanceof Integer) {
            return NumberType.INT;
        }
        if (n instanceof Short) {
            return NumberType.SHORT;
        }
        if (n instanceof Byte) {
            return NumberType.BYTE;
        }
        if (n instanceof BigDecimal) {
            return NumberType.BIG_DECIMAL;
        }
        if (n instanceof DecimalType) {
            return NumberType.DECIMAL_TYPE;
        }
        if (n instanceof Double) {
            return NumberType.DOUBLE;
        }
        if (n instanceof Float) {
            return NumberType.FLOAT;
        }
        return NumberType.OTHER;
    }

    private enum NumberType {
        BIG_INTEGER,
        LONG,
        INT,
        SHORT,
        BYTE,
        BIG_DECIMAL,
        DECIMAL_TYPE,
        DOUBLE,
        FLOAT,
        OTHER
    }

    /**
     * Compares the two specified double values. The sign of the integer value
     * returned indicates which has a larger value or if they have the same value.
     * The delta value specifies how much the specified double values can differ
     * while still being considered of equal value.
     *
     * @param d1 the first {@code double} to compare.
     * @param d2 the second {@code double} to compare.
     * @param delta the the value difference that can exist between {@code d1} and
     *        {@code d2} while they will still be considered of equal value.
     * @return {@code 0} if {@code d1} is numerically equal to {@code d2}; a
     *         value less than {@code 0} if {@code d1} is numerically less than
     *         {@code d2}; and a value greater than {@code 0} if {@code d1} is
     *         numerically greater than {@code d2}.
     */
    public static int compareDouble(double d1, double d2, double delta) {
        int result = Double.compare(d1, d2);
        if (result == 0) {
            return result;
        }
        return Math.abs(d1 - d2) <= delta ? 0 : result;
    }

    /**
     * Compares the two specified float values. The sign of the integer value
     * returned indicates which has a larger value or if they have the same value.
     * The delta value specifies how much the specified float values can differ
     * while still being considered of equal value.
     *
     * @param f1 the first {@code float} to compare.
     * @param f2 the second {@code float} to compare.
     * @param delta the the value difference that can exist between {@code f1} and
     *        {@code f2} while they will still be considered of equal value.
     * @return {@code 0} if {@code f1} is numerically equal to {@code f2}; a
     *         value less than {@code 0} if {@code f1} is numerically less than
     *         {@code f2}; and a value greater than {@code 0} if {@code f1} is
     *         numerically greater than {@code f2}.
     */
    public static int compareFloat(float f1, float f2, float delta) {
        int result = Double.compare(f1, f2);
        if (result == 0) {
            return result;
        }
        return Math.abs(f1 - f2) <= delta ? 0 : result;
    }

    /**
     * Converts any {@link Number} to a {@link BigDecimal}.
     *
     * @param number the {@link Number} to convert.
     * @return The resulting {@link BigDecimal}.
     */
    public static BigDecimal toBigDecimal(Number number) {
        NumberType nt = getNumberType(number);
        switch (nt) {
            case BIG_DECIMAL:
                return (BigDecimal) number;
            case BIG_INTEGER:
                return new BigDecimal((BigInteger) number);
            case DECIMAL_TYPE:
                return ((DecimalType) number).toBigDecimal();
            case LONG:
            case INT:
            case SHORT:
            case BYTE:
                return BigDecimal.valueOf(number.longValue());
            case DOUBLE:
            case FLOAT:
            case OTHER:
            default:
                return BigDecimal.valueOf(number.doubleValue());
        }
    }
}
