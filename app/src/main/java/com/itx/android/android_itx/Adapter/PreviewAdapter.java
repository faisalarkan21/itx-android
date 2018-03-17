package com.itx.android.android_itx.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.ViewHolder.PreviewViewHolder;

import java.util.ArrayList;

/**
 * Created by aladhims on 25/02/18.
 */

public class PreviewAdapter extends RecyclerView.Adapter<PreviewViewHolder> {

    private ArrayList<ImageHolder> imageList;
    private Context mContext;
    private previewInterface mClickHandler;

    public PreviewAdapter(ArrayList<ImageHolder> images, Context activity, previewInterface handler) {
        imageList = images;
        mContext = activity;
        mClickHandler = handler;
    }

    @Override
    public PreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_image, parent, false);

        return new PreviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PreviewViewHolder holder, int position) {
        ImageHolder currentImage = imageList.get(position);
        Uri currentUri = currentImage.getmUri();

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .override(200, 200);

        if (currentUri != null) {

            Glide.with(mContext).asBitmap().apply(options).load(currentUri).into(holder.mIvPreview);

        } else {
            Glide.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + currentImage.getmImage().getmMedium())
                    .into(holder.mIvPreview);
        }

        holder.mFabDeletePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                mClickHandler.deleteCurrentPreviewImage(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface previewInterface {
        void deleteCurrentPreviewImage(int position);
    }
}
