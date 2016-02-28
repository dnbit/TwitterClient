package com.dnbitstudio.twitterclient.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.dnbitstudio.twitterclient.TwitterCustomInstanceBuilder;
import com.dnbitstudio.twitterclient.bus.BusProvider;
import com.squareup.otto.Bus;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class TwitterAPIRequestsService extends IntentService
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final String ACTION_REQUEST_HOME_TIMELINE = "action_request_home_timeline";
    private static final String ACTION_REQUEST_POST_TWEET = "action_request_post_tweet";
    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private Bus mBus;

    /**
     * Required default constructor
     */
    public TwitterAPIRequestsService()
    {
        super("TwitterAPIRequestsService");
    }

    /**
     * Starts this service to perform action RequestHomeTimeline. If
     * the service is already performing a task this action will be queued.
     *
     * @param context the context from which this service is started
     * @see IntentService
     */
    public static void startActionRequestHomeTimeline(Context context)
    {
        Intent intent = new Intent(context, TwitterAPIRequestsService.class);
        intent.setAction(ACTION_REQUEST_HOME_TIMELINE);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action RequestPostNewTweet with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context the context from which this service is started
     * @param message the message to be posted to twitter
     * @see IntentService
     */
    public static void startActionRequestPostNewTweet(Context context, String message)
    {
        Intent intent = new Intent(context, TwitterAPIRequestsService.class);
        intent.setAction(ACTION_REQUEST_POST_TWEET);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.startService(intent);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mBus = BusProvider.getBus();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_REQUEST_HOME_TIMELINE.equals(action))
            {
                handleActionRequestHomeTimeline();
            }
            else if (ACTION_REQUEST_POST_TWEET.equals(action))
            {
                final String message = intent.getStringExtra(EXTRA_MESSAGE);
                handleActionRequestPostNewTweet(message);
            }
        }
    }

    /**
     * Handle action RequestHomeTimeline in the provided background thread.
     */
    private void handleActionRequestHomeTimeline()
    {
        Twitter twitter = TwitterCustomInstanceBuilder.getInstance(this);

        List<Status> statuses = null;
        try
        {
            statuses = twitter.getHomeTimeline();
        }
        catch (TwitterException e)
        {
            e.printStackTrace();
        }

        if (statuses != null)
        {
            mBus.post(statuses);
        }
    }

    /**
     * Handle action RetrieveAccessToken in the provided background thread with the provided
     * parameters.
     *
     * @param message the message to be posted to twitter
     */
    private void handleActionRequestPostNewTweet(String message)
    {
        Twitter twitter = TwitterCustomInstanceBuilder.getInstance(this);
        try
        {
            twitter.updateStatus(message);
        }
        catch (TwitterException e)
        {
            e.printStackTrace();
        }
    }
}