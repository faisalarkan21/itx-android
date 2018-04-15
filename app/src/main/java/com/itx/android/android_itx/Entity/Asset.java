package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faisal on 2/21/18.
 */

public class Asset {

    @SerializedName("inventoryCategories")
    @Expose
    private List<InventoryCategory> inventoryCategories = null;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("isVerified")
    @Expose
    private Boolean isVerified;
    @SerializedName("isPublished")
    @Expose
    private Boolean isPublished;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("deletedAt")
    @Expose
    private Object deletedAt;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("assetCategory")
    @Expose
    private AssetCategory assetCategory;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("npwp")
    @Expose
    private String npwp;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public List<InventoryCategory> getInventoryCategories() {
        return inventoryCategories;
    }

    public void setInventoryCategories(List<InventoryCategory> inventoryCategories) {
        this.inventoryCategories = inventoryCategories;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AssetCategory getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}
