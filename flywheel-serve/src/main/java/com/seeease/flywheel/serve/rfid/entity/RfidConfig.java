package com.seeease.flywheel.serve.rfid.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.util.Date;

@TableName(value = "rfid_config", autoResultMap = true)
@Data
public class RfidConfig  {
    @TableId(type = IdType.AUTO)
    private Integer id;
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
    @TableField("`desc`")
    private String desc;
    /**
     * 创建时间
     */
    private Date createTime;
}
