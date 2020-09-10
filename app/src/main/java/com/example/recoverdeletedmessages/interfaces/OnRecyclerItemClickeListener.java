package com.example.recoverdeletedmessages.interfaces;

import android.view.View;

public interface OnRecyclerItemClickeListener {

    void onItemClicked(int position);

    void onItemLongClicked(int position);

    void onItemCheckBoxClicked(View view, int position);
}
