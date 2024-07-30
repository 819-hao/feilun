package com.seeease.flywheel.web.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/1/29
 */
@Data
public class UserRole implements Serializable {
    private String roleName;
    private String roleKey;
    private Integer shopSpec;
}
