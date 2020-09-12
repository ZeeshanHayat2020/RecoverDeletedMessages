package com.example.recoverdeletedmessages.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.activities.ActivityMessagesViewer;
import com.example.recoverdeletedmessages.activities.ActivityNotificationAccess;
import com.example.recoverdeletedmessages.interfaces.OnRecyclerItemClickeListener;
import com.example.recoverdeletedmessages.models.Messages;
import com.example.recoverdeletedmessages.models.Users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessageViewer extends RecyclerView.Adapter<AdapterMessageViewer.MyViewHolder> {

    private Context context;
    private ActivityMessagesViewer activityMessagesViewer;
    private List<Messages> messagesList;
    private OnRecyclerItemClickeListener onRecyclerItemClickeListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickeListener onRecyclerItemClickeListener) {
        this.onRecyclerItemClickeListener = onRecyclerItemClickeListener;
    }


    public AdapterMessageViewer(Context context, List<Messages> messagesList, ActivityMessagesViewer activityMessagesViewer) {
        this.context = context;
        this.messagesList = messagesList;
        this.activityMessagesViewer = activityMessagesViewer;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTV;
        public TextView timeTv;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            messageTV = view.findViewById(R.id.itemView_ac_msgView_msgTV);
            timeTv = view.findViewById(R.id.itemView_ac_msgView_timeTV);
            checkBox = view.findViewById(R.id.itemView_ac_msgView_checkBox);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_msg_viewer, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Messages currentItem = messagesList.get(position);
        holder.messageTV.setText(currentItem.getMessage());
        holder.timeTv.setText(createDate(currentItem.getTimeStamp()));


        if (activityMessagesViewer.isContextualMenuOpen) {
            if (activityMessagesViewer.isSelectAll) {
                holder.checkBox.setChecked(true);
            } else {
                if (activityMessagesViewer.selectedIndex == position) {
                    holder.checkBox.setChecked(false);
                }
            }
            holder.timeTv.setVisibility(View.INVISIBLE);
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.timeTv.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickeListener != null) {
                    if (!activityMessagesViewer.isContextualMenuOpen) {
                        if (position != RecyclerView.NO_POSITION) {
                            onRecyclerItemClickeListener.onItemClicked(position);
                        }
                    } else {
                        holder.checkBox.performClick();
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onRecyclerItemClickeListener != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        onRecyclerItemClickeListener.onItemLongClicked(position);
                    }
                }
                return false;
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickeListener != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        onRecyclerItemClickeListener.onItemCheckBoxClicked(view, position);
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static CharSequence createDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(d);
    }
   /* private String formatDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000L);
        String date = DateFormat.format("MMMM d hh:mm a", cal).toString();

        return date;
    }*/
}
