package com.dnbitstudio.twitterclient.utils;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for the Utils class
 */
public class UtilsTest
{
    @Test
    public void createStringWithGivenLength_ReturnsRightString()
    {
        String actualString = Utils.createStringWithGivenLength(3,'A');
        Assert.assertEquals("AAA", actualString);
    }
}