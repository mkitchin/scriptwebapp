package com.opsysinc.scripting.server.util;

/**
 * Base utils.
 *
 * @author mkitchin
 */
public final class BaseUtils {

    /**
     * Checks for null object(s).
     *
     * @param input     Object(s) to check.
     * @param isToThrow True to throw IAE on null, false otherwise.
     * @return True if null, false otherwise.
     */
    public static boolean checkNullObject(final boolean isToThrow,
                                          final Object... input) {

        // first, check array itself
        boolean result = BaseUtils.checkNullObject(input, isToThrow);

        // then, check elements
        if (!result) {

            for (final Object item : input) {

                result = BaseUtils.checkNullObject(item, isToThrow);

                if (result) {

                    break;
                }
            }
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

        return BaseUtils.checkNullObject(input, isToThrow, null);
    }

    /**
     * Checks for null object.
     *
     * @param input     Object to check.
     * @param isToThrow True to throw IAE on null, false otherwise.
     * @param exText    Optional exception text.
     * @return True if null, false otherwise.
     */
    public static boolean checkNullObject(final Object input,
                                          final boolean isToThrow, final String exText) {

        final boolean result = input == null;

        if (result && isToThrow) {

            if (exText == null) {

                throw new IllegalArgumentException(
                        BaseUtils.DEFAULT_MISSING_INPUT_TEXT);

            } else {

                throw new IllegalArgumentException(exText);
            }
        }

        return result;
    }

    /**
     * Default missing input text.
     */
    private static final String DEFAULT_MISSING_INPUT_TEXT = "missing input (null)";

    /**
     * Private ctor for utils class.
     */
    private BaseUtils() {
    }

}
