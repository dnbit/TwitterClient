package com.dnbitstudio.twitterclient.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.dnbitstudio.twitterclient.R;
import com.dnbitstudio.twitterclient.TwitterCustomInstanceBuilder;
import com.dnbitstudio.twitterclient.bus.BusProvider;
import com.squareup.otto.Bus;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class TwitterOAuthService extends IntentService
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final String ACTION_RETRIEVE_REQUEST_TOKEN = "action_retrieve_request_token";
    private static final String ACTION_RETRIEVE_ACCESS_TOKEN = "action_retrieve_access_token";
    private static final String EXTRA_REQUEST_TOKEN = "extra_request_token";
    private static final String EXTRA_VERIFIER = "extra_verifier";

    private Bus mBus;

    /**
     * Required default constructor
     */
    public TwitterOAuthService()
    {
        super("TwitterOAuthService");
    }

    /**
     * Starts this service to perform action RetrieveRequestToken. If
     * the service is already performing a task this action will be queued.
     *
     * @param context the context from which this service is started
     * @see IntentService
     */
    public static void startActionRetrieveRequestToken(Context context)
    {
        Intent intent = new Intent(context, TwitterOAuthService.class);
        intent.setAction(ACTION_RETRIEVE_REQUEST_TOKEN);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action RetrieveAccessToken with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context      the context from which this service is started
     * @param requestToken the request token received from twitter
     * @param verifier     the verifier received from twitter
     * @see IntentService
     */
    public static void startActionRetrieveAccessToken(Context context,
                                                      RequestToken requestToken, String verifier)
    {
        Intent intent = new Intent(context, TwitterOAuthService.class);
        intent.setAction(ACTION_RETRIEVE_ACCESS_TOKEN);
        intent.putExtra(EXTRA_REQUEST_TOKEN, requestToken);
        intent.putExtra(EXTRA_VERIFIER, verifier);
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
            String action = intent.getAction();
            if (ACTION_RETRIEVE_REQUEST_TOKEN.equals(action))
            {
                handleActionRetrieveRequestToken();
            }
            else if (ACTION_RETRIEVE_ACCESS_TOKEN.equals(action))
            {
                RequestToken requestToken = (RequestToken) intent.getSerializableExtra
                        (EXTRA_REQUEST_TOKEN);
                String verifier = intent.getStringExtra(EXTRA_VERIFIER);
                handleActionRetrieveAccessToken(requestToken, verifier);
            }
        }
    }

    /**
     * Handle action RetrieveRequestToken in the provided background thread.
     */
    private void handleActionRetrieveRequestToken()
    {
        Twitter twitter = TwitterCustomInstanceBuilder.getInstance(this);
        RequestToken requestToken = null;
        try
        {
            requestToken = twitter.getOAuthRequestToken(getString(R.string.callback_url));
        }
        catch (TwitterException e)
        {
            e.printStackTrace();
        }

        if (requestToken != null)
        {
            mBus.post(requestToken);
        }
    }

    /**
     * Handle action RetrieveAccessToken in the provided background thread with the provided
     * parameters.
     *
     * @param requestToken the request token received from twitter
     * @param verifier     the verifier received from twitter
     */
    private void handleActionRetrieveAccessToken(RequestToken requestToken, String verifier)
    {
        Twitter twitter = TwitterCustomInstanceBuilder.getInstance(this);
        AccessToken accessToken = null;
        try
        {
            accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
        }
        catch (TwitterException e)
        {
            e.printStackTrace();
        }

        if (accessToken != null)
        {
            mBus.post(accessToken);
        }
    }
}