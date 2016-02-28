package com.dnbitstudio.twitterclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.dnbitstudio.twitterclient.service.TwitterAPIRequestsService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Activity to post new tweets. Extends AppCompatActivity
 */
public class PostTweetActivity extends AppCompatActivity
{
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final int MAXIMUM_TWEET_LENGTH = 140;

    @Bind(R.id.activity_post_tweet_message_editText)
    EditText messageEditText;
    @Bind(R.id.activity_post_tweet_counter_textView)
    TextView counterTextView;
    @Bind(R.id.activity_post_tweet_errorMessage_textView)
    TextView errorMessageTextView;

    /**
     * Method to start this activity from the given context
     *
     * @param context the context starting the activity
     */
    public static void launchActivity(Context context)
    {
        Intent intent = new Intent(context, PostTweetActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tweet);
        ButterKnife.bind(this);
    }

    /**
     * Method to control that the length of the message is valid
     *
     * @param message the message which length needs to be validated
     * @return the boolean result of the validation
     */
    private boolean validateMessageLength(String message)
    {
        if (message.length() == 0)
        {
            errorMessageTextView.setText(getResources().getString(R.string.message_too_short));
            return false;
        }

        if (message.length() > MAXIMUM_TWEET_LENGTH)
        {
            errorMessageTextView.setText(getResources().getString(R.string.message_too_long));
            return false;
        }

        return true;
    }

    /**
     * OnClick method bound by ButterKnife to post a new tweet when the right button is pressed
     */
    @OnClick(R.id.activity_post_tweet_send_button)
    protected void postNewTweet()
    {
        String message = messageEditText.getText().toString();
        if (validateMessageLength(message))
        {
            TwitterAPIRequestsService.startActionRequestPostNewTweet(this, message);
            TwitterAPIRequestsService.startActionRequestHomeTimeline(this);
            finish();
        }
    }

    /**
     * OnTextChanged method bound by ButterKnife to calculate the remaining characters available
     */
    @OnTextChanged(R.id.activity_post_tweet_message_editText)
    protected void updateRemainingCharactersCounter()
    {
        errorMessageTextView.setText("");
        int messageLength = messageEditText.getText().toString().length();
        int remainingLength = MAXIMUM_TWEET_LENGTH - messageLength;
        counterTextView.setText(String.valueOf(remainingLength));
    }
}