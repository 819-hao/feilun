package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.fix.convert.StringMapperTypeHandler;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 公司店铺

 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="firm_shop",autoResultMap = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmShop extends BaseDomain {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> lpi;

    private String hfMemberId;


}