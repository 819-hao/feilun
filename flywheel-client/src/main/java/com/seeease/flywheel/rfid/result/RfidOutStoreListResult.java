package com.seeease.flywheel.rfid.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidOutStoreListResult implements Serializable {

    /**
     * 关联单据类型
     */
    private String type;
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
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
