package com.seeease.flywheel.rfid.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seeease.flywheel.storework.result.WmsWorkListResult;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/8/31
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RfidWmsWorkListResult implements Serializable {
    /**
     * 关联单号
     */
    private String no;
    /**
     * 待出库数量
     */
    private Integer waitNo;
    /**
     * 已出库数量
     */
    private Integer outNo;
    /**
     * 快递单号
     */
    private String lgsCode;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private String createTime;
}
