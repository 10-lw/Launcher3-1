package com.android.launcher3.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.device.DeviceConfig;

import java.util.List;

public class DesktopItemsHelper {
    private static AllAppsList mBgAllAppsList;
    private static ModelWriter mWriter;
    private static int numColumns;
    private static int numRows;
    private static int numHotseatIcons;
    private static Context mContext;
    private static ContentResolver cr;

    public static void addScreenAndAddItem(Context context, ModelWriter writer, int appNum, AllAppsList bgAllAppsList) {
        mBgAllAppsList = bgAllAppsList;
        mWriter = writer;
        mContext = context;

        InvariantDeviceProfile deviceProfile = new InvariantDeviceProfile(context);
        numColumns = deviceProfile.numColumns;
        numRows = deviceProfile.numRows;
        numHotseatIcons = deviceProfile.numHotseatIcons;

        cr = context.getContentResolver();

        writer.deleteAllTable();
        //添加到各屏
        addItemsToScreens();
        SystemClock.sleep(4);
        //添加到hotseat
        initHotseatItems();

    }

    private static void initHotseatItems() {
        DeviceConfig instance = DeviceConfig.getInstance(mContext);
        List<String> bottomAppsConfigs = instance.getBottomAppsConfigs();
        int size = mBgAllAppsList.size();
        int x = 0;
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = mBgAllAppsList.get(i);
            ShortcutInfo shortcutInfo = new ShortcutInfo(appInfo);
            String packageName = appInfo.componentName.getPackageName();
            if (bottomAppsConfigs.contains(packageName)) {
                mWriter.addItemToDatabase(shortcutInfo, LauncherSettings.Favorites.CONTAINER_HOTSEAT, 0, x, 0);
                x++;
            }
        }

    }

    private static void addItemsToScreens() {
        DeviceConfig instance = DeviceConfig.getInstance(mContext);
        List<String> bottomAppsConfigs = instance.getBottomAppsConfigs();
        int screemSize = numColumns * numRows;
        int total = mBgAllAppsList.size();

        int count = 0;
        int y = 1;

        // insert screen db
        Uri uri = LauncherSettings.WorkspaceScreens.CONTENT_URI;

        //insert shortcut info db
        for (int i = 0; i < total; i++) {
            AppInfo info = mBgAllAppsList.get(i);
            ShortcutInfo shortcutInfo = new ShortcutInfo(info);
            String packageName = info.componentName.getPackageName();
            boolean inHotseat = bottomAppsConfigs.contains(packageName);


            if (inHotseat) {
                continue;
            }

            int firstScreenSize = (numRows - 2) * numColumns;
            int screenNum = 0;
            if (count < firstScreenSize) {
                int cellX = count % numColumns;
                if (count % numColumns == 0) {
                    y++;
                }
                mWriter.addItemToDatabase(shortcutInfo, LauncherSettings.Favorites.CONTAINER_DESKTOP, 0, cellX, y);
            } else {
                if ((count - firstScreenSize) % screemSize == 0) {
                    screenNum++;
                    ContentValues v = new ContentValues();
                    v.put(LauncherSettings.WorkspaceScreens._ID, screenNum);
                    v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, screenNum);
                    cr.insert(uri, v);
                }
                mWriter.addItemToDatabase(shortcutInfo, -100, screenNum, count % numColumns, y);
            }
            count++;
        }
    }

}
