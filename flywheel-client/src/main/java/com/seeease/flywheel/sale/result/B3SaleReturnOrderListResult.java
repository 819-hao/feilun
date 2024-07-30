package com.seeease.flywheel.sale.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B3SaleReturnOrderListResult implements Serializable {
    /**
     * 作业id
     */
    private Integer id;
    /**
     * 销售退货lineid
     */
    private Integer bsroId;
    /**
     * 图片
     */
    private String image;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;
    /**
     * 型号
     */
    private String model;
    /**
     * 表身号
     */
    private String sn;
    /**
     * 附件详情
     */
    private String attachment;
    /**
     * 发货物流单号
     */
    private String rtNo;
    /**
     * 退货物流单号
     */
    private String stNo;
    /**
     * 关联单号
     */
    private String atNo;
    /**
     * 抖音销售单号
     */
    private String tiktokNo;
    /**
     * 订单来源
     */
    private String origin;
    /**
     * 客户姓名
     */
    private String cName;
    /**
     * 客户电话
     */
    private String cPhone;
    /**
     * 客户地址
     */
    private String cAddress;
    /**
     * 预作业单号
     */

    private String serialNo;
    /**
     * 备注
     */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
