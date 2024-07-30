package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 抖音门店映射表
 * @TableName kuaishou_print_mapping
 */
@TableName(value ="kuaishou_print_mapping")
@Data
public class KuaishouPrintMapping extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 快手门店id
     */
    private Long kuaiShouShopId;

    /**
     * 飞轮门店id
     */
    private Integer shopId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区/县名称
     */
    private String districtName;

    /**
     * 街道名称
     */
    private String streetName;

    /**
     * 剩余详细地址
     */
    private String detailAddress;

    /**
     * 寄件人姓名
     */
    private String name;

    /**
     * 寄件人移动电话
     */
    private String mobile;

    /**
     * 物流商标准模版信息
     */
    private String templateUrl;

    /**
     * 自定义模版信息
     */
    private String customTemplateUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}