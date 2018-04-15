package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by legion on 11/04/2018.
 */

public class Response {

    /*RESPON UPLOAD*/

    public class UploadResponse {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("image")
            @Expose
            private List<Image> image = null;

            public List<Image> getImage() {
                return image;
            }

            public void setImage(List<Image> image) {
                this.image = image;
            }

        }

    }

    /*RESPON USER*/

    public class Login {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("token")
            @Expose
            private String token;
            @SerializedName("user")
            @Expose
            private User user;

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }
        }
    }

    public class GetUser {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("user")
            @Expose
            private User user;

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }

        }

    }

    public class GetAllUser {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("user")
            @Expose
            private List<User> user = null;
            @SerializedName("total")
            @Expose
            private Integer total;

            public List<User> getUser() {
                return user;
            }

            public void setUser(List<User> user) {
                this.user = user;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

        }

    }

    public class CreateUser {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("user")
            @Expose
            private User user;

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }

        }

    }

    public class UpdateUser {

        @SerializedName("status")
        @Expose
        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    public class DeleteUser {

        @SerializedName("status")
        @Expose
        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

    }



    /*RESPON ASSET*/

    public class CreateAsset {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("asset")
            @Expose
            private Asset asset;

            public Asset getAsset() {
                return asset;
            }

            public void setAsset(Asset asset) {
                this.asset = asset;
            }

        }

        public class Asset {

            @SerializedName("inventoryCategories")
            @Expose
            private List<Object> inventoryCategories = null;
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
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("brand")
            @Expose
            private String brand;
            @SerializedName("assetCategory")
            @Expose
            private String assetCategory;
            @SerializedName("user")
            @Expose
            private String user;
            @SerializedName("npwp")
            @Expose
            private String npwp;
            @SerializedName("phone")
            @Expose
            private String phone;
            @SerializedName("rating")
            @Expose
            private Integer rating;
            @SerializedName("address")
            @Expose
            private Address address;
            @SerializedName("__v")
            @Expose
            private Integer v;

            public List<Object> getInventoryCategories() {
                return inventoryCategories;
            }

            public void setInventoryCategories(List<Object> inventoryCategories) {
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

            public String getAssetCategory() {
                return assetCategory;
            }

            public void setAssetCategory(String assetCategory) {
                this.assetCategory = assetCategory;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
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

    }

    public class GetAsset {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("asset")
            @Expose
            private List<Asset> asset = null;

            @SerializedName("total")
            @Expose
            private Integer total;


            public List<Asset> getAsset() {
                return asset;
            }

            public void setAsset(List<Asset> asset) {
                this.asset = asset;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

        }

    }

    public class UpdateAsset {
        @SerializedName("status")
        @Expose
        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    public class DeleteAsset {
        @SerializedName("status")
        @Expose
        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    public class GetAssetCategory {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("assetCategory")
            @Expose
            private List<AssetCategory> assetCategory = null;
            @SerializedName("total")
            @Expose
            private Integer total;

            public List<AssetCategory> getAssetCategory() {
                return assetCategory;
            }

            public void setAssetCategory(List<AssetCategory> assetCategory) {
                this.assetCategory = assetCategory;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

        }

    }

    /*RESPON INVENTORY*/

    public class GetInventory {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("inventoryCategory")
            @Expose
            private List<InventoryCategory> inventoryCategory = null;
            @SerializedName("total")
            @Expose
            private Integer total;

            public List<InventoryCategory> getInventoryCategory() {
                return inventoryCategory;
            }

            public void setInventoryCategory(List<InventoryCategory> inventoryCategory) {
                this.inventoryCategory = inventoryCategory;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

        }

    }

    public class GetFacillity {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("facility")
            @Expose
            private List<Facility> facility = null;
            @SerializedName("total")
            @Expose
            private Integer total;

            public List<Facility> getFacility() {
                return facility;
            }

            public void setFacility(List<Facility> facility) {
                this.facility = facility;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

        }

    }

    public class CreateInventory {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("status")
        @Expose
        private Status status;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public class Data {

            @SerializedName("inventoryCategory")
            @Expose
            private InventoryCategory inventoryCategory;

            public InventoryCategory getInventoryCategory() {
                return inventoryCategory;
            }

            public void setInventoryCategory(InventoryCategory inventoryCategory) {
                this.inventoryCategory = inventoryCategory;
            }

        }

        public class InventoryCategory {

            @SerializedName("facilities")
            @Expose
            private List<String> facilities = null;
            @SerializedName("images")
            @Expose
            private List<Image> images = null;
            @SerializedName("inventories")
            @Expose
            private List<Object> inventories = null;
            @SerializedName("isVerified")
            @Expose
            private Boolean isVerified;
            @SerializedName("createdAt")
            @Expose
            private String createdAt;
            @SerializedName("deletedAt")
            @Expose
            private Object deletedAt;
            @SerializedName("_id")
            @Expose
            private String id;
            @SerializedName("asset")
            @Expose
            private String asset;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("description")
            @Expose
            private String description;
            @SerializedName("space")
            @Expose
            private Integer space;
            @SerializedName("stock")
            @Expose
            private Integer stock;
            @SerializedName("price")
            @Expose
            private Integer price;
            @SerializedName("__v")
            @Expose
            private Integer v;

            public List<String> getFacilities() {
                return facilities;
            }

            public void setFacilities(List<String> facilities) {
                this.facilities = facilities;
            }

            public List<Image> getImages() {
                return images;
            }

            public void setImages(List<Image> images) {
                this.images = images;
            }

            public List<Object> getInventories() {
                return inventories;
            }

            public void setInventories(List<Object> inventories) {
                this.inventories = inventories;
            }

            public Boolean getIsVerified() {
                return isVerified;
            }

            public void setIsVerified(Boolean isVerified) {
                this.isVerified = isVerified;
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

            public String getAsset() {
                return asset;
            }

            public void setAsset(String asset) {
                this.asset = asset;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public Integer getSpace() {
                return space;
            }

            public void setSpace(Integer space) {
                this.space = space;
            }

            public Integer getStock() {
                return stock;
            }

            public void setStock(Integer stock) {
                this.stock = stock;
            }

            public Integer getPrice() {
                return price;
            }

            public void setPrice(Integer price) {
                this.price = price;
            }

            public Integer getV() {
                return v;
            }

            public void setV(Integer v) {
                this.v = v;
            }

        }

    }

    public class UpdateInventory {
        @SerializedName("status")
        @Expose
        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    public class DeleteInventory {
        @SerializedName("status")
        @Expose
        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }


}
