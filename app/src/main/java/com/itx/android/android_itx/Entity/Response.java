package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by legion on 11/04/2018.
 */

public class Response {

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
            @SerializedName("role")
            @Expose
            private String role;

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
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

}
