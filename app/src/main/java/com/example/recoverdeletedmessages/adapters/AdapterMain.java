package com.example.recoverdeletedmessages.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.constants.Constant;
import com.example.recoverdeletedmessages.constants.TableName;
import com.example.recoverdeletedmessages.database.MyDataBaseHelper;
import com.example.recoverdeletedmessages.fragments.FragmentDefault;
import com.example.recoverdeletedmessages.fragments.FragmentFacebook;
import com.example.recoverdeletedmessages.fragments.FragmentInstagram;
import com.example.recoverdeletedmessages.fragments.FragmentWhatsApp;
import com.example.recoverdeletedmessages.interfaces.OnRecyclerItemClickeListener;
import com.example.recoverdeletedmessages.models.Messages;
import com.example.recoverdeletedmessages.models.Users;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.MyViewHolder> {

    private Context context;
    private FragmentWhatsApp fragmentWhatsApp;
    private FragmentFacebook fragmentFacebook;
    private FragmentInstagram fragmentInstagram;
    private FragmentDefault fragmentDefault;
    private List<Users> usersList;
    private OnRecyclerItemClickeListener onRecyclerItemClickeListener;
    private String activeFragment;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickeListener onRecyclerItemClickeListener) {
        this.onRecyclerItemClickeListener = onRecyclerItemClickeListener;
    }


    public AdapterMain(Context context,
                       FragmentWhatsApp fragmentWhatsApp,
                       FragmentFacebook fragmentFacebook,
                       FragmentInstagram fragmentInstagram,
                       FragmentDefault fragmentDefault,
                       List<Users> usersList, String activeFragment) {
        this.context = context;
        this.fragmentWhatsApp = fragmentWhatsApp;
        this.fragmentFacebook = fragmentFacebook;
        this.fragmentInstagram = fragmentInstagram;
        this.fragmentDefault = fragmentDefault;
        this.usersList = usersList;
        this.activeFragment = activeFragment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageView;
        public TextView titleTV;
        public TextView descTV;
        public TextView timeTV;
        public TextView readStatusTV;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.itemView_main_iv);
            titleTV = view.findViewById(R.id.itemView_main_title_tv);
            descTV = view.findViewById(R.id.itemView_main_desc_tv);
            timeTV = view.findViewById(R.id.itemView_main_time_tv);
            readStatusTV = view.findViewById(R.id.itemView_main_readStatus_tv);
            checkBox = itemView.findViewById(R.id.itemView_main_checkBox);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_main, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Users currentItem = usersList.get(position);
        holder.titleTV.setText(currentItem.getUserTitle());
        setImageFromFile(currentItem.getLargeIconUri(), holder.imageView);

        MyDataBaseHelper helper = new MyDataBaseHelper(context);
        switch (activeFragment) {
            case Constant.ACTIVE_FRAGMENT_WHATS_APP: {
                int color = context.getResources().getColor(R.color.colorFragmentWhatsappToolbar);
                holder.readStatusTV.setBackground(setReadStatusBackground(color));
                List<Messages> list = helper.getSelectedMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, currentItem.getUserTitle());
                if (!list.isEmpty()) {
                    if (list.get(list.size() - 1).getReadStatus().equals("Read")) {
                        holder.readStatusTV.setVisibility(View.INVISIBLE);
                    }
                    setLimitedMessage(list.get(list.size() - 1).getMessage(), holder.descTV);
                    holder.timeTV.setText(createDateTime(list.get(list.size() - 1).getTimeStamp()));
                    setCheckBoxTintColor(holder.checkBox, color);
                }
                if (fragmentWhatsApp.isContextualMenuOpen) {
                    if (fragmentWhatsApp.isSelectAll) {
                        holder.checkBox.setChecked(true);
                    } else {
                        holder.checkBox.setChecked(false);
                    }
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.timeTV.setVisibility(View.INVISIBLE);

                } else {
                    holder.checkBox.setVisibility(View.INVISIBLE);
                    holder.timeTV.setVisibility(View.VISIBLE);
                }
            }
            break;
            case Constant.ACTIVE_FRAGMENT_FACEBOOK: {
                int color = context.getResources().getColor(R.color.colorFragmentFbToolbar);
                holder.readStatusTV.setBackground(setReadStatusBackground(color));
                List<Messages> list = helper.getSelectedMessages(TableName.TABLE_NAME_MESSAGES_FACEBOOK, currentItem.getUserTitle());
                if (!list.isEmpty()) {
                    if (list.get(list.size() - 1).getReadStatus().equals("Read")) {
                        holder.readStatusTV.setVisibility(View.INVISIBLE);
                    }
                    setLimitedMessage(list.get(list.size() - 1).getMessage(), holder.descTV);
                    holder.timeTV.setText(createDateTime(list.get(list.size() - 1).getTimeStamp()));
                    setCheckBoxTintColor(holder.checkBox, color);
                }
                if (fragmentFacebook.isContextualMenuOpen) {
                    if (fragmentFacebook.isSelectAll) {
                        holder.checkBox.setChecked(true);
                    } else {
                        holder.checkBox.setChecked(false);
                    }
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.timeTV.setVisibility(View.INVISIBLE);

                } else {
                    holder.checkBox.setVisibility(View.INVISIBLE);
                    holder.timeTV.setVisibility(View.VISIBLE);
                }

            }
            break;
            case Constant.ACTIVE_FRAGMENT_ACTIVE_INSTA: {
                int color = context.getResources().getColor(R.color.colorFragmentInstaToolbar);
                holder.readStatusTV.setBackground(setReadStatusBackground(color));
                List<Messages> list = helper.getSelectedMessages(TableName.TABLE_NAME_MESSAGES_INSTAGRAM, currentItem.getUserTitle());
                if (!list.isEmpty()) {
                    if (list.get(list.size() - 1).getReadStatus().equals("Read")) {
                        holder.readStatusTV.setVisibility(View.INVISIBLE);
                    }
                    setLimitedMessage(list.get(list.size() - 1).getMessage(), holder.descTV);
                    holder.timeTV.setText(createDateTime(list.get(list.size() - 1).getTimeStamp()));
                    setCheckBoxTintColor(holder.checkBox, color);
                }
                if (fragmentInstagram.isContextualMenuOpen) {
                    if (fragmentInstagram.isSelectAll) {
                        holder.checkBox.setChecked(true);
                    } else {
                        holder.checkBox.setChecked(false);
                    }
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.timeTV.setVisibility(View.INVISIBLE);

                } else {
                    holder.checkBox.setVisibility(View.INVISIBLE);
                    holder.timeTV.setVisibility(View.VISIBLE);
                }

            }
            break;

            case Constant.ACTIVE_FRAGMENT_DEFAULT: {
                int color = context.getResources().getColor(R.color.colorFragmentSmsToolbar);
                holder.readStatusTV.setBackground(setReadStatusBackground(color));
                List<Messages> list = helper.getSelectedMessages(TableName.TABLE_NAME_MESSAGES_DEFAULT, currentItem.getUserTitle());
                if (!list.isEmpty()) {
                    if (list.get(list.size() - 1).getReadStatus().equals("Read")) {
                        holder.readStatusTV.setVisibility(View.INVISIBLE);
                    }
                    setLimitedMessage(list.get(list.size() - 1).getMessage(), holder.descTV);
                    holder.timeTV.setText(createDateTime(list.get(list.size() - 1).getTimeStamp()));
                    setCheckBoxTintColor(holder.checkBox, color);
                }
                if (fragmentDefault.isContextualMenuOpen) {
                    if (fragmentDefault.isSelectAll) {
                        holder.checkBox.setChecked(true);
                    } else {
                        holder.checkBox.setChecked(false);
                    }
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.timeTV.setVisibility(View.INVISIBLE);

                } else {
                    holder.checkBox.setVisibility(View.INVISIBLE);
                    holder.timeTV.setVisibility(View.VISIBLE);
                }

            }
            break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickeListener != null) {
                    switch (activeFragment) {
                        case Constant.ACTIVE_FRAGMENT_WHATS_APP: {
                            if (!fragmentWhatsApp.isContextualMenuOpen) {
                                if (position != RecyclerView.NO_POSITION) {
                                    onRecyclerItemClickeListener.onItemClicked(position);
                                }
                            } else {
                                holder.checkBox.performClick();
                            }
                        }
                        break;
                        case Constant.ACTIVE_FRAGMENT_FACEBOOK: {
                            if (!fragmentFacebook.isContextualMenuOpen) {
                                if (position != RecyclerView.NO_POSITION) {
                                    onRecyclerItemClickeListener.onItemClicked(position);
                                }
                            } else {
                                holder.checkBox.performClick();
                            }
                        }
                        break;
                        case Constant.ACTIVE_FRAGMENT_ACTIVE_INSTA: {
                            if (!fragmentInstagram.isContextualMenuOpen) {
                                if (position != RecyclerView.NO_POSITION) {
                                    onRecyclerItemClickeListener.onItemClicked(position);
                                }
                            } else {
                                holder.checkBox.performClick();
                            }
                        }
                        break;
                        case Constant.ACTIVE_FRAGMENT_DEFAULT: {
                            if (!fragmentDefault.isContextualMenuOpen) {
                                if (position != RecyclerView.NO_POSITION) {
                                    onRecyclerItemClickeListener.onItemClicked(position);
                                }
                            } else {
                                holder.checkBox.performClick();
                            }
                        }
                        break;
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

    public static CharSequence createDateTime(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, HH:mm a");
        return sdf.format(d);
    }

    private void setLimitedMessage(String message, TextView textView) {

        if (message.length() > 25) {
            message = message.substring(0, 25) + "...";
            textView.setText(message);
        } else {

            textView.setText(message);

        }
    }

    private void setCheckBoxTintColor(CheckBox btn, int tintColor) {
        if (Build.VERSION.SDK_INT < 21) {
            CompoundButtonCompat.setButtonTintList(btn, ColorStateList.valueOf(tintColor));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
        } else {
            btn.setButtonTintList(ColorStateList.valueOf(tintColor));//setButtonTintList is accessible directly on API>19
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    private void setImageFromFile(String filePath, ImageView imageView) {
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }

    private GradientDrawable setReadStatusBackground(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(10f);
        shape.setColor(backgroundColor);
        return shape;
    }

}
