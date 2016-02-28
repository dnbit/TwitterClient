package com.dnbitstudio.twitterclient.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnbitstudio.twitterclient.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.Status;

/**
 * Adapter for the timeline listview. Extends ArrayAdapter with Status objects.
 */
public class TimelineAdapter extends ArrayAdapter<Status>
{
    private final String LOG_TAG = this.getClass().getSimpleName();

    private final Context context;
    private final int layoutResource;
    private final ArrayList<Status> statusesList;
    private final LayoutInflater inflater;

    /**
     * Constructor
     *
     * @param context        The current context.
     * @param layoutResource The resource ID for a layout file to use when instantiating views.
     * @param statusesList   The objects to represent in the ListView.
     */
    public TimelineAdapter(Context context, int layoutResource, ArrayList<Status> statusesList)
    {
        super(context, layoutResource, statusesList);
        this.context = context;
        this.layoutResource = layoutResource;
        this.statusesList = statusesList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        if (statusesList.size() > 0)
        {
            for (Status status : statusesList)
            {
                // fetch in advance to improve user experience on scroll
                Picasso.with(context).load(status.getUser().getBiggerProfileImageURL()).fetch();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Status status = statusesList.get(position);
        populateViewHolder(viewHolder, status);

        return convertView;
    }

    /**
     * Method to populate the view holder with the values from a given status
     *
     * @param viewHolder the view holder to populate
     * @param status     the status used to populate the view holder
     */
    private void populateViewHolder(ViewHolder viewHolder, Status status)
    {
        if (status.isRetweet())
        {
            String formattedRetweetedBy = String.format(context.getString(R.string.retweetedBy),
                    status.getUser().getName());
            viewHolder.retweetedBy.setText(formattedRetweetedBy);
            viewHolder.retweetedBy.setVisibility(View.VISIBLE);
            status = status.getRetweetedStatus();
        }
        else
        {
            viewHolder.retweetedBy.setText("");
            viewHolder.retweetedBy.setVisibility(View.GONE);
        }

        if (status.getUser().getProfileImageURL() != null)
        {
            Picasso.with(context)
                    .load(status.getUser().getBiggerProfileImageURL())
                    .into(viewHolder.profileImage);
        }

        viewHolder.name.setText(status.getUser().getName());
        viewHolder.text.setText(status.getText());
        viewHolder.elapsedTime.setText(calculateTimeSinceStatusWasPosted(status));

        String formattedScreenName = String.format(context.getString(R.string.twitterHandle),
                status.getUser().getScreenName());
        viewHolder.screenName.setText(formattedScreenName);

        String formattedRetweetCount = String.format(context.getString(R.string.retweetCount),
                status.getRetweetCount());
        viewHolder.retweetCount.setText(formattedRetweetCount);

        String formattedFavouriteCount = String.format(context.getString(R.string.favouriteCount),
                status.getFavoriteCount());
        viewHolder.favouriteCount.setText(formattedFavouriteCount);
    }

    /**
     * Calculates the elapsed time since status was posted
     *
     * @param status the status for which to calculate the elapsedTime
     * @return the string representing the elapsed time
     */
    private String calculateTimeSinceStatusWasPosted(Status status)
    {
        long createdAt = status.getCreatedAt().getTime();
        long now = System.currentTimeMillis();
        return DateUtils.getRelativeTimeSpanString(createdAt, now, 0,
                DateUtils.FORMAT_ABBREV_ALL).toString();
    }

    /**
     * View holder class to improve listview scrolling performance
     */
    protected static class ViewHolder
    {
        @Bind(R.id.list_item_timeline_retweetedBy)
        TextView retweetedBy;
        @Bind(R.id.list_item_timeline_profileImage)
        ImageView profileImage;
        @Bind(R.id.list_item_timeline_name)
        TextView name;
        @Bind(R.id.list_item_timeline_screenName)
        TextView screenName;
        @Bind(R.id.list_item_timeline_text)
        TextView text;
        @Bind(R.id.list_item_timeline_elapsedTime)
        TextView elapsedTime;
        @Bind(R.id.list_item_timeline_retweetCount)
        TextView retweetCount;
        @Bind(R.id.list_item_timeline_favouriteCount)
        TextView favouriteCount;

        /**
         * Constructor
         *
         * @param view the view to bind the butterknife elements
         */
        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }
}