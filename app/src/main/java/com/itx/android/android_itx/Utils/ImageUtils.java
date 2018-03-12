package com.itx.android.android_itx.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.itx.android.android_itx.BuildConfig;
import com.itx.android.android_itx.CreateNewAsset;
import com.itx.android.android_itx.CreateNewInventory;
import com.itx.android.android_itx.CreateNewUser;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.UpdateAsset;
import com.itx.android.android_itx.UpdateInventory;
import com.itx.android.android_itx.UpdateUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by aladhims on 12/03/18.
 */

public class ImageUtils {

    public static void takeOnePhoto(final Activity activity,
                              final int CAMERA_REQUEST,
                              final int GALLERY_REQUEST,
                              final int RC_PERM_GALLERY,
                              final int RC_PERM_CAMERA) {
        //show dialog for user to choose between camera or gallery
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle("Pilih Foto");
        alertBuilder.setMessage("Ambil foto dari ?");
        alertBuilder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                takeOnePhotoWithPermission(activity,CAMERA_REQUEST, RC_PERM_CAMERA);
            }
        });
        alertBuilder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                takeOneImageGalleryWithPermission(activity,GALLERY_REQUEST,RC_PERM_GALLERY);
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static void takeMultiplePhotos(final Activity activity,
                                    final int CAMERA_REQUEST,
                                    final int GALLERY_REQUEST,
                                    final int RC_PERM_GALLERY,
                                    final int RC_PERM_CAMERA) {
        //show dialog for user to choose between camera or gallery
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle("Pilih Foto");
        alertBuilder.setMessage("Ambil foto dari ?");
        alertBuilder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                takeMultiplePhotosWithPermission(activity,CAMERA_REQUEST,RC_PERM_CAMERA);
            }
        });
        alertBuilder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                takeMultipleImagesGalleryWithPermission(activity,GALLERY_REQUEST,RC_PERM_GALLERY);
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private static void takeOneImageGalleryWithPermission(Activity activity,
                                                   int GALLERY_REQUEST,
                                                   int RC_PERM_GALLERY) {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            ImageUtils.takeOnePhotoFromGallery(activity, GALLERY_REQUEST);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(activity, "Izinkan aplikasi untuk akses storage",
                    RC_PERM_GALLERY, perms);
        }
    }

    public static void takeOnePhotoWithPermission(Activity activity,
                                            int CAMERA_REQUEST,
                                            int RC_PERM_CAMERA) {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            ImageUtils.takeOnePhotoFromCamera(activity, CAMERA_REQUEST);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(activity, "Izinkan aplikasi untuk akses kamera dan storage",
                    RC_PERM_CAMERA, perms);
        }
    }

    public static void takeMultiplePhotosWithPermission(Activity activity,
                                                  int CAMERA_REQUEST,
                                                  int RC_PERM_CAMERA) {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            takeMultiplePhotosFromCamera(activity,CAMERA_REQUEST);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(activity, "Izinkan aplikasi untuk akses kamera dan storage",
                    RC_PERM_CAMERA, perms);
        }
    }

    private static void takeMultipleImagesGalleryWithPermission(Activity activity,
                                                         int GALLERY_REQUEST,
                                                         int RC_PERM_GALLERY) {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            takeMultiplePhotosFromGallery(activity,GALLERY_REQUEST);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(activity, "Izinkan aplikasi untuk akses storage",
                    RC_PERM_GALLERY, perms);
        }
    }

    public static String getPath(Uri uri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        if (context.getClass() == CreateNewUser.class){
            CreateNewUser.mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        } else if(context.getClass() == UpdateUser.class){
            UpdateUser.mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        }
        return image;
    }

    public static void takeOnePhotoFromGallery(Activity activity, int GALLERY_REQUEST) {
        /*
            ambil foto dari gallery lalu hasilnya akan ada di onActivityResult
        */

        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryIntent.setType("image/jpeg");
        activity.startActivityForResult(Intent.createChooser(openGalleryIntent, "Select Picture"), GALLERY_REQUEST);
    }

    public static void takeOnePhotoFromCamera(Activity context,
                                              int CAMERA_REQUEST) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                if(context.getClass() == CreateNewUser.class) {
                    CreateNewUser.filePhoto = createImageFile(context);
                } else if(context.getClass() == UpdateUser.class){
                    UpdateUser.filePhoto = createImageFile(context);
                }

            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if(context.getClass() == CreateNewUser.class){
                if (CreateNewUser.filePhoto != null) {
                    CreateNewUser.photoURI = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            CreateNewUser.filePhoto);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, CreateNewUser.photoURI);
                    context.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } else if(context.getClass() == UpdateUser.class){
                if (UpdateUser.filePhoto != null) {
                    UpdateUser.photoURI = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            UpdateUser.filePhoto);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, UpdateUser.photoURI);
                    context.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }
        }
    }

    public static void takeMultiplePhotosFromGallery(Activity activity, int GALLERY_RC) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryIntent.setType("image/jpeg");
        openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(Intent.createChooser(openGalleryIntent, "Select Picture"), GALLERY_RC);
    }

    public static void takeMultiplePhotosFromCamera(Activity activity,
                                              int CAMERA_REQUEST) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            Class currentAct = activity.getClass();
            try {
                if (currentAct == CreateNewAsset.class){
                    CreateNewAsset.fileImages.add(createImageFile(activity));
                } else if (currentAct == UpdateAsset.class){
                    UpdateAsset.fileImages.add(createImageFile(activity));
                } else if (currentAct == CreateNewInventory.class){
                    CreateNewInventory.fileImages.add(createImageFile(activity));
                } else if (currentAct == UpdateInventory.class){
                    UpdateInventory.fileImages.add(createImageFile(activity));
                }

            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (currentAct == CreateNewAsset.class){
                if (CreateNewAsset.fileImages.get(CreateNewAsset.fileImages.size() - 1) != null) {
                    Uri uri = FileProvider.getUriForFile(activity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            CreateNewAsset.fileImages.get(CreateNewAsset.fileImages.size() - 1));
                    CreateNewAsset.imagePreviews.add(new ImageHolder(null, uri));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, CreateNewAsset.imagePreviews.get(
                            CreateNewAsset.imagePreviews.size() - 1).getmUri());
                    activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } else if (currentAct == UpdateAsset.class){
                if (UpdateAsset.fileImages.get(UpdateAsset.fileImages.size() - 1) != null) {
                    Uri uri = FileProvider.getUriForFile(activity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            UpdateAsset.fileImages.get(UpdateAsset.fileImages.size() - 1));
                    UpdateAsset.imagePreviews.add(new ImageHolder(null, uri));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, UpdateAsset.imagePreviews.get(
                            UpdateAsset.imagePreviews.size() - 1).getmUri());
                    activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } else if (currentAct == CreateNewInventory.class){
                if (CreateNewInventory.fileImages.get(CreateNewInventory.fileImages.size() - 1) != null) {
                    Uri uri = FileProvider.getUriForFile(activity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            CreateNewInventory.fileImages.get(CreateNewInventory.fileImages.size() - 1));
                    CreateNewInventory.imagePreviews.add(new ImageHolder(null, uri));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, CreateNewInventory.imagePreviews.get(
                            CreateNewInventory.imagePreviews.size() - 1).getmUri());
                    activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } else if (currentAct == UpdateInventory.class){
                if (UpdateInventory.fileImages.get(UpdateInventory.fileImages.size() - 1) != null) {
                    Uri uri = FileProvider.getUriForFile(activity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            UpdateInventory.fileImages.get(UpdateInventory.fileImages.size() - 1));
                    UpdateInventory.imagePreviews.add(new ImageHolder(null, uri));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, UpdateInventory.imagePreviews.get(
                            UpdateInventory.imagePreviews.size() - 1).getmUri());
                    activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }
        }
    }




}
