package com.seeease.flywheel.serve.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 供应商表
 * @TableName customer
 */
@TableName(value ="customer")
@Data
public class Customer implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 客户类型 1:个人 2.企业
     */
    private CustomerTypeEnum type;

    /**
     * 公司名称
     */
    private String customerName;

    /**
     * 开户名称
     */
    private String accountName;

    /**
     * 开户银行
     */
    private String bank;

    /**
     * 账号
     */
    private String bankAccount;

    /**
     * 客户标签
     */
    private String cusLabel;

    /**
     * 客户销售渠道
     */
    private String cusChannel;

    /**
     * 备注
     */
    private String remark;

    /**
     * logo图
     */
    private String logo;

    /**
     * 稀蜴对接人
     */
    private Integer dockingPeople;

    /**
     * 选择项，选项有：无，1家，2-3家，3-5家，5-10家，10家以上
     */
    private String storeNo;

    /**
     * 选择项，选项有：1-3人（包含3人），3-6人（包含6人），6-10人（包含10人），10-20人（包含20人），20-30人（包含30人），30人以上

选择项，选项有：1-3人（包含3人），3-6人（包含6人），6-10人（包含10人），10-20人（包含20人），20-30人（包含30人），30人以上


     */
    private String staffNo;

    /**
     * 腕表、箱包、饰品，以上选项可多选
     */
    private String mainBusiness;

    /**
     * 1-3万，3-10万，10万以上，以上选项可多选
     */
    private String mainPrice;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer delFlag;

    /**
     * 
     */
    private String prop;

    /**
     * 身份证
     */
    private String identityCard;

    /**
     * 身份证图
     */
    private String identityCardImage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}