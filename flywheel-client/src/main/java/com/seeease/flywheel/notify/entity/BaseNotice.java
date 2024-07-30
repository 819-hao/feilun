package com.seeease.flywheel.notify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseNotice implements Serializable {

    /**
     * 接收的用户
     *
     * @return
     */
    private List<Integer> toUserIdList;

    /**
     * 接收的角色
     *
     * @return
     */
    private List<String> toUserRoleKey;

    /**
     * 门店id
     */
    private Integer shopId;

}
