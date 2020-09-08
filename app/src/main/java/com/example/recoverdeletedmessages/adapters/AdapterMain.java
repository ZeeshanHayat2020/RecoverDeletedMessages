package com.example.recoverdeletedmessages.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.models.ModelMain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.MyViewHolder> {

    private Context context;
    private List<ModelMain> modelMainList;


    public AdapterMain(Context context, List<ModelMain> notesList) {
        this.context = context;
        this.modelMainList = notesList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageView;
        public TextView titleTV;
        public TextView descTV;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.itemView_main_iv);
            titleTV = view.findViewById(R.id.itemView_main_title_tv);
            descTV = view.findViewById(R.id.itemView_main_desc_tv);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_main, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ModelMain currentItem = modelMainList.get(position);
        holder.titleTV.setText(currentItem.getTitle());
        holder.descTV.setText(currentItem.getMessage());
    }

    @Override
    public int getItemCount() {
        return modelMainList.size();
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
}
