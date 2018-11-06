package com.android.launcher3.device;

import android.content.Context;


import com.android.launcher3.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lizeiwei on 2018/7/31.
 */

public class ResourceUtil {
    public static String getRawResourceString(Context context, String fileName, String pkgName) {
        int treeId = context.getResources().getIdentifier(fileName.toLowerCase(), "raw", pkgName);
        InputStream inputStream = context.getResources().openRawResource(treeId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String lineStr;
        StringBuilder result = new StringBuilder();
        try {
            while ((lineStr = bufferedReader.readLine()) != null) {
                result.append(lineStr);
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
