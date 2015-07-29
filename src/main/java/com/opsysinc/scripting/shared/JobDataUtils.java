package com.opsysinc.scripting.shared;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Job data utilities.
 *
 * @author mkitchin
 */
public final class JobDataUtils {

    /**
     * Checks for null/empty string.
     *
     * @param input     String to check.
     * @param isToThrow True to throw IAE on null/empty, false otherwise.
     * @return True if null/empty, false otherwise.
     */
    public static boolean checkEmptyString(final Object input,
                                           final boolean isToThrow) {

        final boolean result = (input == null)
                || input.toString().trim().isEmpty();

        try {

            if (result && isToThrow) {

                throw new IllegalArgumentException("missing input (null/empty)");
            }

        } catch (final Throwable ex) {

            JobDataUtils.LOGGER.log(Level.WARNING, "checkEmptyString()", ex);
            throw ex;
        }

        return result;
    }

    /**
     * Checks for null object.
     *
     * @param input     Object to check.
     * @param isToThrow True to throw IAE on null, false otherwise.
     * @return True if null, false otherwise.
     */
    public static boolean checkNullObject(final Object input,
                                          final boolean isToThrow) {

        final boolean result = input == null;

        try {

            if (result && isToThrow) {

                throw new IllegalArgumentException("missing input (null)");
            }

        } catch (final Throwable ex) {

            JobDataUtils.LOGGER.log(Level.WARNING, "checkNullObject()", ex);
            throw ex;
        }

        return result;
    }

    /**
     * Get as boolean.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Value if found, default value otherwise.
     */
    public static boolean getAsBoolean(final String inputText,
                                       final boolean defaultValue) {

        boolean result = defaultValue;

        if (inputText != null) {

            try {

                result = Boolean.valueOf(inputText.trim());

            } catch (final IllegalArgumentException ignored) {

                // ignore;
            }
        }

        return result;

    }

    /**
     * Get as byte.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Value if found, default value otherwise.
     */
    public static byte getAsByte(final String inputText, final byte defaultValue) {

        byte result = defaultValue;
        final Number rawValue = JobDataUtils.getAsNumber(inputText, null);

        if (rawValue != null) {

            result = rawValue.byteValue();
        }

        return result;
    }

    /**
     * Get as double.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Value if found, default value otherwise.
     */
    public static double getAsDouble(final String inputText,
                                     final double defaultValue) {

        double result = defaultValue;
        final Number rawValue = JobDataUtils.getAsNumber(inputText, null);

        if (rawValue != null) {

            result = rawValue.doubleValue();
        }

        return result;
    }

    /**
     * Get as float.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Value if found, default value otherwise.
     */
    public static float getAsFloat(final String inputText,
                                   final float defaultValue) {

        float result = defaultValue;
        final Number rawValue = JobDataUtils.getAsNumber(inputText, null);

        if (rawValue != null) {

            result = rawValue.floatValue();
        }

        return result;
    }

    /**
     * Get as integer.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Value if found, default value otherwise.
     */
    public static int getAsInteger(final String inputText,
                                   final int defaultValue) {

        int result = defaultValue;
        final Number rawValue = JobDataUtils.getAsNumber(inputText, null);

        if (rawValue != null) {

            result = rawValue.intValue();
        }

        return result;
    }

    /**
     * Get as long.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Value if found, default value otherwise.
     */
    public static long getAsLong(final String inputText, final long defaultValue) {

        long result = defaultValue;
        final Number rawValue = JobDataUtils.getAsNumber(inputText, null);

        if (rawValue != null) {

            result = rawValue.longValue();
        }

        return result;
    }

    /**
     * Get as number.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Number if found, null otherwise.
     */
    public static Number getAsNumber(final String inputText,
                                     final Number defaultValue) {

        Number result = defaultValue;

        try {

            result = Double.valueOf(inputText.trim());

        } catch (final IllegalArgumentException ignored) {

            // ignore;
        }

        return result;
    }

    /**
     * Get as short.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return Value if found, default value otherwise.
     */
    public static short getAsShort(final String inputText,
                                   final short defaultValue) {

        short result = defaultValue;
        final Number rawValue = JobDataUtils.getAsNumber(inputText, null);

        if (rawValue != null) {

            result = rawValue.shortValue();
        }

        return result;
    }

    /**
     * Get as date.
     *
     * @param inputText    Input text.
     * @param defaultValue Default value.
     * @return String if found, null otherwise.
     */
    public static String getAsString(final String inputText,
                                     final String defaultValue) {

        String result = defaultValue;

        if (inputText != null) {

            result = String.valueOf(inputText);
        }

        return result;
    }

    /**
     * Logger.
     */
    private static final Logger LOGGER;

    static {

        LOGGER = Logger.getLogger(JobDataUtils.class.getName());
    }

    /**
     * Private ctor (utility class).
     */
    private JobDataUtils() {
    }
}
