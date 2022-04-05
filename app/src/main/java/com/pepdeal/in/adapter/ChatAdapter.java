package com.pepdeal.in.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.model.messagemodel.ChatModel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by qboxus on 4/3/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatModel> mDataSet;
    String myID;
    private static final int MY_CHAT = 1;
    private static final int FRIEND_CHAT = 2;
    private static final int ALERT_MESSAGE = 7;



    Context context;
    Integer today_day = 0;

    private OnItemClickListener listener;
    private OnLongClickListener longListener;

    public interface OnItemClickListener {
        void onItemClick(ChatModel item, View view);
    }

    public interface OnLongClickListener {
        void onLongClick(ChatModel item, View view);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     *                Device id
     */

    public ChatAdapter(List<ChatModel> dataSet, String id, Context context, OnItemClickListener listener, OnLongClickListener longListener) {
        mDataSet = dataSet;
        this.myID = id;
        this.context = context;
        this.listener = listener;
        this.longListener = longListener;
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

    }


    // this is the all types of view that is used in the chat
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype) {
            // we have 4 type of layout in chat activity text chat of my and other and also
            // image layout of my and other
            case MY_CHAT:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                Chatviewholder mychatHolder = new Chatviewholder(v);
                return mychatHolder;
            case FRIEND_CHAT:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                Chatviewholder friendchatHolder = new Chatviewholder(v);
                return friendchatHolder;
            case ALERT_MESSAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                Alertviewholder alertviewholder = new Alertviewholder(v);
                return alertviewholder;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatModel chat = mDataSet.get(position);
        if (chat.getType().equals("text")) {
            Chatviewholder chatviewholder = (Chatviewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1")) {
                    chatviewholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                } else
                    chatviewholder.message_seen.setText("Sent");

            } else {
                chatviewholder.message_seen.setText("");
            }
            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chatviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatviewholder.datetxt.setVisibility(View.VISIBLE);
                    chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
                chatviewholder.message.setText(chat.getText());
            } else {
                chatviewholder.datetxt.setVisibility(View.VISIBLE);
                chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                chatviewholder.message.setText(chat.getText());
            }

            chatviewholder.bind(chat, longListener);

        } else if (chat.getType().equals("delete")) {
            Alertviewholder alertviewholder = (Alertviewholder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.delete_message_text));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_background_2));

            alertviewholder.message.setText("This message is deleted by " + chat.getSender_name());

            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                    alertviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

            } else {
                alertviewholder.datetxt.setVisibility(View.VISIBLE);
                alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }

        }


    }

    @Override
    public int getItemViewType(int position) {
        // get the type it view ( given message is from sender or receiver)
        if (mDataSet.get(position).getType().equals("text")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return MY_CHAT;
            }
            return FRIEND_CHAT;
        } else {
            return ALERT_MESSAGE;
        }
    }

    /**
     * Inner Class for a recycler view
     */

    // this is the all the viewholder in which first is for the text
    class Chatviewholder extends RecyclerView.ViewHolder {
        TextView message, datetxt, message_seen;
        View view;

        public Chatviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.msgtxt);
            this.datetxt = view.findViewById(R.id.datetxt);
            message_seen = view.findViewById(R.id.message_seen);
        }

        public void bind(final ChatModel item, final OnLongClickListener long_listener) {
            message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongClick(item, v);
                    return false;
                }
            });
        }
    }

    // forth is for the alert type view
    class Alertviewholder extends RecyclerView.ViewHolder {
        TextView message, datetxt;
        View view;

        public Alertviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.message);
            this.datetxt = view.findViewById(R.id.datetxt);
        }

    }

    // change the date into (today ,yesterday and date)
    public String ChangeDate(String date) {

        try {
            long currenttime = System.currentTimeMillis();

            //database date in millisecond
            long databasedate = 0;
            Date d = null;
            try {
                d = Utils.df.parse(date);
                databasedate = d.getTime();

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long difference = currenttime - databasedate;
            if (difference < 86400000) {
                int chatday = Integer.parseInt(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                if (today_day == chatday)
                    return "Today " + sdf.format(d);
                else if ((today_day - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            } else if (difference < 172800000) {
                int chatday = Integer.parseInt(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                if ((today_day - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
            return sdf.format(d);
        } catch (Exception e) {

        } finally {
            return date;
        }

    }

    // change the date into (today ,yesterday and date)
    public String ChangeDate_to_time(String date) {
        try {
            Date d = null;
            try {
                d = Utils.df2.parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            return sdf.format(d);
        } catch (Exception e) {

        } finally {
            return date;
        }

    }

    // get the audio file duration that is store in our directory
    public String getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            long second = (file_duration / 1000) % 60;
            long minute = (file_duration / (1000 * 60)) % 60;

            return String.format("%02d:%02d", minute, second);
        } catch (Exception e) {

        }
        return null;
    }

}
