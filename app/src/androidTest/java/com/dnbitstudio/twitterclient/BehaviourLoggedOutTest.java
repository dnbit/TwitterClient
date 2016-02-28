package com.dnbitstudio.twitterclient;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Class to test various behaviours when the user is logged out
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BehaviourLoggedOutTest
{
    private final String LOG_TAG = this.getClass().getSimpleName();
    
    @Rule
    public ActivityTestRule<LaunchActivity> mActivityRule = new ActivityTestRule<>(
            LaunchActivity.class);

    @Test
    public void launchActivity_MenuNotVisibleBeforeLogin()
    {
        onView(withId(R.id.activity_launch_menu_action_postTweet)).check(doesNotExist());
    }

    @Test
    public void launchActivity_ClickOnLoginButton_LaunchesLoginActivity()
    {
        onView(withId(R.id.activity_launch_login_button)).perform(click());
        onView(withId(R.id.activity_login_webView)).check(matches(isDisplayed()));
    }

    @Test
    public void loginActivity_BackButton_LoginButtonStillVisible()
    {
        onView(withId(R.id.activity_launch_login_button)).perform(click());
        onView(withId(R.id.activity_login_webView)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.activity_launch_login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void loginActivity_BackButton_MenuStillNotVisible()
    {
        onView(withId(R.id.activity_launch_login_button)).perform(click());
        onView(withId(R.id.activity_login_webView)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.activity_launch_menu_action_postTweet)).check(doesNotExist());
    }

    @Test
    public void loginActivity_FindsWebViewElements()
    {
        onView(withId(R.id.activity_launch_login_button)).perform(click());
        onView(withId(R.id.activity_login_webView)).check(matches(isDisplayed()));
    }
}