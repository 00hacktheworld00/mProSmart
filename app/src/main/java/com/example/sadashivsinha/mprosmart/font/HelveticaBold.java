package com.example.sadashivsinha.mprosmart.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by saDashiv sinha on 07-Jun-16.
 */
public class HelveticaBold extends TextView {

    public HelveticaBold(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-neue-bold.ttf"));
    }
}