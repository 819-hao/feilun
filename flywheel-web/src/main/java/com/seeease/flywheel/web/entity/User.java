package com.seeease.flywheel.web.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/29
 */
@Data
public class User implements Serializable {

    private Integer id;
    private String userid;
    private String userName;
    private String tagName;
    private String tagShortcodes;
    private List<UserRole> roles;

}
