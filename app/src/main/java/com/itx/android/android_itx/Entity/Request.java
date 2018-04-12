package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by legion on 11/04/2018.
 */

public class Request {

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
        @SerializedName("images")
        @Expose
        private List<Image> images = null;
        @SerializedName("location")
        @Expose
        private Location location;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

    }
}
