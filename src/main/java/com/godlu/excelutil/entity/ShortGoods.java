package com.godlu.excelutil.entity;

import lombok.Data;
import java.util.UUID;

/**
 * @author: GodLu
 * @create: 2024-02-28 22:08
 * @description: TODO
 */
@Data
public class ShortGoods {
    private String id;//唯一id
    private String shortName;//简称
    private Goods goods;//物品信息

    public ShortGoods() {
        id = UUID.randomUUID().toString();
    }

    public ShortGoods(String shortName, Goods goods) {
        id = UUID.randomUUID().toString();
        this.shortName = shortName;
        this.goods = goods;
    }
}
