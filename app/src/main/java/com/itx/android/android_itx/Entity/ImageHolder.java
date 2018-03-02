package com.itx.android.android_itx.Entity;

import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by aladhims on 03/03/18.
 */

public class ImageHolder {

    private Uri mUri;
    private Image mImage;

    public ImageHolder(){}

    public ImageHolder(@Nullable Image img, @Nullable Uri uri){
        mImage = img;
        mUri = uri;
    }

    public Uri getmUri() {
        return mUri;
    }

    public Image getmImage() {
        return mImage;
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    public void setmImage(Image mImage) {
        this.mImage = mImage;
    }


}
