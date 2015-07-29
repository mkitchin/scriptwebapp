package com.opsysinc.scripting.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Text utils.
 *
 * @author mkitchin
 */
public final class TextUtils {

    /**
     * Checks for null/empty string(s).
     *
     * @param input     String(s) to check.
     * @param isToThrow True to throw IAE on null/empty, false otherwise.
     * @return True if null/empty, false otherwise.
     */
    public static boolean checkEmptyString(final boolean isToThrow,
                                           final Object... input) {

        // first, check array itself
        boolean result = BaseUtils.checkNullObject(input, isToThrow);

        // then, check elements
        if (!result) {

            for (final Object item : input) {

                result = TextUtils.checkEmptyString(item, isToThrow);

                if (result) {

                    break;
                }
            }
        }

        return result;
    }

    /**
     * Checks for null/empty string.
     *
     * @param input     String to check.
     * @param isToThrow True to throw IAE on null/empty, false otherwise.
     * @return True if null/empty, false otherwise.
     */
    public static boolean checkEmptyString(final Object input,
                                           final boolean isToThrow) {

        return TextUtils.checkEmptyString(input, isToThrow, null);
    }

    /**
     * Checks for null/empty string.
     *
     * @param input     String to check.
     * @param isToThrow True to throw IAE on null/empty, false otherwise.
     * @param exText    Optional exception text.
     * @return True if null/empty, false otherwise.
     */
    public static boolean checkEmptyString(final Object input,
                                           final boolean isToThrow, final String exText) {

        final boolean result = (input == null)
                || input.toString().trim().isEmpty();

        if (result && isToThrow) {

            if (exText == null) {

                throw new IllegalArgumentException(
                        TextUtils.DEFAULT_MISSING_INPUT_TEXT);

            } else {

                throw new IllegalArgumentException(exText);
            }
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
        final Number rawValue = TextUtils.getAsNumber(inputText, null);

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
        final Number rawValue = TextUtils.getAsNumber(inputText, null);

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
        final Number rawValue = TextUtils.getAsNumber(inputText, null);

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
        final Number rawValue = TextUtils.getAsNumber(inputText, null);

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
        final Number rawValue = TextUtils.getAsNumber(inputText, null);

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
     * Get as date.
     *
     * @param inputText    Input object.
     * @param defaultValue Default value.
     * @return String if found, null otherwise.
     */
    public static Date getAsOptionQuoteDate(final String inputText,
                                            final Date defaultValue) {

        Date result = defaultValue;

        if (inputText != null) {

            try {

                result = TextUtils.OPTION_QUOTE_DATE_FORMAT.parse(inputText
                        .trim());

            } catch (final ParseException ignored) {

                // ignore
            }
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
        final Number rawValue = TextUtils.getAsNumber(inputText, null);

        if (rawValue != null) {

            result = rawValue.shortValue();
        }

        return result;
    }

    /**
     * Get as string.
     *
     * @param inputObject  Input object.
     * @param defaultValue Default value.
     * @return Input as string if non-null, default value otherwise.
     */
    public static String getAsString(final Object inputObject,
                                     final String defaultValue) {

        String result = defaultValue;

        if (inputObject != null) {

            result = inputObject.toString();
        }

        return result;
    }

    /**
     * Get as text (pretty-printed string).
     *
     * @param inputObject Input object.
     * @return Input as text (trimmed string) if non-null/non-empty, empty
     * string ("") otherwise.
     */
    public static String getAsText(final Object inputObject) {

        return TextUtils.getAsText(inputObject, "");
    }

    /**
     * Get as text (pretty-printed string).
     *
     * @param inputObject  Input object.
     * @param defaultValue Default value.
     * @return Input as text (trimmed string) if non-null/non-empty, default
     * value otherwise.
     */
    public static String getAsText(final Object inputObject,
                                   final String defaultValue) {

        String result = defaultValue;

        if (inputObject != null) {

            final String inputString = inputObject.toString().trim();

            if (!TextUtils.checkEmptyString(inputString, false)) {

                result = inputString;
            }
        }

        return result;
    }

    /**
     * String split method.
     *
     * @param input        Input text.
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection first, false otherwise.
     * @return True if target collection modified, false otherwise.
     */
    public static boolean splitText(final String input, final String separator,
                                    final Collection<String> target, final boolean isClearFirst) {

        BaseUtils.checkNullObject(true, input, separator);
        boolean result = false;

        if (!input.isEmpty()) {

            for (final String item : input.split(separator)) {

                if (target.add(item.trim())) {

                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Default missing input text.
     */
    private static final String DEFAULT_MISSING_INPUT_TEXT = "missing input (null/empty)";

    /**
     * Option quote date format.
     */
    public static final DateFormat OPTION_QUOTE_DATE_FORMAT;

    static {

        OPTION_QUOTE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Private ctor for util class.
     */
    private TextUtils() {
    }

}
