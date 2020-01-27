package me.buddyoruna.appinspeccion.domain.entity;

import androidx.annotation.Keep;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.File;

@Keep
@IgnoreExtraProperties
public class FileInspeccion {

    private boolean isUpload;

    private String key;
    private File file;
    private String pathUpload;

    public FileInspeccion(File mFile) {
        this.file = mFile;
        this.isUpload = false;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public java.lang.String getPathUpload() {
        return pathUpload;
    }

    public void setPathUpload(java.lang.String pathUpload) {
        this.pathUpload = pathUpload;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
