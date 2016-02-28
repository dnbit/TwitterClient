package com.dnbitstudio.twitterclient;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.By;
import com.robotium.solo.Solo;

/**
 * Class to test the log in behaviour. Extends ActivityInstrumentationTestCase2.
 * Test using Robotium as Espresso finds some issues with javascript on twitter login page.
 */
public class LogInTest extends ActivityInstrumentationTestCase2
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    private Solo solo;

    public LogInTest()
    {
        super(LaunchActivity.class);
    }

    @Override
    public void setUp() throws Exception
    {
        //setUp() is run before a test case is started.
        //This is where the solo object is created.
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception
    {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during
        // the test execution.
        solo.finishOpenedActivities();
    }

    public void testPerformLogin_WithValidCredentials()
    {
        solo.unlockScreen();
        solo.clickOnButton(getInstrumentation()
                .getTargetContext()
                .getResources()
                .getString(R.string.button_twitter_login));

        String username_or_email = getInstrumentation().getTargetContext().getString(R.string
                .username_or_email_for_tests);
        solo.enterTextInWebElement(By.id("username_or_email"), username_or_email);
        String password = getInstrumentation().getTargetContext().getString(R.string.password_for_tests);
        solo.enterTextInWebElement(By.id("password"), password);
        solo.clickOnWebElement(By.id("allow"));

        solo.waitForView(R.id.activity_launch_timeline_listView);
    }
}