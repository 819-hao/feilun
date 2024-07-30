package com.seeease.flywheel.notify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AccountReceiptConfirmNotice extends BaseNotice {
    /**
     * 打款单id
     */
    private Integer id;


    /**
     * 打款单号
     */
    private String serialNo;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 状态描述
     */
    private String state;

    /**
     * true是跳转财务视角 false是跳转业务视角
     */
    private String scene;
}

