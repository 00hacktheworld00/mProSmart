package com.example.sadashivsinha.mprosmart.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by saDashiv sinha on 24-May-16.
 */
public class GetCurrentDate {

    public String getDate()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());

        return  strDate;
    }

}
