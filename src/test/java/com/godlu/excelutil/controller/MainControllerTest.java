package com.godlu.excelutil.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.godlu.excelutil.entity.Goods;
import com.godlu.excelutil.entity.SystemGoods;
import com.godlu.excelutil.utils.FileUtil;
import com.godlu.excelutil.utils.LogUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: GodLu
 * @create: 2024-02-25 11:07
 * @description: TODO
 */
public class MainControllerTest {
    private static final String readExcelPath = "C:\\Users\\GodLu\\Desktop\\张集1月.xls";
    private static final String templateExcelPath = "C:\\Users\\GodLu\\Desktop\\work.xls";
    private static final String writeExcelPath = "C:\\Users\\GodLu\\Desktop\\WriteTest测试写.xls";

    @Test
    public void testReadExcel(){
        EasyExcel.read(readExcelPath, Goods.class,new PageReadListener<Goods>(vegetableList -> {
            for (Goods goods : vegetableList) {
                System.out.println("读取到一条数据：" + goods.toString());
            }
        })).sheet().doRead();
    }

    @Test
    public void testWriteExcel(){
        List<Goods> goodsList = new ArrayList<>();
        //读
        EasyExcel.read(readExcelPath, Goods.class,new PageReadListener<Goods>(list -> {
            for (Goods goods : list) {
                goodsList.add(goods);
                System.out.println("读取到一条数据：" + JSON.toJSONString(goods));
            }
        })).sheet().doRead();
        //写
        EasyExcel.write(writeExcelPath, Goods.class)
                //.withTemplate(templateExcelPath)
                .sheet("sheet1")
                .doWrite(() -> {
                    // 分页查询数据
                    return goodsList;
                });
    }

    @Test
    public void getStoreDataList(){
        String storeDataStr = FileUtil.getInstance().getSystemDataStr();
        JSONArray objects = JSON.parseArray(storeDataStr);
        for (Object object : objects) {
            System.out.println(object);
            System.out.println("=======================================================");
        }
    }

    @Test
    public void saveStoreNameListJson(){

    }

    @Test
    public void testGetJson(){
        String jsonPath = "C:\\Users\\GodLu\\Desktop\\temp.txt";
        List<SystemGoods> systemDataList = new ArrayList<>();
        String jsonStr = FileUtil.getInstance().readStrFromFile(jsonPath);
        if (!jsonStr.startsWith("[") || !jsonStr.endsWith("]")){
            System.out.println("json数据不合法，请在数据最外侧加上“[]”！");
            return;
        }
        JSONArray jsonArray = JSON.parseArray(jsonStr);
        for (Object obj : jsonArray) {
            JSONObject jsonObject = JSON.parseObject(obj.toString());
            //System.out.println("data节点字符串：");
            String data = jsonObject.getString("data");
            //System.out.println(data);
            JSONArray dataArray = JSON.parseArray(data);
            //System.out.println("data节点数组：" + dataArray.size());
            for (Object o : dataArray) {
                JSONObject json = JSON.parseObject(o.toString());
                String goodsName = json.getString("WPMC");
                String measureUnit = json.getString("JLDW");
                //System.out.println(goodsName + ":" + measureUnit);
                //遍历库内数据集合，数据存在则不保存
                boolean shouldSave = true;
                for (SystemGoods systemGoods : systemDataList) {
                    if (goodsName.equals(systemGoods.getName())){
                        System.out.println("数据已存在，不添加！" + goodsName);
                        shouldSave = false;
                    }
                }
                if (shouldSave) {
                    SystemGoods systemGoods = new SystemGoods(goodsName, measureUnit);
                    systemDataList.add(systemGoods);
                }
            }
        }
        FileUtil.getInstance().saveSystemDataStr(systemDataList, new FileUtil.WriteFileListener() {
            @Override
            public void onSuccess() {
                System.out.println("添加成功：" );
                for (SystemGoods systemGoods : systemDataList) {
                    System.out.println(systemGoods);
                }
            }

            @Override
            public void onFail(String msg) {
                System.out.println("添加失败：" + msg);
            }
        });
    }

    @Test
    public void testLog(){
        //LoggerFactory.getLogger(MainControllerTest.class).info("test");
        LogUtil.debug("调试");
        LogUtil.info("信息");
        LogUtil.warn("警告");
        LogUtil.error("错误");
    }

    @Test
    public void testIsNull(){
        Goods goods = new Goods();
        System.out.println(goods);
        System.out.println(StringUtils.isEmpty(goods.getName()));
        System.out.println(goods.getCount());
    }

    @Test
    public void testDialog(){
        //DialogUtils.showPromptDialog();
    }
}
