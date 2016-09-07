package com.example.sadashivsinha.mprosmart.ModelLists;

import android.graphics.drawable.Drawable;

/**
 * Created by saDashiv sinha on 11-Mar-16.
 */
public class AttachmentList
{
        private String file_name;
        private Drawable file_preview;

        public AttachmentList() {
        }

        public AttachmentList(String file_name,Drawable file_preview) {

            this.file_name = file_name;
            this.file_preview = file_preview;
        }

        public Drawable getFile_preview() {
            return file_preview;
        }

        public String getFile_name() {
        return file_name;
    }

}