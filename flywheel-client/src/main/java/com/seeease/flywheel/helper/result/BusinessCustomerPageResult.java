package com.seeease.flywheel.helper.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessCustomerPageResult implements Serializable {
    private Integer id;
    /**
     * 公司名称
     */
    private String firmName;
    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * 联系人电话
     */
    private String contactPhone;
    /**
     * 联系人区域
     */
    private String contactArea;
    /**
     * 联系人地址
     */
    private String contactAddress;
    /**
     * 区域id
     */
    private List<Integer> areaIds;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedTime;
    /**
     * 客户属性
     */
    private String prop;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 更新人
     */
    private String updatedBy;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 审批人
     */
    private String approverName;


    private String  areaIdsJson;
}
