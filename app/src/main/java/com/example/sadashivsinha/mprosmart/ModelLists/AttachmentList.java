package com.example.sadashivsinha.mprosmart.ModelLists;

import android.graphics.drawable.Drawable;

/**
 * Created by saDashiv sinha on 11-Mar-16.
 */
public class AttachmentList
{
    private String file_name, imageUrl;
    private Drawable file_preview;
    private Boolean isViewOnly;

    public AttachmentList(Boolean isViewOnly, String file_name,Drawable file_preview) {

        this.file_name = file_name;
        this.file_preview = file_preview;
        this.isViewOnly = isViewOnly;
    }

    public AttachmentList(Boolean isViewOnly, String file_name,String imageUrl) {

        this.file_name = file_name;
        this.imageUrl = imageUrl;
        this.isViewOnly = isViewOnly;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getViewOnly() {
        return isViewOnly;
    }

    public Drawable getFile_preview() {
            return file_preview;
        }

        public String getFile_name() {
        return file_name;
    }

}