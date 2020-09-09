package com.example.recoverdeletedmessages.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.constants.TableName;
import com.example.recoverdeletedmessages.database.MyDataBaseHelper;
import com.example.recoverdeletedmessages.interfaces.OnRecyclerItemClickeListener;
import com.example.recoverdeletedmessages.models.Messages;
import com.example.recoverdeletedmessages.models.Users;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.MyViewHolder> {

    private Context context;
    private List<Users> modelMainList;
    private OnRecyclerItemClickeListener onRecyclerItemClickeListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickeListener onRecyclerItemClickeListener) {
        this.onRecyclerItemClickeListener = onRecyclerItemClickeListener;
    }


    public AdapterMain(Context context, List<Users> notesList) {
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Users currentItem = modelMainList.get(position);
        holder.titleTV.setText(currentItem.getUserTitle());
        setImageFromFile(currentItem.getLargeIconUri(), holder.imageView);
        MyDataBaseHelper helper = new MyDataBaseHelper(context);
        List<Messages> list = helper.getSelectedMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, currentItem.getUserTitle());
        holder.descTV.setText(list.get(0).getMessage());

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
        return modelMainList.size();
    }


    private void setImageFromFile(String filePath, ImageView imageView) {
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }


}
