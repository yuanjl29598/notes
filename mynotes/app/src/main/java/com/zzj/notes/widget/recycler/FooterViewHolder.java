package com.zzj.notes.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.zzj.notes.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yjl on 2017/3/17.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.rcv_load_more)
    ProgressWheel rcvLoadMore;

    public FooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }
}
