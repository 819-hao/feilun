package com.seeease.flywheel.web.common.context;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleDropVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    private Long roleId;

    /**
     * 门店名称
     */

    private String roleName;


}
