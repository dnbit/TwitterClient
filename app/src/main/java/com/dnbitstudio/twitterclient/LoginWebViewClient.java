package com.dnbitstudio.twitterclient;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A custom Web view client to handle twitter login
 */
public class LoginWebViewClient extends WebViewClient
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    private final LoginActivity loginActivity;

    /**
     * public constructor
     *
     * @param loginActivity the instance of the login activity using this custom web view client
     */
    public LoginWebViewClient(LoginActivity loginActivity)
    {
        this.loginActivity = loginActivity;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        if (url.startsWith(loginActivity.getString(R.string.callback_url)))
        {
            loginActivity.requestAccessToken(url);
        }

        super.onPageStarted(view, url, favicon);
    }
}