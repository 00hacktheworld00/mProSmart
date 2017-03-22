package com.example.sadashivsinha.mprosmart.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by saDashiv sinha on 17-Oct-16.
 */

public class UberBoldFont extends TextView {

    public UberBoldFont(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/UberFontBold.ttf"));
    }
}