package com.seeease.flywheel.recycle.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 协议图片信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolSyncRequest implements Serializable {

    /**
     * 回收单主键
     */
    private Integer recycleId;

    /**
     * 回购政策协议图片
     */
    private String protocol;

}
