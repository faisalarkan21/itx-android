package com.itx.android.android_itx.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.itx.android.android_itx.R;

/**
 * Created by aladhims on 25/02/18.
 */

public class PreviewViewHolder extends RecyclerView.ViewHolder {

    public ImageView mIvPreview;

    public PreviewViewHolder(View itemView) {
        super(itemView);

        mIvPreview = (ImageView) itemView.findViewById(R.id.iv_img_preview);
    }
}
