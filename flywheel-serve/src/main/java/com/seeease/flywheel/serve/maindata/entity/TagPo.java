package com.seeease.flywheel.serve.maindata.entity;

import lombok.Data;

/**
 * 标签
 * @TableName tag
 */
@Data
public class TagPo extends Tag{
   private Integer storeId;
   private String tagName;
}