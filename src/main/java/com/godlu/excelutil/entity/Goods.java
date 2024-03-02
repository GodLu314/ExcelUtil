package com.godlu.excelutil.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: GodLu
 * @create: 2024-02-24 13:47
 * @description: TODO
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Goods {
    @ExcelProperty(value = "物品名称")
    private String name;//名称
    @ExcelProperty(value = "计量单位")
    private String measureUnit;//计量单位
    @ExcelProperty(value = "入库数量")
    private Float count;//数量
    @ExcelProperty(value = "入库单价")
    private Float price;//单价
    @ExcelProperty(value = "总金额")
    private Float totalPrice;//总金额
}
