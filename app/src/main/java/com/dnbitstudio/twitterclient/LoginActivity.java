package com.dnbitstudio.twitterclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.dnbitstudio.twitterclient.bus.BusProvider;
import com.dnbitstudio.twitterclient.service.TwitterOAuthService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * The activity to centralize all login related tasks. Extends AppCompatActivity
 */
public class LoginActivity extends AppCompatActivity
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final String OAUTH_VERIFIER_QUERY_PARAMETER = "oauth_verifier";

    @Bind(R.id.activity_login_webView)
    WebView webView;

    private Bus bus;
    private RequestToken requestToken;

    /**
     * Method to start this activity for result from the given parameters
     *
     * @param activity    the activity calling this one for result
     * @param requestCode the request code to be used on result
     */
    public static void launchActivityForResult(Activity activity, int requestCode)
    {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initializeFields();

        TwitterOAuthService.startActionRetrieveRequestToken(this);
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

    /**
     * Method to initialize the object's fields when onCreate method is called
     */
    private void initializeFields()
    {
        bus = BusProvider.getBus();
        webView.setWebViewClient(new LoginWebViewClient(this));
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setSaveFormData(false);
    }

    /**
     * Launches the request token's authentication url in the web view with cleared cookies
     */
    private void launchWebViewWithClearedCookies()
    {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            cookieManager.removeAllCookie();
        }
        else
        {
            cookieManager.removeAllCookies(null);
        }

        webView.loadUrl(requestToken.getAuthenticationURL());
    }

    /**
     * Method to request the access token via the TwitterOAuthService class
     *
     * @param authenticatedURL the authenticatedURL received from twitter
     */
    protected void requestAccessToken(String authenticatedURL)
    {
        String verifier = Uri.parse(authenticatedURL).getQueryParameter
                (OAUTH_VERIFIER_QUERY_PARAMETER);
        TwitterOAuthService.startActionRetrieveAccessToken(this, requestToken, verifier);
    }

    /**
     * Returns the result to the activity that started this one for result
     *
     * @param resultCode the result code to be used
     */
    private void returnResultToCallingActivity(int resultCode)
    {
        Intent returnIntent = new Intent();
        setResult(resultCode, returnIntent);
        finish();
    }

    /**
     * Otto event bus subscribed method to receive the request token
     *
     * @param requestToken received from twitter
     */
    @Subscribe
    public void handleReceivedRequestToken(RequestToken requestToken)
    {
        this.requestToken = requestToken;
        launchWebViewWithClearedCookies();
    }

    /**
     * Otto event bus subscribed method to receive the access token
     *
     * @param accessToken received from twitter
     */
    @Subscribe
    public void handleReceivedAccessToken(AccessToken accessToken)
    {
        SharedPreferences preferences = getSharedPreferences(LaunchActivity
                .OAUTH_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LaunchActivity.ACCESS_TOKEN_KEY, accessToken.getToken());
        editor.putString(LaunchActivity.ACCESS_TOKEN_SECRET_KEY, accessToken.getTokenSecret());
        editor.commit();

        returnResultToCallingActivity(Activity.RESULT_OK);
    }
}