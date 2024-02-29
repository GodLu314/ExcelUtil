package com.godlu.excelutil.config;


import com.godlu.excelutil.utils.FileUtil;

import java.io.File;

/**
 * Created by GodLu on 2020/4/19 21:25
 **/

public class GlobalParam {
    public static final String CONFIG_DIR = FileUtil.getExePath() + File.separator + "config";
    public static final String STORE_DATA_LIST_JSON_PATH = CONFIG_DIR + File.separator + "system_name_list.json";
    public static final String SHORT_DATA_LIST_JSON_PATH = CONFIG_DIR + File.separator + "short_name_list.json";
}
