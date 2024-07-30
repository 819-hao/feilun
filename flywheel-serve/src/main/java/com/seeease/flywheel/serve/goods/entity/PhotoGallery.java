package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName photo_gallery
 */
@TableName(value ="photo_gallery")
@Data
public class PhotoGallery implements Serializable {
    /**
     * 图片库列表ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 图片url
     */
    private String imgUrl;

    /**
     * 商品型号
     */
    private String stockModel;

    /**
     * 商品表身号
     */
    private String stockSn;

    /**
     * 图片库状态(默认0:启动，1:关闭)
     */
    private Integer status;

    /**
     * 子类id
     */
    private Integer childId;

    /**
     * 列表id
     */
    private Integer dicId;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}