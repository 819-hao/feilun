package com.seeease.flywheel.serve.rfid.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("store_shop")
@Data
public class StoreShop {
    private Integer id;
    private Integer shopId;
    private Integer storeId;
    private Integer delted;
}
