package com.godlu.excelutil.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.godlu.excelutil.config.GlobalParam;
import com.godlu.excelutil.entity.SystemGoods;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Objects;

/**
 * Created by GodLu on 2020/3/30 8:43
 **/

public class FileUtil {
    private static FileUtil fileUtil;//静态FileUtil实例

    public static FileUtil getInstance(){
        if (fileUtil == null){
            fileUtil = new FileUtil();
        }
        return fileUtil;
    }

    /**
     * @Author GodLu
     * @Description //TODO 获取exe绝对路径
     * @Date 2020/4/3 11:02
     * @return java.lang.String
     **/
    public static String getExePath() {
        return new File("").getAbsolutePath();
    }

    /**
     * @Author GodLu
     * @Description //TODO 复制指定文件到指定目录
     * @Date 2020/4/13 12:50
     * @param oldFilePath 要复制的文件
     * @param newDirPath 指定的目录
     * @return void
     **/
    public boolean copyFileToDir(String oldFilePath, String newDirPath){
        String newPath = newDirPath + File.separator + oldFilePath.substring(oldFilePath.lastIndexOf("\\") + 1);
        if (new File(oldFilePath).isFile()) {
            return copyFile(oldFilePath, newPath);
        }else{
            return copyDirContentToDir(oldFilePath,newPath);
        }
    }

    /**
     * @Author GodLu
     * @Description //TODO 复制文件夹内容到指定文件夹
     * @Date 2020/4/22 19:18
     * @param oldDir 要复制内容的父文件夹
     * @param newDir 指定文件夹
     * @return boolean
     **/
    private boolean copyDirContentToDir(String oldDir, String newDir){
        File oldFile = new File(oldDir);
        if (!oldFile.isDirectory()){
            LogUtil.error("传入对象不是文件夹：" + oldDir);
            return false;
        }
        for (File file : Objects.requireNonNull(oldFile.listFiles())) {//遍历
            String newPath = newDir + File.separator + file.getPath().substring(file.getPath().lastIndexOf(File.separator) + 1);
            System.out.println("newPath:" + newPath);
            if (file.isDirectory()){//如果是文件夹
                if (!createNewDirectory(newPath)) {//在新文件夹下创建文件夹失败
                    return false;
                }
                if (!copyDirContentToDir(file.getPath(),newPath)) {//递归调用
                    return false;
                }
            }else {//如果不是文件夹
                if (!copyFile(file.getPath(),newPath)) {//复制文件
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @Author GodLu
     * @Description //TODO 复制文件
     * @Date 2020/4/3 11:19
     * @param oldPath 原文件路径
     * @param newPath 指定路径
     * @return void
     **/
    public boolean copyFile(String oldPath, String newPath) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            File fileDir = new File(newPath).getParentFile();//获取上一级文件夹
            if (!fileDir.exists()){//如果文件夹不存在
                fileDir.mkdirs();//创建文件夹
                LogUtil.info("上一级文件夹不存在，创建文件夹：" + fileDir);
            }
            //复制文件到新文件夹下
            inputChannel = new FileInputStream(new File(oldPath)).getChannel();
            outputChannel = new FileOutputStream(new File(newPath)).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            LogUtil.info("复制文件" + oldPath + "到" + newPath + "成功");
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("复制文件" + oldPath + "到" + newPath + "失败：" + e.getMessage());
            return false;
        }finally {
            //关闭输入输出流
            try {
                assert inputChannel != null;
                inputChannel.close();
                assert outputChannel != null;
                outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.error("关闭输入输出流失败：" + e.getMessage());
            }
        }
    }

    public String getSystemDataStr(){
        return readStrFromFile(GlobalParam.STORE_DATA_LIST_JSON_PATH);
    }
    public void saveSystemDataStr(List<SystemGoods> list, WriteFileListener listener){
        JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(list));
        System.out.println("保存库内数据：" + array.toJSONString());
        writeContentToFile(array.toJSONString(),GlobalParam.STORE_DATA_LIST_JSON_PATH,listener);
    }
    public String getShotDataStr(){
        return readStrFromFile(GlobalParam.SHORT_DATA_LIST_JSON_PATH);
    }
    public void saveShotDataStr(List<String> list, WriteFileListener listener){
        JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(list));
        System.out.println("保存简称数据：" + array.toJSONString());
        writeContentToFile(array.toJSONString(),GlobalParam.SHORT_DATA_LIST_JSON_PATH,listener);
    }

    /**
    * @Author: GodLu
    * @Date: 2024/2/26 0:30
    * @Description: 从指定文件读取字符
    * @param filePath 文件路径
    * @return: String
    */
    public String readStrFromFile(String filePath){
        File file = new File(filePath);
        if (!file.exists()) //如果目标文件不存在
            return null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    /**
     * @Author GodLu
     * @Description //将指定内容写出到指定文件
     * @Date 2020/4/17 8:44
     * @param content 要写出的内容
     * @param filePath 要写出到的文件路径
     **/
    public void writeContentToFile(String content, String filePath, WriteFileListener listener){
        //规范文件名称
        if (filePath.toCharArray()[1] == ':') {
            filePath = filePath.split(":\\\\")[0] + ":\\" + filePath.split(":\\\\")[1]
                    .replaceAll("[/:*?\"<>|]", "");
        }
        File file = new File(filePath);
        if (!file.exists()){//如果目标文件不存在
            if (!file.getParentFile().exists()){//如果文件的父文件夹不存在
                if (!file.getParentFile().mkdirs()){//就创建父文件夹
                    listener.onFail("传入的文件创建父文件夹失败：" + file.getParentFile().getPath());
                    LogUtil.error("传入的文件创建父文件夹失败：" + file.getParentFile().getPath());
                }
            }else if (!createNewFile(filePath)){//如果创建文件失败
                listener.onFail("创建文件失败：" + filePath);
                LogUtil.error("创建文件失败：" + filePath);
            }
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(content);
            listener.onSuccess();
            LogUtil.info("写出文件成功：" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info(filePath + "写出文件失败：" + e.getMessage());
            listener.onFail(filePath + "写出文件失败：" + e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }
    public interface WriteFileListener{
        void onSuccess();
        void onFail(String msg);
    }
    /**
     * @Author GodLu
     * @Description //TODO 删除文件，可以是文件或文件夹
     * @Date 2020/4/7 11:55
     * @param file 要删除的文件名
     * @return boolean 删除成功返回true，否则返回false
     **/
    public boolean deleteFileOrDirectory(File file){
        if (!file.exists()) {
            LogUtil.error("删除文件失败:" + file.getPath() + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(file.getPath());
            else
                return deleteDirectory(file.getPath());
        }
    }
    /**
     * @Author GodLu
     * @Description //TODO 删除单个文件
     * @Date 2020/4/7 11:57
     * @param fileName 要删除的文件的文件名
     * @return boolean 单个文件删除成功返回true，否则返回false
     **/
    private boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                LogUtil.error("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            LogUtil.error("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
    /**
     * @Author GodLu
     * @Description //TODO 删除目录及目录下的文件
     * @Date 2020/4/7 11:59
     * @param dir 要删除的目录的文件路径
     * @return boolean 目录删除成功返回true，否则返回false
     **/
    private boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            LogUtil.error("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            LogUtil.error("删除目录失败！");
            return false;
        }
        // 删除当前目录
        return dirFile.delete();
    }

    /**
     * @Author GodLu
     * @Description //TODO 创建新的文件
     * @Date 2020/4/7 11:35
     * @param filePath 新建文件路径
     * @return void
     **/
    public boolean createNewFile(String filePath){
        //规范文件名称
        if (filePath.toCharArray()[1] == ':') {
            filePath = filePath.split(":\\\\")[0] + ":\\" + filePath.split(":\\\\")[1]
                    .replaceAll("[/:*?\"<>|]", "");
        }
        File file = new File(filePath);
        if (file.exists()){//如果文件已经存在
            if (!deleteFileOrDirectory(file)) {//删除文件
                LogUtil.error("删除原来的文件或文件夹失败" + file);
                return false;
            }
            LogUtil.debug("删除原来的文件或文件夹" + file);
        }
        try {
            if (!file.createNewFile()) {//创建文件
                LogUtil.error("新建文件失败：" + file.getPath());
                return false;
            }
        } catch (IOException e) {
            LogUtil.error("创建" + file + "文件失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        LogUtil.debug("新建文件" + file);
        return true;
    }

    /**
     * @Author GodLu
     * @Description //TODO 创建新的文件夹
     * @Date 2020/4/7 11:35
     * @param filePath 新建文件夹路径
     * @return void
     **/
    public boolean createNewDirectory(String filePath){
        //规范文件名称
        if (filePath.toCharArray()[1] == ':') {
            filePath = filePath.split(":\\\\")[0] + ":\\" + filePath.split(":\\\\")[1]
                    .replaceAll("[/:*?\"<>|]", "");
        }
        File file = new File(filePath);
        if (file.exists()){//如果文件或文件夹已经存在
            if (!deleteFileOrDirectory(file)) {//删除文件或文件夹
                LogUtil.error("删除原来的文件或文件夹失败：" + file);
                return false;
            }
            LogUtil.debug("删除原来的文件或文件夹：" + file);
        }
        if (!file.mkdirs()){//创建文件夹
            LogUtil.error("新建文件夹失败：" + file.getPath());
            return false;
        }
        LogUtil.debug("新建文件夹：" + file);
        return true;
    }

    /**
     * @Author GodLu
     * @Description 选择文件
     * @Date 2020/4/17 8:53
     * @param window 打开文件选择器的窗体
     * @param fileTypes 文件类型
     * @return java.io.File
     **/
    public File selectFile(Window window,String... fileTypes){
        FileChooser fileChooser = new FileChooser();
        //添加扩展名过滤器，过滤文件
        //注意，这个过滤器添加得在showOpenDialog方法之前，不然会没有效果，这里注意需要有个 * 号
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("文件类型", fileTypes));
        //选择对话框的标题
        fileChooser.setTitle("选择文件");
        //showOpenDialog之后会返回获得的文件
        return fileChooser.showOpenDialog(window);
    }
}
