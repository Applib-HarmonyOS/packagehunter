package github.nisrulz.packagehunter.packagehunterlib.utils;

/**
 * Utility class for string functions.
 */
public class StringUtils {
    private static final String TAG_LOG = "packagehunter";

    private StringUtils() {
        /* Do nothing */
    }

    /**
     * check if the input is empty.
     *
     * @param input the input string
     * @return the input string is empty
     */
    public static boolean isEmpty(String input) {
        return input == null || input.length() == 0;
    }
}
