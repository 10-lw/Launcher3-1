package com.android.launcher3.device;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviceConfig {
    private final String BOTTOM_CONFIG_APPS_PKG = "confirm_apps_on_launcher_bottom";
    private final String APPS_DONOT_SHOW = "do_not_show_apps";
    private Context mContext;
    private static DeviceConfig configs;
    private JSONObject parse;

    private DeviceConfig(Context mContext) {
        this.mContext = mContext;
        String config = ResourceUtil.getRawResourceString(mContext, "rk3288_config", mContext.getPackageName());
        parse = (JSONObject) JSON.parse(config);
    }

    public static DeviceConfig getInstance(Context context) {
        if (configs == null) {
            configs = new  DeviceConfig(context);
        }
        return configs;
    }

    public List<String> getBottomAppsConfigs() {
          String string = parse.getString(BOTTOM_CONFIG_APPS_PKG);
        JSONArray jsonArray = JSON.parseArray(string);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.get(i).toString());
        }
        return list;
    }

    public List<String> getExcludeAppsConfigs() {
        List<String> excludeApps = getBottomAppsConfigs();
        String string = parse.getString(APPS_DONOT_SHOW);
        JSONArray jsonArray = JSON.parseArray(string);
        for (int i = 0; i < jsonArray.size(); i++) {
            excludeApps.add(jsonArray.get(i).toString());
        }
        return excludeApps;
    }
}
