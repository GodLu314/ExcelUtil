package com.godlu.excelutil.utils;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
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
        showDialog(node,"错误",errorMessage,true,false);
    }

    /**
     * @Author GodLu
     * @Description //显示提示框
     * @Date 2020/4/17 9:16
     **/
    public static void showPromptDialog(Node node, String message){
        showDialog(node,"提示",message,false,false);
    }

    /**
    * @Author: GodLu
    * @Date: 2024/3/2 2:37
    * @Description: 显示长文本提示框
    * @param node 节点
    * @param message 长文本
    * @return: void
    */
    public static void showLongMsgDialog(Node node, String message){
        showDialog(node,"提示",message,false,true);
    }
    /**
     * @Author GodLu
     * @Description //展示对话框
     * @Date 2020/4/18 22:57
     * @param node 对话框依附的载体
     * @param title 对话框标题
     * @param message 对话框信息
     **/
    private static void showDialog(Node node,String title,String message,boolean isError,boolean isLongMsg){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //新建alert提示框
                JFXAlert<String> alert = new JFXAlert<>((Stage) node.getScene().getWindow());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setOverlayClose(false);
                JFXDialogLayout layout = new JFXDialogLayout();
                Label titleLable = new Label(title);
                if (isError) {
                    titleLable.setStyle("-fx-font-family: System;-fx-font-size: 20;-fx-text-fill: #ff002f");
                }else {
                    titleLable.setStyle("-fx-font-family: System;-fx-font-size: 20;-fx-text-fill: #068c21");
                }
                HBox titleHBox = new HBox();
                titleHBox.setPrefSize(400,50);
                titleHBox.setAlignment(Pos.CENTER);
                titleHBox.getChildren().add(titleLable);
                layout.setHeading(titleHBox);
                //layout.setStyle("-fx-background-color: #FFCC0070");
                //将组建加入到布局
                Label msgLable = new Label(message);
                msgLable.setWrapText(true);//设置自动换行
                msgLable.setPrefWidth(400);
                if (isLongMsg) {
                    ScrollPane scrollPane = new ScrollPane(msgLable);
                    scrollPane.setPrefSize(400, 200);
                    scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
                    scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    layout.setBody(scrollPane);
                }else {
                    HBox bodyHBox = new HBox();
                    bodyHBox.setPrefSize(400,200);
                    bodyHBox.setAlignment(Pos.CENTER);
                    bodyHBox.getChildren().add(msgLable);
                    layout.setBody(bodyHBox);
                }
                //新建确定按钮
                JFXButton positiveBtn = new JFXButton("关闭");
                positiveBtn.setDefaultButton(true);
                positiveBtn.setStyle("-fx-font-family: System;-fx-font-size: 16");
                positiveBtn.setTextFill(Paint.valueOf("#0099ff"));
                //点击确定后执行
                positiveBtn.setOnAction(closeEvent -> {
                    alert.hideWithAnimation();
                });
                HBox actionsHBox = new HBox();
                actionsHBox.setPrefSize(400,50);
                actionsHBox.setAlignment(Pos.CENTER);
                actionsHBox.getChildren().add(positiveBtn);
                layout.setActions(actionsHBox);
                layout.setPrefSize(400,300);
                alert.setContent(layout);
                alert.showAndWait();
            }
        });
    }
}
