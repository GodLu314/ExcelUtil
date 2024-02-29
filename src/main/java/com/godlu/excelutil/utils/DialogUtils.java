package com.godlu.excelutil.utils;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by GodLu on 2020/3/15 15:32
 **/

public class DialogUtils {
    private static ProgressIndicator progressIndicator = null;//创建进度条
    private static Label coverLabel = null;//创建遮挡label

    /**
     * @Author GodLu
     * @Description //TODO 显示加载提示框
     * @Date 2020/4/17 9:17
     * @param gridPane 依附的载体
     * @param message 提示信息
     **/
    public static void showLoading(Pane gridPane, String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (progressIndicator == null || coverLabel == null){
                    progressIndicator = new ProgressIndicator();
                    progressIndicator.setTranslateX(540);
                    progressIndicator.setTranslateY(-100);
                    //progressIndicator.setStyle("-fx-column-halignment: center");
                    progressIndicator.setMaxSize(100,100);
                    //设置透明度背景展示加载中效果
                    coverLabel = new Label(message);
                    coverLabel.setStyle("-fx-alignment: center;-fx-text-fill: black;-fx-font-size: 20px;-fx-font-family: KaiTi;" +
                            "-fx-text-alignment: center");
                    coverLabel.setMaxSize(1500,680);
                    coverLabel.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FFFFFF80"),
                            CornerRadii.EMPTY, Insets.EMPTY)));
                    gridPane.getChildren().addAll(coverLabel,progressIndicator);
                }else {
                    coverLabel.setText(message);
                }
            }
        });
    }

    /**
     * @Author GodLu
     * @Description //TODO 关闭加载提示框
     * @Date 2020/4/17 9:16
     **/
    public static void closeLoading() {
        Platform.runLater(new Runnable() {
            public void run() {
                progressIndicator.setVisible(false);
                coverLabel.setVisible(false);
                progressIndicator = null;
                coverLabel = null;
            }
        });
    }

    /**
     * @Author GodLu
     * @Description //TODO 显示错误提示框
     * @Date 2020/4/17 9:16
     **/
    public static void showErrorDialog(Node node, String errorMessage){
        showDialog(node,"错误",errorMessage,true);
    }

    /**
     * @Author GodLu
     * @Description //TODO 显示提示框
     * @Date 2020/4/17 9:16
     **/
    public static void showPromptDialog(Node node, String message){
        showDialog(node,"提示",message,false);
    }
    /**
     * @Author GodLu
     * @Description //TODO 展示对话框
     * @Date 2020/4/18 22:57
     * @param node 对话框依附的载体
     * @param title 对话框标题
     * @param message 对话框信息
     **/
    private static void showDialog(Node node,String title,String message,boolean isError){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //新建alert提示框
                JFXAlert<String> alert = new JFXAlert<>((Stage) node.getScene().getWindow());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setOverlayClose(false);
                JFXDialogLayout layout = new JFXDialogLayout();
                Label label = new Label(title);
                if (isError) {
                    label.setStyle("-fx-font-family: System;-fx-font-size: 20;-fx-text-fill: #ff002f");
                }else {
                    label.setStyle("-fx-font-family: System;-fx-font-size: 20;-fx-text-fill: #068c21");
                }
                layout.setHeading(label);
                //layout.setStyle("-fx-background-color: #FFCC0070");
                //新建确定按钮
                JFXButton positiveBtn = new JFXButton("确定");
                positiveBtn.setDefaultButton(true);
                positiveBtn.setStyle("-fx-font-family: System;-fx-font-size: 16");
                positiveBtn.setTextFill(Paint.valueOf("#0099ff"));
                //将组建加入到布局
                layout.setBody(new VBox(new Label(message)));
                //点击确定后执行
                positiveBtn.setOnAction(closeEvent -> {
                    alert.hideWithAnimation();
                });
                layout.setActions(positiveBtn);
                alert.setContent(layout);
                alert.showAndWait();
            }
        });
    }
}
