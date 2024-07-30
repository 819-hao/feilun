package com.seeease.flywheel.recycle.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 回收、回购请求接口
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketRecycleOrderRequest implements Serializable {
    //用户名字
    private String userName;
    //用户手机号
    private String phone;
    //客户经理id
    private String employeeId;
    //门店id
    private Integer storeId;
    //品牌名称
    private String brandName;
    //商城估价单主键
    private String assessId;
    //三方关联单号
    private String bizOrderCode;
    //商城传递回收订单图片
    private List<String> assessPictureVOS;
    //飞轮商品id
    private Integer stockId;
    //飞轮销售单号
    private String serialNo;
    /**
     *用来区分大类：回收还是回购
     * @see com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum
     */
    private Integer recycleType;

}
