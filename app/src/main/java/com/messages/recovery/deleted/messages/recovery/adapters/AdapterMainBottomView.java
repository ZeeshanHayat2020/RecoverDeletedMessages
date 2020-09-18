package com.messages.recovery.deleted.messages.recovery.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.activities.MainActivity;
import com.messages.recovery.deleted.messages.recovery.interfaces.OnRecyclerItemClickeListener;
import com.messages.recovery.deleted.messages.recovery.models.ModelBottomView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMainBottomView extends RecyclerView.Adapter<AdapterMainBottomView.MyViewHolder> {

    private Context context;
    private MainActivity mainActivity;
    private List<ModelBottomView> bottomViewList;
    private OnRecyclerItemClickeListener onRecyclerItemClickeListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickeListener onRecyclerItemClickeListener) {
        this.onRecyclerItemClickeListener = onRecyclerItemClickeListener;
    }


    public AdapterMainBottomView(Context context, MainActivity mainActivity, List<ModelBottomView> bottomViewList) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.bottomViewList = bottomViewList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public CircleImageView imageView;
        public TextView titleTv;


        public MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.itemView_ac_main_bottomRecycler_cardView);
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
        if (mainActivity.selectedIndex == position) {
            translateAnim(holder.cardView, 0f, -20f);
        } else {
            translateAnim(holder.cardView, 0, 0);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

    private void translateAnim(View view, float translateFrom, float translateTo) {
        ObjectAnimator textViewAnimator = ObjectAnimator.ofFloat(view, "translationY", translateFrom, translateTo);
        textViewAnimator.setDuration(50);
        textViewAnimator.start();
    }

}
