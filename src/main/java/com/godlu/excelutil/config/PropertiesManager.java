package com.godlu.excelutil.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by GodLu on 2020/3/23 21:33
 **/

public class PropertiesManager {
    private static Properties readProp = new Properties();
    private static Properties writeProp = new Properties();
    private static InputStream in = null;
    private static FileOutputStream oFile = null;
    private static String filePath = null;

    //设置路径
    public static void setPath(String path){
        filePath = path;
    }

    //获取属性
    public static Object getProperty(String key){
        Object object = null;
        if (in == null) {
            try {
                in = PropertiesManager.class.getClassLoader().getResourceAsStream(filePath);
                //in = new FileInputStream(PropertiesManager.class.getClassLoader().getResource(filePath).getPath());
                readProp.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        object = readProp.get(key);
        return object;
    }

    //写入属性
    public static void setProperty(String key,String value){
        try {
            System.out.println("传入的map为" + key + ":" + value);
            if (oFile == null) {
                oFile = new FileOutputStream(Objects.requireNonNull(PropertiesManager.class.getClassLoader().getResource(filePath)).getPath(),false);
            }
            writeProp.setProperty(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeStream(){
        if (in != null){
            try {
                System.out.println("关闭输入流");
                readProp = new Properties();
                in.close();
                in = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (oFile != null) {
            try {
                System.out.println("关闭输出流");
                writeProp.store(oFile, null);
                oFile.flush();
                writeProp = new Properties();
                oFile.close();
                oFile = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
