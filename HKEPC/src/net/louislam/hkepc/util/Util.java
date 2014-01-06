package net.louislam.hkepc.util;

import android.util.Log;

public class Util {
    static final String TAG = "Util";

    public static int convertInt(String s){
        int i = -1;
        try{
            i = Integer.valueOf(s);
        }
        catch(Exception e){
            Log.d(TAG, "Error in getting pageNo: " + e.getMessage());
        }

        return i;
    }
}
