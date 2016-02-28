package com.dnbitstudio.twitterclient;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Class to test the log out behaviour.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LogOutTest
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Rule
    public ActivityTestRule<LaunchActivity> launchActivityRule = new ActivityTestRule<>(
            LaunchActivity.class);

    @Test
    public void launchActivity_ClickMenuOptionLogOut_PerfomsLogOut()
    {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.log_out)).check(matches(isDisplayed()));
        onView(withText(R.string.log_out)).perform(click());
        onView(withText(R.string.logOut_dialog_confirm)).check(matches(isDisplayed()));
        onView(withText(R.string.logOut_dialog_confirm)).perform(click());
        onView(withId(R.id.activity_launch_login_button)).check(matches(isDisplayed()));
    }
}