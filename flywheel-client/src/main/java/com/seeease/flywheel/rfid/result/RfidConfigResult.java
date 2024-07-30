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
public class RfidConfigResult implements Serializable {
    /**
     * 版本号
     */
    private String version;
    /**
     * 平台id
     */
    private Integer platform;
    /**
     * 下载地址
     */
    private String url;
    /**
     * 描述
     */
    private String desc;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
