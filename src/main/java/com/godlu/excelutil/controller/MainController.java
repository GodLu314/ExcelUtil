package com.godlu.excelutil.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.godlu.excelutil.entity.Goods;
import com.godlu.excelutil.entity.ShortGoods;
import com.godlu.excelutil.entity.SystemGoods;
import com.godlu.excelutil.utils.DialogUtils;
import com.godlu.excelutil.utils.FileUtil;
import com.godlu.excelutil.utils.LogUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class MainController implements Initializable {
    private List<Goods> excelDataList = new ArrayList<>();//原表数据集合
    private List<Goods> finalDataList = new ArrayList<>();//回写数据集合
    private List<SystemGoods> systemDataList = new ArrayList<>();//库内数据集合
    private List<String> shortNameList = new ArrayList<>();//简称数据集合（需要录入）
    private List<String> shortNameLackList = new ArrayList<>();//检测到不存在简称的原表数据集合

    private File fileOutDir = null;//文件输出目录
    private String inFileName = null;//输入文件名称

    public VBox mainVB;
    public TextField filePathTF;
    public TextField outPathTF;
    public TextField shortNameTF;
    public TextField goodsNameTF;
    public TextField measureUnitTF;
    public TextField txtPathTF;
    public CheckBox addTotalCB;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //读取json数据
        readJsonData();
        //展示温馨提示
        showTipDialog();
        //设置拖拽文件
        setDragEvent(filePathTF, new DragListener() {
            @Override
            public void onDragFile(File file) {
                updateInFile(file);
            }
        },"xls","xlsx");
        setDragEvent(txtPathTF, new DragListener() {
            @Override
            public void onDragFile(File file) {
                updateTxtFile(file);
            }
        },"txt");
    }
    private void showTipDialog() {
        DialogUtils.showPromptDialog(mainVB,"每次选择表格时请务必保证表格内列名称与后台模板一致，否则无法正常读取");
    }
    private void readJsonData() {
        //获取库内数据集合
        systemDataList.clear();
        String systemDataStr = FileUtil.getInstance().getSystemDataStr();
        JSONArray systemDataArray = JSON.parseArray(systemDataStr);
        if (systemDataArray != null) {
            systemDataList = systemDataArray.toJavaList(SystemGoods.class);
        }
        LogUtil.info("读取到" + systemDataList.size() + "条库内数据集合：" + systemDataList);
        //获取简称集合
        shortNameList.clear();
        String shotDataStr = FileUtil.getInstance().getShotDataStr();
        JSONArray shorNameArray = JSON.parseArray(shotDataStr);
        if (shorNameArray != null) {
            shortNameList = shorNameArray.toJavaList(String.class);
        }
        LogUtil.info("读取到" + shortNameList.size() + "条简称集合为：" + shortNameList);
    }

    /**
     * @Author: GodLu
     * @Date: 2024/3/2 1:36
     * @Description: 为指定控件添加拖拽文件功能
     * @param node 指定控件
     * @param listener 监听
     * @param extensions 文件后缀名
     * @return: void
     */
    private void setDragEvent(Node node,DragListener listener,String... extensions) {
        node.setFocusTraversable(false);
        node.setOnDragOver(new EventHandler<DragEvent>() { //node添加拖入文件事件
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()) {
                    File file = dragboard.getFiles().get(0);
                    //判断文件是否为指定类型的文件
                    String[] sufixArray = extensions.clone();
                    List<String> sufixs = Arrays.asList(sufixArray);
                    if (sufixs.contains(FilenameUtils.getExtension(file.getName()))) { //用来过滤拖入类型
                        event.acceptTransferModes(TransferMode.COPY);//接受拖入文件
                    }
                }
            }
        });
        node.setOnDragDropped(new EventHandler<DragEvent>() { //拖入后松开鼠标触发的事件
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                if (event.isAccepted()) {
                    //获取拖入的文件
                    listener.onDragFile(dragboard.getFiles().get(0));
                }
            }
        });
        node.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                File file = dragboard.getFiles().get(0);
                //判断文件是否为指定类型的文件
                String[] sufixArray = extensions.clone();
                List<String> sufixs = Arrays.asList(sufixArray);
                if (sufixs.contains(FilenameUtils.getExtension(file.getName()))) { //用来过滤拖入类型
                    node.setStyle("-fx-opacity: 0.3;");//设置透明度背景展示拖入效果
                }
            }
        });
        node.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                node.setStyle("-fx-opacity: 1;");
            }
        });
    }
    public interface DragListener{
        void onDragFile(File file);
    }

    //选择输入文件路径
    public void selectFilePath(ActionEvent actionEvent) {
        updateInFile(FileUtil.getInstance().selectFile(mainVB.getScene().getWindow(), "*.xls", "*.xlsx"));
    }

    //选择输出文件路径
    public void selectOutFileDir(ActionEvent actionEvent) {
        // 创建文件夹选择器对象
        DirectoryChooser directoryChooser = new DirectoryChooser();
        // 显示文件夹选择对话框
        File fileOutDir = directoryChooser.showDialog(mainVB.getScene().getWindow());
        if (fileOutDir != null) {
            if (!fileOutDir.isDirectory()) {
                DialogUtils.showErrorDialog(mainVB, "请选择有效的文件夹！");
                return;
            }
            updateOutFileDir(fileOutDir);
        }
    }

    /**
     * @Author: GodLu
     * @Date: 2024/2/26 15:26
     * @Description: 更新输入文件
     * @param inFile 输入文件
     * @return: void
     */
    private void updateInFile (File inFile){
        excelDataList.clear();
        filePathTF.setText(inFile.getPath());
        inFileName = inFile.getName();
        System.out.println("输入文件路径：" + inFile.getPath());
        updateOutFileDir(inFile.getParentFile());
        //读取原表数据集合
        EasyExcel.read(inFile.getPath(), Goods.class, new ReadListener<Goods>() {
            @Override
            public void invoke(Goods goods, AnalysisContext analysisContext) {
                if (StringUtils.isEmpty(goods.getName()) || StringUtils.isEmpty(goods.getMeasureUnit()) ||
                        goods.getCount() == null || goods.getCount() == 0 ||
                        goods.getPrice() == null || goods.getPrice() == 0 ||
                        goods.getTotalPrice() == null || goods.getTotalPrice() == 0){
                    return;
                }
                excelDataList.add(goods);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).doReadAll();
        LogUtil.info("共读取到["+ excelDataList.size() + "]条原表数据：" + excelDataList);
    }

    /**
     * @Author: GodLu
     * @Date: 2024/2/25 15:00
     * @Description: 更新输出文件
     * @param outFile 输出文件
     * @return: void
     */
    private void updateOutFileDir(File outFile) {
        if (!outFile.isDirectory()) return;
        fileOutDir = outFile;
        outPathTF.setText(outFile.getPath());
        LogUtil.info("文件输出目录：" + outFile.getPath());
    }

    public void doWork(ActionEvent actionEvent) {
        if (StringUtils.isEmpty(filePathTF.getText()) || excelDataList.size() == 0) return;
        DialogUtils.showLoading(mainVB,"加载中...");
        //第一步：遍历原表数据集合，将与库内数据同名的首个原表数据存入回写数据，返回剩余原表数据集合
        List<Goods> residualList = getResidualList(excelDataList);

        //第二步：遍历原表数据集合，将原表名称与简称匹配的数据封装成Map
        List<ShortGoods> shortGoodsList = getShortGoodsList(residualList);

        //第三步：遍历临时Map，将Map的Value中的name和measureUnit替换为库内数据相对应的值
        writeToOutFile(shortGoodsList);
    }

    /**
    * @Author: GodLu
    * @Date: 2024/2/26 15:19
    * @Description: 遍历原表数据集合，将与库内数据同名的首个原表数据存入回写数据，返回剩余原表数据集合
    * @param dataList 需要遍历的原表数据集合
    * @return: List<Goods>
    */
    private List<Goods> getResidualList(List<Goods> dataList) {
        //遍历原表数据集合
        Iterator<Goods> iterator = dataList.iterator();
        Goods currentGoods;
        while (iterator.hasNext()){
            currentGoods = iterator.next();
            //原表数据与库内数据对比
            for (SystemGoods systemData : systemDataList) {
                //如果库内数据存在与当前数据同名的数据
                if (StringUtils.equals(currentGoods.getName(),systemData.getName())){
                    //原表数据对比回写数据集合
                    boolean isExcelEqFinal = false;
                    for (Goods finalGoods : finalDataList) {
                        //如果回写数据存在与当前数据同名的数据
                        if (StringUtils.equals(currentGoods.getName(),finalGoods.getName())){
                            isExcelEqFinal = true;
                            break;
                        }
                    }
                    //如果回写数据不存在与当前数据同名的数据
                    if (!isExcelEqFinal){
                        //存入回写数据集合
                        finalDataList.add(currentGoods);
                        //从原表数据集合中移除当前数据项
                        iterator.remove();
                    }
                    break;
                }
            }
        }
        return dataList;
    }

    /**
    * @Author: GodLu
    * @Date: 2024/2/26 15:04
    * @Description: 遍历原表数据集合，获取简称商品信息集合
    * @param excelDataList 原表数据集合
    * @return: Map<String,Goods>
    */
    private List<ShortGoods> getShortGoodsList(List<Goods> excelDataList) {
        List<ShortGoods> shortGoodsList = new ArrayList<>();//简称物品信息集合
        //遍历原表数据集合
        for (Goods currentGoods : excelDataList) {
            //当前原表数据对比简称数据集合
            boolean isTempCtShort = false;
            for (String shortName : shortNameList) {
                //如果简称数据集合存在与当前原表数据匹配的数据
                if (currentGoods.getName().contains(shortName)){
                    isTempCtShort = true;
                    //将当前简称和当前原表数据封装进ShortGoods对象并添加到简称物品信息集合
                    shortGoodsList.add(new ShortGoods(shortName,currentGoods));
                    break;
                }
            }
            //如果简称数据集合不存在与当前原表数据匹配的数据
            if (!isTempCtShort){
                //DialogUtils.showErrorDialog(mainVB,"当前数据的简称未录入！");
                //将当前原表数据存储到检测到不存在简称的原表数据集合
                shortNameLackList.add(currentGoods.getName());
                //存入回写数据集合
                finalDataList.add(currentGoods);
            }
        }
        return shortGoodsList;
    }

    /**
    * @Author: GodLu
    * @Date: 2024/2/26 14:58
    * @Description: 输出excel
    * @param shortGoodsList 简称物品信息集合
    * @return: Void
    */
    private void writeToOutFile(List<ShortGoods> shortGoodsList) {
        /*遍历简称物品信息集合，将ShortGoods的Goods中的name和measureUnit替换为库内数据相对应的值，然后对比回写数据，若已经存在则等待下一轮，若不存在则存入回写数据集合。
        * 最后输出excel
        * */
        //遍历简称物品信息集合
        Iterator<ShortGoods> iterator = shortGoodsList.iterator();
        while (iterator.hasNext()){
            ShortGoods currentShortGoods = iterator.next();
            //当前ShortGoods的shortName与库内数据集合做对比
            boolean isSysCtShortName = false;
            for (SystemGoods systemData : systemDataList) {
                //如果库内数据集合存在与当前当前ShortGoods的shortName匹配的数据
                if (systemData.getName().contains(currentShortGoods.getShortName())){
                    isSysCtShortName = true;
                    //设置当前ShortGoods的goods的name为库内数据名称
                    currentShortGoods.getGoods().setName(systemData.getName());
                    //设置当前ShortGoods的goods的measureUnit为库内数据的计量单位
                    currentShortGoods.getGoods().setMeasureUnit(systemData.getMeasureUnit());
                    //当前ShortGoods的goods的name与回写数据集合对比
                    boolean isKeyNameEqFinal = false;
                    for (Goods finalGoods : finalDataList) {
                        //如果回写数据集合的name存在与当前ShortGoods的goods的name相同的数据
                        if (currentShortGoods.getGoods().getName().equals(finalGoods.getName())) {
                            System.out.println("回写数据集合已存在：" + currentShortGoods.getGoods().getName());
                            isKeyNameEqFinal = true;
                            //存入剩余Map集合
                            break;
                        }
                    }
                    //如果回写数据集合的name不存在与当前ShortGoods的goods的name相同的数据
                    if (!isKeyNameEqFinal){
                        //存入回写数据集合
                        finalDataList.add(currentShortGoods.getGoods());
                        //从简称物品信息集合移除当前元素
                        iterator.remove();
                        break;
                    }
                }
            }
            if (!isSysCtShortName){
                //如果库内数据集合不存在与当前ShortGoods的shortName匹配的数据
                DialogUtils.showErrorDialog(mainVB,"库内没有简称为[" + currentShortGoods.getGoods().getName() + "]的数据！");
                //存入回写数据集合
                finalDataList.add(currentShortGoods.getGoods());
                //从简称物品信息集合移除当前元素
                iterator.remove();
            }
        }
        //将回写数据集合写出到输出文件
        writeFile();
        if (shortGoodsList.size() > 0){
            writeToOutFile(shortGoodsList);
        }else {
            DialogUtils.closeLoading();
            if (shortNameLackList.size() > 0) {
                DialogUtils.showPromptDialog(mainVB, "检测到以下原表数据缺少相应的简称数据：" + shortNameLackList);
            }else {
                DialogUtils.showPromptDialog(mainVB,"转换完成！");
            }
            try {
                Desktop.getDesktop().open(fileOutDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            filePathTF.setText(null);
            outPathTF.setText(null);
            //清空资源
            excelDataList.clear();
            finalDataList.clear();
            shortNameLackList.clear();
        }
    }

    /**
    * @Author: GodLu
    * @Date: 2024/2/26 17:04
    * @Description: 写出excel表格
    * @param dataList 要写出的数据集合
    * @return: void
    */
    private static int saveCount = 1;
    private void writeFile() {
        //拼接excel文件输出路径
        String  outFilePath;
        if (inFileName == null) {
            outFilePath = fileOutDir.getPath() + "done_" + System.currentTimeMillis() + ".xls";
        }
        String[] splits = inFileName.split("\\.");
        outFilePath = fileOutDir.getPath() + File.separator + splits[0]+ "_done_" + saveCount + "." + splits[1];
        //合计数据写出到最后一行
        if (addTotalCB.isSelected()) {
            Goods total = new Goods();
            total.setName("合计");
            Float totalPrice = 0F;
            for (Goods goods : finalDataList) {
                totalPrice += goods.getTotalPrice();
            }
            total.setTotalPrice(totalPrice);
            finalDataList.add(total);
        }
        //写出到excel文件
        LogUtil.info("第" + saveCount + "次保存内容:" + finalDataList.toString());
        EasyExcel.write(outFilePath, Goods.class)
                .sheet("sheet1")
                .doWrite(() -> {
                    return finalDataList;
                });
        saveCount += 1;
        finalDataList.clear();
    }



/**=========================================================手动添加============================================================*/
    //添加简称
    public void addShortName(ActionEvent actionEvent) {
        String shortName = shortNameTF.getText();
        if (StringUtils.isEmpty(shortName)){
            DialogUtils.showErrorDialog(mainVB,"请输入数据！");
        }
        //遍历简称数据集合，数据存在则不保存
        for (String sname : shortNameList) {
            if (StringUtils.equals(shortName,sname)){
                DialogUtils.showErrorDialog(mainVB,"数据已存在！");
                return;
            }
        }
        shortNameList.add(shortName);
        FileUtil.getInstance().saveShotDataStr(shortNameList, new FileUtil.WriteFileListener() {
            @Override
            public void onSuccess() {
                DialogUtils.showPromptDialog(mainVB,"添加成功！");
                shortNameTF.setText(null);
            }

            @Override
            public void onFail(String msg) {
                DialogUtils.showErrorDialog(mainVB,"添加失败！");
            }
        });
    }
    //添加库内数据
    public void addSystemData(ActionEvent actionEvent) {
        String goodsName = goodsNameTF.getText();
        String measureUnit = measureUnitTF.getText();
        if (StringUtils.isEmpty(goodsName) && StringUtils.isEmpty(measureUnit)){
            DialogUtils.showErrorDialog(mainVB,"请输入数据！");
        }
        //遍历库内数据集合，数据存在则不保存
        for (SystemGoods systemGoods : systemDataList) {
            if (goodsName.equals(systemGoods.getName()) && measureUnit.equals(systemGoods.getMeasureUnit())){
                DialogUtils.showErrorDialog(mainVB,"数据已存在！");
                return;
            }
        }
        SystemGoods systemGoods = new SystemGoods(goodsName, measureUnit);
        systemDataList.add(systemGoods);
        FileUtil.getInstance().saveSystemDataStr(systemDataList, new FileUtil.WriteFileListener() {
            @Override
            public void onSuccess() {
                DialogUtils.showPromptDialog(mainVB,"添加成功！");
                goodsNameTF.setText(null);
                measureUnitTF.setText(null);
            }

            @Override
            public void onFail(String msg) {
                DialogUtils.showErrorDialog(mainVB,"添加失败！");
            }
        });
    }



    /**=====================================================自动添加==========================================================*/
    //选择txt文件
    public void selectTxtPath(ActionEvent actionEvent) {
        updateTxtFile(FileUtil.getInstance().selectFile(mainVB.getScene().getWindow(), "*.txt"));
    }
    //更新txt文件路径
    private void updateTxtFile(File txtFile){
        txtPathTF.setText(txtFile.getPath());
    }
    //开始读取txt文件
    public void beginRead(ActionEvent actionEvent) {
        DialogUtils.showLoading(mainVB,"正在添加...");
        //从txt文件读取
        List<SystemGoods> list = readSystemDataListFromTxt(txtPathTF.getText());
        //保存
        saveSystemDataList(list);
    }
    /**
    * @Author: GodLu
    * @Date: 2024/2/27 23:33
    * @Description: 保存库内数据集合
    * @param systemGoodsList 库内数据集合
    * @return: void
    */
    private void saveSystemDataList(List<SystemGoods> systemGoodsList){
        if (systemGoodsList == null || systemGoodsList.size() == 0){
            LogUtil.error("保存数据为空");
            DialogUtils.closeLoading();
            DialogUtils.showErrorDialog(mainVB,"保存数据为空！");
            return;
        }
        systemDataList.addAll(systemGoodsList);
        FileUtil.getInstance().saveSystemDataStr(systemDataList, new FileUtil.WriteFileListener() {
            @Override
            public void onSuccess() {
                LogUtil.info("添加数据成功：" + systemDataList);
                DialogUtils.closeLoading();
                DialogUtils.showLongMsgDialog(mainVB,"添加数据成功：" + systemDataList);
            }

            @Override
            public void onFail(String msg) {
                LogUtil.error("添加失败：" + msg);
                DialogUtils.closeLoading();
                DialogUtils.showErrorDialog(mainVB,"添加失败：" + msg);
            }
        });
    }
    /**
    * @Author: GodLu
    * @Date: 2024/2/27 23:29
    * @Description: 从txt文件读取库内数据集合
    * @param txtPath txt文件路径
    * @return: List<SystemGoods>
    */
    private List<SystemGoods> readSystemDataListFromTxt(String txtPath) {
        List<SystemGoods> systemDataList = new ArrayList<>();
        String jsonStr = FileUtil.getInstance().readStrFromFile(txtPath);
        if (!jsonStr.startsWith("[") || !jsonStr.endsWith("]")){
            DialogUtils.showErrorDialog(mainVB,"json数据不合法，请在数据最外侧加上“[]”！");
            return null;
        }
        JSONArray jsonArray = JSON.parseArray(jsonStr);
        for (Object obj : jsonArray) {
            JSONObject jsonObject = JSON.parseObject(obj.toString());
            String data = jsonObject.getString("data");
            LogUtil.info("data节点字符串：" + data);
            JSONArray dataArray = JSON.parseArray(data);
            LogUtil.info("data节点数组：" + dataArray.size() + dataArray);
            for (Object o : dataArray) {
                JSONObject json = JSON.parseObject(o.toString());
                String goodsName = json.getString("WPMC");
                String measureUnit = json.getString("JLDW");
                //System.out.println(goodsName + ":" + measureUnit);
                //遍历库内数据集合，数据存在则不保存
                boolean shouldSave = true;
                for (SystemGoods systemGoods : systemDataList) {
                    if (StringUtils.equals(goodsName,systemGoods.getName())){
                        DialogUtils.showErrorDialog(mainVB,"数据已存在，不用添加！");
                        shouldSave = false;
                    }
                }
                if (shouldSave) {
                    SystemGoods systemGoods = new SystemGoods(goodsName, measureUnit);
                    systemDataList.add(systemGoods);
                }
            }
        }
        return systemDataList;
    }
}