package com.dnbitstudio.twitterclient;

import android.content.Context;
import android.content.SharedPreferences;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * A class to ensure the right twitter instance is retrieved
 */
public class TwitterCustomInstanceBuilder
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    /**
     * The method that returns the right twitter instance
     *
     * @param context the context requesting the instance
     * @return the right twitter instance
     */
    public static Twitter getInstance(Context context)
    {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(context.getString(R.string.consumer_key));
        builder.setOAuthConsumerSecret(context.getString(R.string.consumer_secret));
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);

        SharedPreferences sharedPreferences = context.getSharedPreferences
                (LaunchActivity.OAUTH_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(LaunchActivity.ACCESS_TOKEN_KEY))
        {
            String token = sharedPreferences.getString(LaunchActivity.ACCESS_TOKEN_KEY, "");
            String tokenSecret = sharedPreferences.getString(LaunchActivity
                    .ACCESS_TOKEN_SECRET_KEY, "");
            AccessToken accessToken = new AccessToken(token, tokenSecret);
            return factory.getInstance(accessToken);
        }

        return factory.getInstance();
    }
}