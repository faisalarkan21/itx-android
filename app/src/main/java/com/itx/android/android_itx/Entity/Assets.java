package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by faisal on 2/21/18.
 */

public class Assets {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("assetCategory")
    @Expose
    private String assetCategory;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name")
    @Expose
    private String fullName;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("npwp")
    @Expose
    private String npwp;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("inventories")
    @Expose
    private String inventories;
    @SerializedName("inventoryCategories")
    @Expose
    private String inventoryCategories;
    @SerializedName("images")
    @Expose
    private String images;
    @SerializedName("accountHolder")
    @Expose
    private String accountHolder;
    @SerializedName("isVerified")
    @Expose
    private String isVerified;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("deletedAt")
    @Expose
    private String deletedAt;

    public Assets(String name, String assetCategory, float rating){

        this.name = name;
        this.assetCategory = assetCategory;
        this.rating = rating;

    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(String assetCategory) {
        this.assetCategory = assetCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getInventories() {
        return inventories;
    }

    public void setInventories(String inventories) {
        this.inventories = inventories;
    }

    public String getInventoryCategories() {
        return inventoryCategories;
    }

    public void setInventoryCategories(String inventoryCategories) {
        this.inventoryCategories = inventoryCategories;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}
