package com.itx.android.android_itx.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itx.android.android_itx.R;
import com.itx.android.android_itx.ViewHolder.InventoryViewHolder;
import com.itx.android.android_itx.ViewHolder.PreviewViewHolder;

import java.util.ArrayList;

/**
 * Created by aladhims on 25/02/18.
 */

public class PreviewAdapter extends RecyclerView.Adapter<PreviewViewHolder> {

    private ArrayList<Uri> imageUriList;
    private Context mContext;
    private previewInterface mClickHandler;

    public PreviewAdapter(ArrayList<Uri> uris, Context activity, previewInterface handler){
        imageUriList = uris;
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
        Uri currentUri = imageUriList.get(position);
        holder.mIvPreview.setImageURI(currentUri);
        holder.mFabDeletePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                imageUriList.remove(pos);
                notifyDataSetChanged();
                mClickHandler.deleteCurrentPreviewImage(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }

    public interface previewInterface {
        void deleteCurrentPreviewImage(int position);
    }
}
