package com.shzlabs.statussaver.data.local;

import android.os.Environment;
import android.util.Log;

import com.shzlabs.statussaver.data.model.ImageModel;
import com.shzlabs.statussaver.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by shaz on 5/3/17.
 */

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();
    private static final String STATUS_SAVER_LOCAL_DIR_URI = "/StatusSaver";
    private static final String WHATSAPP_STATUS_DIR_URI = "/WhatsApp/Media/.Statuses";

    @Inject
    public FileHelper() {
    }

    private String getStatusSaverDirPath() {
        return Environment.getExternalStorageDirectory().toString()+ STATUS_SAVER_LOCAL_DIR_URI;
    }

    private String getWhatsappStatusDirPath() {
        return Environment.getExternalStorageDirectory().toString()+ WHATSAPP_STATUS_DIR_URI;
    }

    public List<ImageModel> getRecentImages() {
        List<ImageModel> images = new ArrayList<>();
        if (isWhatsappDirAvailable()) {
            File directory = new File(getWhatsappStatusDirPath());
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                ImageModel imageModel = new ImageModel(
                        files[i].getName(),
                        files[i].getAbsolutePath(),
                        files[i].lastModified());
                images.add(imageModel);
            }
        }
        return sortMediaByLastCreated(images);
    }

    public List<ImageModel> getSavedImages() {

        List<ImageModel> images = new ArrayList<>();
        if (isStatusDirAvailable()) {
            File directory = new File(getStatusSaverDirPath());
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                ImageModel imageModel = new ImageModel(
                        files[i].getName(),
                        files[i].getAbsolutePath(),
                        files[i].lastModified());
                images.add(imageModel);
            }
        }
        return sortMediaByLastCreated(images);
    }

    private boolean isStatusDirAvailable() {
        String absolutePath = getStatusSaverDirPath();
        File fPath = new File(absolutePath);
        if (fPath.exists()) {
            return true;
        }else{
            return false;
        }
    }

    private boolean isWhatsappDirAvailable() {
        String absolutePath = getWhatsappStatusDirPath();
        File fPath = new File(absolutePath);
        if (fPath.exists()) {
            return true;
        }else{
            return false;
        }
    }

    private boolean setupStatusSaverDirectory() {
        String absolutePath = getStatusSaverDirPath();
        File fPath = new File(absolutePath);
        if (!fPath.exists()) {
            if (!fPath.mkdir()) {
                Log.d(TAG, "setupStatusSaverDirectory: Error creating directory");
                return false;
            }
        }
        return true;
    }

    public boolean saveMediaToLocalDir(ImageModel imageModel) {

        if (!setupStatusSaverDirectory()) {
            return false;
        }

        String absolutePath = imageModel.getCompletePath();
        String fileName = imageModel.getFileName();

        File sourcePath = new File(absolutePath);
        File destPath = new File(getStatusSaverDirPath(), fileName);

        try {
            FileUtil.copyFile(sourcePath, destPath);
        } catch (IOException e) {
            Log.e(TAG, "saveMediaToLocalDir: Error copying files.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private List<ImageModel> sortMediaByLastCreated(List<ImageModel> imageModels) {
        Collections.sort(imageModels, new Comparator<ImageModel>() {
            @Override
            public int compare(ImageModel o1, ImageModel o2) {
                long o1Value = o1.getLastModified();
                long o2Value = o2.getLastModified();
                return o1Value > o2Value ? -1 : (o1Value < o2Value) ? 1: 0;
            }
        });
        return imageModels;
    }
}