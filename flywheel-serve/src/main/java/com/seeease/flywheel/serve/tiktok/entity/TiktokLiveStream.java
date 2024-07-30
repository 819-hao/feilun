package com.seeease.flywheel.serve.tiktok.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="tiktok_live_stream")
@Data
public class TiktokLiveStream implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 作者id
     */
    private String authorId;
    /**
     * 作者名称
     */
    private String authorName;
    /**
     * 房间id
     */
    private String roomId;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 直播结束时
     */
    private Date endTime;
    /**
     * 曝光人数
     */
    private Integer exposureCount;
    /**
     * 观看人数
     */
    private Integer viewCount;
    /**
     * 点击率
     */
    private Double ctr;
    /**
     * 成交率
     */
    private Double cr;
    /**
     * 互动率
     */
    private Double er;
    /**
     * 平均停留时长
     */
    private Integer avgStayTime;
}
