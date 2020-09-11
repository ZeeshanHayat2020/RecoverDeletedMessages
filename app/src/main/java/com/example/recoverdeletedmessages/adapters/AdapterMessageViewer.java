package com.example.recoverdeletedmessages.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recoverdeletedmessages.R;
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
    private List<Messages> messagesList;
    private OnRecyclerItemClickeListener onRecyclerItemClickeListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickeListener onRecyclerItemClickeListener) {
        this.onRecyclerItemClickeListener = onRecyclerItemClickeListener;
    }


    public AdapterMessageViewer(Context context, List<Messages> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTV;
        public TextView timeTv;

        public MyViewHolder(View view) {
            super(view);
            messageTV = view.findViewById(R.id.itemView_ac_msgView_msgTV);
            timeTv = view.findViewById(R.id.itemView_ac_msgView_timeTV);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_msg_viewer, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Messages currentItem = messagesList.get(position);
        holder.messageTV.setText(currentItem.getMessage());
        holder.timeTv.setText(formatDate(String.valueOf(currentItem.getTimeStamp())));

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
   /* private String formatDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000L);
        String date = DateFormat.format("MMMM d hh:mm a", cal).toString();

        return date;
    }*/
}
