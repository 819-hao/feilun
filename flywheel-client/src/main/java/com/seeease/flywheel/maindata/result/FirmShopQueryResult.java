package com.seeease.flywheel.maindata.result;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class  FirmShopQueryResult implements Serializable {


    /**
     * 主键
     */
    private Integer id;


    /**
     * 公司名称
     */
    private String firmName;
    /**
     * 地址
     */
    private String address;
    /**
     * 电话
     */
    private String phone;
    /**
     * 税号
     */
    private String tiNo;

    /**
     * 开户行
     */
    private String bod;
    /**
     * 银行账号
     */
    private String bankAcct;
    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date regTime;
    /**
     * 对公账户
     */
    private String csAcct;
    /**
     * 店铺信息
     */
    private String shopInfo;
    /**
     * 收款方式
     */
    private String pm;
    /**
     * 抖店主id
     */
    private String tkMainId;
    /**
     * 抖店短id
     */
    private String tkSortId;
    /**
     * 账号uid
     */
    private String tkUid;
    /**
     * 开店日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date openTime;

    /**
     * 经营状态
     */
    private String state;
    /**
     * 部门
     */
    private String dept;
    /**
     * 部门id
     */
    private Integer deptId;
    /**
     * 店铺电话
     */
    private String shopPhone;
    /**
     * 店铺地址
     */
    private String shopAddress;
    /**
     * 营业执照
     */
    private String bizLicense;
    /**
     * 法人证件
     */
    private List<String> lpi;

    private String hfMemberId;

}
