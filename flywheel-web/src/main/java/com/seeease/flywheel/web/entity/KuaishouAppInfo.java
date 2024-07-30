package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 快手开放平台配置表
 * @TableName kuaishou_app_info
 */
@TableName(value ="kuaishou_app_info")
@Data
public class KuaishouAppInfo extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 寄件人姓名
     */
    private String name;

    /**
     * 账号Id
     */
    private String openShopId;

    /**
     * appid
     */
    private String appId;

    /**
     * appSecret
     */
    private String appSecret;

    /**
     * signSecret签名
     */
    private String signSecret;

    /**
     * 消息秘钥
     */
    private String encodingAesKey;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}