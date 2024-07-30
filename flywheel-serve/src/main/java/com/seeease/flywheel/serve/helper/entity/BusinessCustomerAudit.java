package com.seeease.flywheel.serve.helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.helper.enmus.BusinessCustomerAuditStatusEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.*;

/**
 * @ Description   :  小程序b端客户审核表
 * @ Author        :  西门 游
 * @ CreateDate    :  9/11/23
 * @ Version       :  1.0
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "app_business_customer_audit", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusinessCustomerAudit extends BaseDomain implements TransitionStateEntity {

    @TableId(type = IdType.AUTO)
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
     * 地区id列表
     */
    private String areaIds;
    @TransitionState
    private BusinessCustomerAuditStatusEnum status;
    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
    /**
     * 客户属性
     */
    private String prop;
    /**
     * 审批人id
     */
    private Integer approver;
}
