package com.dnbitstudio.twitterclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.dnbitstudio.twitterclient.adapter.TimelineAdapter;
import com.dnbitstudio.twitterclient.bus.BusProvider;
import com.dnbitstudio.twitterclient.service.TwitterAPIRequestsService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

public class LaunchActivity extends AppCompatActivity
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    public static final String OAUTH_SHARED_PREFERENCES = "oauth_shared_preferences";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";
    private static final int REQUEST_CODE = 1234;

    @Bind(R.id.activity_launch_login_button)
    Button loginButton;
    @Bind(R.id.activity_launch_timeline_listView)
    ListView timelineListView;

    private Twitter twitter;
    private Bus bus;
    private SharedPreferences sharedPreferences;
    private TimelineAdapter timelineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);

        initializeFields();

        if (sharedPreferences.contains(ACCESS_TOKEN_KEY))
        {
            loginButton.setVisibility(View.GONE);
            setListViewAndMenuVisibility(View.VISIBLE);
            requestHomeTimeline();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (timelineListView.getVisibility() != View.VISIBLE)
        {
            return false;
        }

        getMenuInflater().inflate(R.menu.activity_launch_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.activity_launch_menu_action_logout:
                confirmLogOut();
                break;
            case R.id.activity_launch_menu_action_refresh:
                requestHomeTimeline();
                break;
            case R.id.activity_launch_menu_action_postTweet:
                PostTweetActivity.launchActivity(this);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            setListViewAndMenuVisibility(View.VISIBLE);
            requestHomeTimeline();
        }
        else
        {
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method to initialize the object's fields when onCreate method is called
     */
    private void initializeFields()
    {
        bus = BusProvider.getBus();
        twitter = TwitterCustomInstanceBuilder.getInstance(this);
        sharedPreferences = getSharedPreferences(OAUTH_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        timelineAdapter = new TimelineAdapter(this, R.layout.list_item_timeline, new
                ArrayList<Status>());
        timelineListView.setAdapter(timelineAdapter);
    }

    /**
     * Sets the visibility of the timelineListView and the menu of this activity
     *
     * @param desiredVisibility the visibility value to be set
     */
    private void setListViewAndMenuVisibility(int desiredVisibility)
    {
        timelineListView.setVisibility(desiredVisibility);
        invalidateOptionsMenu();
    }

    /**
     * Requests the home timeline via TwitterAPIRequestsService
     */
    private void requestHomeTimeline()
    {
        String token = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");
        String tokenSecret = sharedPreferences.getString(ACCESS_TOKEN_SECRET_KEY, "");
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        twitter.setOAuthAccessToken(accessToken);

        TwitterAPIRequestsService.startActionRequestHomeTimeline(this);
    }

    /**
     * Requests confirmation before performing the log out
     */
    private void confirmLogOut()
    {
        new AlertDialog.Builder(this)
                .setMessage(R.string.logOut_dialog_message)
                .setPositiveButton(R.string.logOut_dialog_confirm, new DialogInterface
                        .OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        performConfirmedLogOut();
                    }
                })
                .setNegativeButton(R.string.logOut_dialog_cancel, null)
                .show();
    }

    /**
     * Performs the logout once confirmed in confirmLogOut and updates the UI
     */
    private void performConfirmedLogOut()
    {
        sharedPreferences.edit().clear().apply();
        timelineAdapter.clear();

        loginButton.setVisibility(View.VISIBLE);
        setListViewAndMenuVisibility(View.INVISIBLE);
    }

    /**
     * OnClick method bound by ButterKnife to perform login when the right button is pressed
     */
    @OnClick(R.id.activity_launch_login_button)
    protected void performLogin()
    {
        loginButton.setVisibility(View.GONE);
        LoginActivity.launchActivityForResult(this, REQUEST_CODE);
    }

    /**
     * Otto event bus subscribed method to receive home timeline
     *
     * @param statuses the home timeline received from twitter
     */
    @Subscribe
    public void handleReceivedHomeTimeLine(ArrayList<Status> statuses)
    {
        timelineAdapter.clear();
        timelineAdapter.addAll(statuses);
    }
}