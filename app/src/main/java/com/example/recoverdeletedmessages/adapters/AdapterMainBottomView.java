package com.example.recoverdeletedmessages.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.activities.ActivityMessagesViewer;
import com.example.recoverdeletedmessages.interfaces.OnRecyclerItemClickeListener;
import com.example.recoverdeletedmessages.models.Messages;
import com.example.recoverdeletedmessages.models.ModelBottomView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdapterMainBottomView extends RecyclerView.Adapter<AdapterMainBottomView.MyViewHolder> {

    private Context context;
    private List<ModelBottomView> bottomViewList;
    private OnRecyclerItemClickeListener onRecyclerItemClickeListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickeListener onRecyclerItemClickeListener) {
        this.onRecyclerItemClickeListener = onRecyclerItemClickeListener;
    }


    public AdapterMainBottomView(Context context, List<ModelBottomView> bottomViewList) {
        this.context = context;
        this.bottomViewList = bottomViewList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTv;


        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.itemView_ac_main_bottomRecycler_iv);
            titleTv = view.findViewById(R.id.itemView_ac_main_bottomRecycler_tv);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_main_bottom_recycler, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ModelBottomView currentItem = bottomViewList.get(position);
        holder.imageView.setImageResource(currentItem.getImageId());
        holder.titleTv.setText(currentItem.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickeListener != null) {

                    if (position != RecyclerView.NO_POSITION) {
                        onRecyclerItemClickeListener.onItemClicked(position);
                    }

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return bottomViewList.size();
    }


}
