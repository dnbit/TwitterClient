package com.dnbitstudio.twitterclient.utils;

import java.util.Arrays;

/**
 * Utils class for reusable methods
 */
public class Utils
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    /**
     * Utility method to build a string with a given length
     *
     * @param length    the length of the string to be created
     * @param character the character used to build the string
     * @return the desired string
     */
    public static String createStringWithGivenLength(int length, char character)
    {
        char[] chars = new char[length];
        Arrays.fill(chars, character);

        return new String(chars);
    }
}