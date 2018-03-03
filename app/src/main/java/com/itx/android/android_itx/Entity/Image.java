package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by aladhims on 03/03/18.
 */

public class Image {

    @SerializedName("alt")
    @Expose
    private String mAlt;
    @SerializedName("fullsize")
    @Expose
    private String mFullsize;
    @SerializedName("medium")
    @Expose
    private String mMedium;
    @SerializedName("large")
    @Expose
    private String mLarge;
    @SerializedName("thumbnail")
    @Expose
    private String mThumbnail;
    @SerializedName("updatedAt")
    @Expose
    private Date mUpdatedAt;
    @SerializedName("createdAt")
    @Expose
    private Date mCreatedAt;
    @SerializedName("deletedAt")
    @Expose
    private Date mDeletedAt;

    public Image(){

    }

    public void setmAlt(String mAlt) {
        this.mAlt = mAlt;
    }

    public void setmFullsize(String mFullsize) {
        this.mFullsize = mFullsize;
    }

    public void setmMedium(String mMedium) {
        this.mMedium = mMedium;
    }

    public void setmLarge(String mLarge) {
        this.mLarge = mLarge;
    }

    public void setmThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public void setmUpdatedAt(Date mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }

    public void setmCreatedAt(Date mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public void setmDeletedAt(Date mDeletedAt) {
        this.mDeletedAt = mDeletedAt;
    }

    public String getmAlt() {
        return mAlt;
    }

    public String getmFullsize() {
        return mFullsize;
    }

    public String getmMedium() {
        return mMedium;
    }

    public String getmLarge() {
        return mLarge;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public Date getmUpdatedAt() {
        return mUpdatedAt;
    }

    public Date getmCreatedAt() {
        return mCreatedAt;
    }

    public Date getmDeletedAt() {
        return mDeletedAt;
    }


}
