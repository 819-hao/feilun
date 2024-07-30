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
 * @TableName kuaishou_token_info
 */
@TableName(value ="kuaishou_token_info")
@Data
public class KuaishouTokenInfo extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * appid
     */
    private String appId;

    /**
     * open_id
     */
    private String openId;

    /**
     * 认证token
     */
    private String accessToken;

    /**
     * 过期时间
     */
    private Long expiresIn;

    /**
     * 过期时刻
     */
    private Long accessTokenExpiresTime;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 过期时间
     */
    private Long refreshTokenExpiresIn;

    /**
     * 过期时刻
     */
    private Long refreshTokenExpiresTime;

    /**
     * access_token包含的scope
     */
    private String scopes;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}