package com.itx.android.android_itx.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by faisal on 2/19/18.
 */

public class Users {

    @SerializedName("idUser")
    @Expose
    private String idUser;
 
    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("fullname")
    @Expose
    private String fullName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("ktp")
    @Expose
    private String ktp;

    @SerializedName("email")
    @Expose
    private String email;
 
    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("socials")
    @Expose
    private String socials;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("assets")
    @Expose
    private String assets;

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("isVerified")
    @Expose
    private boolean isVerified;

    @SerializedName("updatedAt")
    @Expose
    private Date updatedAt;

    @SerializedName("createdAt")
    @Expose
    private Date createdAt;    

    @SerializedName("deletedAt")   
    @Expose
    private Date deletedAt;

    @SerializedName("token")
    @Expose
    private String token;

    private String role;


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getKtp() {
        return ktp;
    }

    public void setKtp(String ktp) {
        this.ktp = ktp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSocials() {
        return socials;
    }

    public void setSocials(String socials) {
        this.socials = socials;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

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
