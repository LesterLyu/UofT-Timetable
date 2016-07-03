package com.lvds2000.utsccsuntility;

import android.content.Context;
import android.content.Intent;

import com.lvds2000.utsccsuntility.A08.A08_Ann;
import com.lvds2000.utsccsuntility.A08.A08_Exercise;
import com.lvds2000.utsccsuntility.A08.A08_Lec;
import com.lvds2000.utsccsuntility.A08.A08_Pra;
import com.lvds2000.utsccsuntility.A08.A08_Test;
import com.lvds2000.utsccsuntility.A08.A08_Tut;

/**
 * Created by LV on 2015-10-31.
 */
public class IntentOpener {

    public static void csca08_exe(Context context) {
        Intent intent = new Intent(context, A08_Exercise.class);
        context.startActivity(intent);

    }
    public static void csca08_ann(Context context) {
        Intent intent = new Intent(context, A08_Ann.class);
        context.startActivity(intent);

    }
    public static void csca08_lec(Context context) {
        Intent intent = new Intent(context, A08_Lec.class);
        context.startActivity(intent);

    }
    public static void csca08_pra(Context context) {
        Intent intent = new Intent(context, A08_Pra.class);
        context.startActivity(intent);
    }
    public static void csca08_tut(Context context) {
        Intent intent = new Intent(context, A08_Tut.class);
        context.startActivity(intent);

    }
    public static void csca08_test(Context context) {
        Intent intent = new Intent(context, A08_Test.class);
        context.startActivity(intent);

    }
}
