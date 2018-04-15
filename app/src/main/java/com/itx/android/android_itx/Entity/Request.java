package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by legion on 11/04/2018.
 */

public class Request {

    /*REQUEST USER*/

    public class Login {

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

    public class CreateUser {

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

    public class UpdateUser{
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

    /*REQUEST ASSET*/

    public class CreateAsset {

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

    public class UpdateAsset{

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

    /*REQUEST INVENTORY*/

    public class CreateInventory{
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

    public class UpdateInventory{
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
}
