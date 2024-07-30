package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import java.io.Serializable;
import lombok.Data;


/**
 * 抖音门店映射表
 * @TableName douyin_print_mapping
 */
@TableName(value ="douyin_print_mapping")
@Data
public class DouyinPrintMapping extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 抖音门店id
     */
    private Integer douYinShopId;

    /**
     * 飞轮门店id
     */
    private Integer shopId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区/县名称
     */
    private String districtName;

    /**
     * 街道名称
     */
    private String streetName;

    /**
     * 剩余详细地址
     */
    private String detailAddress;

    /**
     * 寄件人姓名
     */
    private String name;

    /**
     * 寄件人移动电话
     */
    private String mobile;

    /**
     * 物流商标准模版信息
     */
    private String templateUrl;

    /**
     * 自定义模版信息
     */
    private String customTemplateUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DouyinPrintMapping other = (DouyinPrintMapping) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDouYinShopId() == null ? other.getDouYinShopId() == null : this.getDouYinShopId().equals(other.getDouYinShopId()))
            && (this.getShopId() == null ? other.getShopId() == null : this.getShopId().equals(other.getShopId()))
            && (this.getProvinceName() == null ? other.getProvinceName() == null : this.getProvinceName().equals(other.getProvinceName()))
            && (this.getCityName() == null ? other.getCityName() == null : this.getCityName().equals(other.getCityName()))
            && (this.getDistrictName() == null ? other.getDistrictName() == null : this.getDistrictName().equals(other.getDistrictName()))
            && (this.getStreetName() == null ? other.getStreetName() == null : this.getStreetName().equals(other.getStreetName()))
            && (this.getDetailAddress() == null ? other.getDetailAddress() == null : this.getDetailAddress().equals(other.getDetailAddress()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getMobile() == null ? other.getMobile() == null : this.getMobile().equals(other.getMobile()))
            && (this.getTemplateUrl() == null ? other.getTemplateUrl() == null : this.getTemplateUrl().equals(other.getTemplateUrl()))
            && (this.getCustomTemplateUrl() == null ? other.getCustomTemplateUrl() == null : this.getCustomTemplateUrl().equals(other.getCustomTemplateUrl()))
            && (this.getRevision() == null ? other.getRevision() == null : this.getRevision().equals(other.getRevision()))
            && (this.getCreatedId() == null ? other.getCreatedId() == null : this.getCreatedId().equals(other.getCreatedId()))
            && (this.getCreatedBy() == null ? other.getCreatedBy() == null : this.getCreatedBy().equals(other.getCreatedBy()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getUpdatedId() == null ? other.getUpdatedId() == null : this.getUpdatedId().equals(other.getUpdatedId()))
            && (this.getUpdatedBy() == null ? other.getUpdatedBy() == null : this.getUpdatedBy().equals(other.getUpdatedBy()))
            && (this.getUpdatedTime() == null ? other.getUpdatedTime() == null : this.getUpdatedTime().equals(other.getUpdatedTime()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
                }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDouYinShopId() == null) ? 0 : getDouYinShopId().hashCode());
        result = prime * result + ((getShopId() == null) ? 0 : getShopId().hashCode());
        result = prime * result + ((getProvinceName() == null) ? 0 : getProvinceName().hashCode());
        result = prime * result + ((getCityName() == null) ? 0 : getCityName().hashCode());
        result = prime * result + ((getDistrictName() == null) ? 0 : getDistrictName().hashCode());
        result = prime * result + ((getStreetName() == null) ? 0 : getStreetName().hashCode());
        result = prime * result + ((getDetailAddress() == null) ? 0 : getDetailAddress().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getMobile() == null) ? 0 : getMobile().hashCode());
        result = prime * result + ((getTemplateUrl() == null) ? 0 : getTemplateUrl().hashCode());
        result = prime * result + ((getCustomTemplateUrl() == null) ? 0 : getCustomTemplateUrl().hashCode());
        result = prime * result + ((getRevision() == null) ? 0 : getRevision().hashCode());
        result = prime * result + ((getCreatedId() == null) ? 0 : getCreatedId().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getUpdatedId() == null) ? 0 : getUpdatedId().hashCode());
        result = prime * result + ((getUpdatedBy() == null) ? 0 : getUpdatedBy().hashCode());
        result = prime * result + ((getUpdatedTime() == null) ? 0 : getUpdatedTime().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", douYinShopId=").append(douYinShopId);
        sb.append(", shopId=").append(shopId);
        sb.append(", provinceName=").append(provinceName);
        sb.append(", cityName=").append(cityName);
        sb.append(", districtName=").append(districtName);
        sb.append(", streetName=").append(streetName);
        sb.append(", detailAddress=").append(detailAddress);
        sb.append(", name=").append(name);
        sb.append(", mobile=").append(mobile);
        sb.append(", templateUrl=").append(templateUrl);
        sb.append(", customTemplateUrl=").append(customTemplateUrl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}