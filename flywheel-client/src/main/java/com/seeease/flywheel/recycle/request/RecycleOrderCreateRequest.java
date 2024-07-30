package com.seeease.flywheel.recycle.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 回收、回购请求接口
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecycleOrderCreateRequest implements Serializable {
    //批次号
    private String serial;
    //商品id
    private Integer stockId;
    //品牌id
    private Integer brandId;
    //批次id
    private Integer batchId;
    //供应商id
    private Integer customerId;
    //客户经理id
    private String employeeId;
    //检测id
    private Integer detectionId;
    //年份
    private String year;
    //第一次估价
    private String valuationPrice;
    //第一次估价备注
    private String valuationRemark;
    //第一次报价人id
    private Integer valuationPriceId;
    //估价图片
    private String valuationImage;
    //第二次估价
    private BigDecimal valuationPriceTwo;
    //第二次估价备注
    private String valuationPriceTwoRemark;
    //第二次报价人id
    private Integer valuationPriceTwoId;
    //估价时间
    private Date valuationTime;
    //回收协议
    private String agreement;
    //销售时间
    private Date saleTime;
    //质检结果
    private String qualityTestingResult;
    //状态
    private Integer state;
    //商城图片
    private String shopImage;
    //需求门店id
    private Integer demandId;
    //估价单主键
    private String assessId;
    //类型用来区分来源：回收还是置换
    private Integer type;
    //用来区分大类：回收还是回购
    private Integer recycleType;
    //三方关联单号
    private String bizOrderCode;

}
