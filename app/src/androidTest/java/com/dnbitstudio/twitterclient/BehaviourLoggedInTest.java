package com.dnbitstudio.twitterclient;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.dnbitstudio.twitterclient.utils.Utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Class to test various behaviours when the user is logged in
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class BehaviourLoggedInTest
{
    private final String LOG_TAG = this.getClass().getSimpleName();
    
    @Rule
    public ActivityTestRule<LaunchActivity> launchActivityRule = new ActivityTestRule<>(
            LaunchActivity.class);

    @Test
    public void launchActivity_LoginButtonGoneAfterLogin()
    {
        onView(withId(R.id.activity_launch_login_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void launchActivity_MenuVisibleAfterLogin()
    {
        onView(withId(R.id.activity_launch_menu_action_postTweet)).check(matches(isDisplayed()));
    }

    @Test
    public void launchActivity_ClickMenuOptionNew_launchesPostTweetActivity()
    {
        onView(withId(R.id.activity_launch_menu_action_postTweet)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_launch_menu_action_postTweet)).perform(click());
        onView(withId(R.id.activity_post_tweet_message_editText)).check(matches(isDisplayed()));
    }

    @Test
    public void launchActivity_ClickRefreshOptionLogOut_RefreshesListView()
    {
        onView(withId(R.id.activity_launch_menu_action_refresh)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_launch_menu_action_refresh)).perform(click());
        onView(withId(R.id.activity_launch_timeline_listView)).check(matches(isClickable()));
    }

    @Test
    public void postNewTweet_WithLengthZero_ShowsTooShortErrorMessage()
    {
        onView(withId(R.id.activity_launch_menu_action_postTweet)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_launch_menu_action_postTweet)).perform(click());
        onView(withId(R.id.activity_post_tweet_message_editText))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.activity_post_tweet_send_button)).perform(click());
        onView(withId(R.id.activity_post_tweet_errorMessage_textView))
                .check(matches(withText(R.string.message_too_short)));
    }

    @Test
    public void postNewTweet_LargerThanMaximumLength_ShowsTooLongErrorMessage()
    {
        onView(withId(R.id.activity_launch_menu_action_postTweet)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_launch_menu_action_postTweet)).perform(click());

        String longMessage = Utils.createStringWithGivenLength(PostTweetActivity
                .MAXIMUM_TWEET_LENGTH + 1, '*');
        onView(withId(R.id.activity_post_tweet_message_editText))
                .perform(typeText(longMessage), closeSoftKeyboard());

        onView(withId(R.id.activity_post_tweet_send_button)).perform(click());
        onView(withId(R.id.activity_post_tweet_errorMessage_textView))
                .check(matches(withText(R.string.message_too_long)));
    }

    @Test
    public void postNewTweet_WithValidLength_PostsAndShowsHomeTimeline()
    {
        onView(withId(R.id.activity_launch_menu_action_postTweet)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_launch_menu_action_postTweet)).perform(click());

        String message = "test " + new Random().nextInt(Integer.MAX_VALUE);
        onView(withId(R.id.activity_post_tweet_message_editText))
                .perform(typeText(message), closeSoftKeyboard());

        onView(withId(R.id.activity_post_tweet_send_button)).perform(click());
        onView(withId(R.id.activity_launch_timeline_listView)).check(matches(isDisplayed()));
    }
}