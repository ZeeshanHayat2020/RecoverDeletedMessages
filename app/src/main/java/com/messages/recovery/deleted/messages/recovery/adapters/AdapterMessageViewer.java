package com.messages.recovery.deleted.messages.recovery.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.activities.ActivityMessagesViewer;
import com.messages.recovery.deleted.messages.recovery.interfaces.OnRecyclerItemClickeListener;
import com.messages.recovery.deleted.messages.recovery.models.Messages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        public CardView cardView;
        public TextView messageTV;
        public TextView timeTv;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.itemView_ac_msgView_cardView);
            messageTV = view.findViewById(R.id.itemView_ac_msgView_msgTV);
            timeTv = view.findViewById(R.id.itemView_ac_msgView_timeTV);
            checkBox = view.findViewById(R.id.itemView_ac_msgView_checkBox);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_msg_viewer2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Messages currentItem = messagesList.get(position);
        holder.messageTV.setText(currentItem.getMessage());
        holder.timeTv.setText(createDate(currentItem.getTimeStamp()));

        switch (activityMessagesViewer.currentMessagesTitle) {
            case "Whatsapp Messages": {
                setCheckBoxTintColor(holder.checkBox, context.getResources().getColor(R.color.colorFragmentWhatsappToolbar));
            }
            break;
            case "Instagram Messages": {
                setCheckBoxTintColor(holder.checkBox, context.getResources().getColor(R.color.colorFragmentInstaToolbar));

            }
            break;
            case "Facebook Messages": {
                setCheckBoxTintColor(holder.checkBox, context.getResources().getColor(R.color.colorFragmentFbToolbar));

            }
            break;
            case "Sms": {
                setCheckBoxTintColor(holder.checkBox, context.getResources().getColor(R.color.colorFragmentSmsToolbar));
            }
            break;
        }

        if (activityMessagesViewer.isContextualMenuOpen) {
            if (activityMessagesViewer.isSelectAll) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            holder.timeTv.setVisibility(View.INVISIBLE);
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.timeTv.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.INVISIBLE);

        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
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
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, HH:mm a");
        return sdf.format(d);
    }

    private void setCheckBoxTintColor(CheckBox btn, int tintColor) {
        if (Build.VERSION.SDK_INT < 21) {
            CompoundButtonCompat.setButtonTintList(btn, ColorStateList.valueOf(tintColor));
        } else {
            btn.setButtonTintList(ColorStateList.valueOf(tintColor));
        }
    }
}
