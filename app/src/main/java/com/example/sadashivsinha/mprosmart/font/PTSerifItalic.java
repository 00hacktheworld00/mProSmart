package com.example.sadashivsinha.mprosmart.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by saDashiv sinha on 23-Feb-16.
 */
public class PTSerifItalic  extends TextView {

    public PTSerifItalic(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Pt-Serif_Italic.ttf"));
    }
}

