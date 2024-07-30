package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * api统计
 * @TableName api_log
 */
@TableName(value ="api_log")
@Data
public class ApiLog extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 时间戳
     */
    private Long nowTime;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 门店id
     */
    private Integer storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求类方法
     */
    private String classMethod;

    /**
     * 请求来源地址ip
     */
    private String remoteAddr;

    /**
     * 请求uri
     */
    private String remoteUri;

    /**
     * 登录用户信息
     */
    private String loginUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}