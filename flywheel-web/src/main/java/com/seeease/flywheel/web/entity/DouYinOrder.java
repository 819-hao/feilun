package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.web.entity.enums.WhetherNotifyEnum;
import com.seeease.flywheel.web.entity.enums.WhetherUseEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 抖音订单
 *
 * @TableName douyin_order
 */
@TableName(value = "douyin_order")
@Data
public class DouYinOrder extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 抖音门店id
     */
    private Long douYinShopId;
    /**
     * 门店id
     */
    private Integer shopId;
    /**
     * 是否已经使用
     */
    private WhetherUseEnum whetherUse;
    /**
     * 是否查询抽检码
     */
    private Integer whetherQuery;
    /**
     * 抖音门店名称
     */
    private String douYinShopName;

    /**
     * 抖音订单id
     */
    private String orderId;

    /**
     * 飞轮单号
     */
    private String serialNo;

    /**
     * 订单状态
     */
    private Long orderStatus;

    /**
     * 订单状态描述
     */
    private String orderStatusDesc;

    /**
     * 订单类型
     */
    private Long orderType;

    /**
     * 订单类型描述
     */
    private String orderTypeDesc;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 支付类型
     */
    private Long payType;

    /**
     * 支付渠道的流水号
     */
    private String channelPaymentNo;

    /**
     * 买家留言
     */
    private String buyerWords;

    /**
     * 商家备注
     */
    private String sellerWords;

    /**
     * 开放平台地址id
     */
    private String openAddressId;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String town;

    /**
     * 街道
     */
    private String street;

    /**
     * 密文收件人电话
     */
    private String encryptPostTel;

    /**
     * 密文收件人姓名
     */
    private String encryptPostReceiver;

    /**
     * 密文收件地址省市区
     */
    private String encryptAddrArea;

    /**
     * 密文收件地址
     */
    private String encryptDetail;

    /**
     * 解密收件人电话
     */
    private String decryptPostTel;

    /**
     * 解密收件人姓名
     */
    private String decryptPostReceiver;

    /**
     * 解密收件地址
     */
    private String decryptAddrDetail;

    /**
     * 脱敏收件人电话
     */
    private String maskPostTel;

    /**
     * 脱敏收件人姓名
     */
    private String maskPostReceiver;

    /**
     * 脱敏收件地址
     */
    private String maskDetail;

    /**
     * 取消时间
     */
    private Date cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 是否通知同步
     */
    private WhetherNotifyEnum whetherNotify;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}