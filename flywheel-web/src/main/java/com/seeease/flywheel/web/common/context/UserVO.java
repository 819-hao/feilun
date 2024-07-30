package com.seeease.flywheel.web.common.context;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户登陆信息
 *
 * @author zgq
 */
@NoArgsConstructor
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;


    private Boolean customerSign;

    private Integer id;

    private String nameRole;

    /**
     * 用户唯一标识
     */
    private String token;


    /**
     * 企微成员所属部门id列表
     */

    private String department;

    /**
     * 登陆时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 数据权限列表
     */
    private List<String> dataPermissions;
    /**
     * 资源路径权限列表
     */
    private List<String> urlPermissions;

    private List<StoreVo> stores = new ArrayList<>();
    private List<RoleDropVo> roles = new ArrayList<>();
    /**
     * 企业微信id
     */
    private String userid;

    /**
     * 成员名称
     */
    private String userName;
    /**
     * 职位名称
     */

    private String position;
    /**
     * 帐号状态（0正常 1停用）
     */

    private Boolean mobileSign;

    private String status;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date syncTime;

    private Integer dataPerm;


    private String jobNumber;
}
