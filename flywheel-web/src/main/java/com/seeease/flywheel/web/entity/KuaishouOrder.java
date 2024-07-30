package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 快手订单
 *
 * @TableName kuaishou_order
 */
@TableName(value = "kuaishou_order")
@Data
public class KuaishouOrder extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 快手门店id（userId）
     */
    private Long kuaiShouShopId;

    /**
     * 快手门店名称（sellerNick）
     */
    private String kuaiShouShopName;

    /**
     * 快手订单id
     */
    private String orderId;

    /**
     * 飞轮订单号
     */
    private String serialNo;

    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 订单状态（status）订单状态：[0, "未知状态"], [10, "待付款"], [30, "已付款"], [40, "已发货"], [50, "已签收"], [70, "订单成功"], [80, "订单失败"];
     * 订单取消后会转为“订单失败”状态
     */
    private Integer orderStatus;

    /**
     * 订单类型(cpsType)
     */
    private Integer orderType;

    /**
     * 活动类型，2="0元抽奖"，3="老铁团"，4="1分夺宝"，5="福袋抽奖"，6="定金预售"，7="暑期众筹"，17=“多人拼团”
     */
    private Integer activityType;

    /**
     * 支付方式 0:未知, 1:微信, 2:支付宝, 3:平安, 99:银行转账. 88:支付宝先用后付
     */
    private Integer payType;

    /**
     * 支付方式
     */
    private String payChannel;

    /**
     * [未知：0]；[券包：2]；[话费充值：3]；[跨境：8]
     */
    private Integer coType;

    /**
     * 订单渠道来源：[直播间:2] [短视频: 1] [直播回放:7] [订单详情页:311 ] [个人店铺页3、29 ] [搜索-商详页: 10008] [LIVE页面进入RECO:201] [买家端订单列表：311] [买家首页feed:478] [讲解回放：100010] [微信分享：值在10000-20000之间都属于]
     */
    private Integer carrierType;

    /**
     * 直播间id或短视频id，仅当订单渠道来源为直播间或短视频时有值
     */
    private Long carrierId;

    /**
     * 订单金额(totalFee)
     */
    private BigDecimal orderAmount;

    /**
     * 支付金额(totalFee)
     */
    private BigDecimal payAmount;

    /**
     * 支付时间(时间戳)
     */
    private Date payTime;

    /**
     * 买家留言(remark)
     */
    private String buyerWords;

    /**
     * 商家备注(,分割)
     */
    private String sellerWords;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区(district)
     */
    private String town;

    /**
     * 街道（todo）
     */
    private String street;

    /**
     * 密文收件人电话(encryptedMobile)
     */
    private String encryptPostTel;

    /**
     * 密文收件人姓名(encryptedConsignee)
     */
    private String encryptPostReceiver;

    /**
     * 密文收件地址省市区
     */
    private String encryptAddrArea;

    /**
     * 密文收件地址(encryptedAddress)
     */
    private String encryptDetail;

    /**
     * 解密收件人电话(todo)
     */
    private String decryptPostTel;

    /**
     * 解密收件人姓名(todo)
     */
    private String decryptPostReceiver;

    /**
     * 解密收件地址(encrypt_addr_area+todo)
     */
    private String decryptAddrDetail;

    /**
     * 脱敏收件人电话(desensitiseMobile)
     */
    private String maskPostTel;

    /**
     * 脱敏收件人姓名(desensitiseConsignee)
     */
    private String maskPostReceiver;

    /**
     * 脱敏收件地址(encrypt_addr_area+desensitiseAddress)
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
     * 审核状态
     */
    private Integer whetherUse;

    /**
     *
     */
    private Date usageTime;

    /**
     *
     */
    private Integer whetherQuery;

    /**
     * 是否通知同步 发布
     */
    private Integer whetherNotify;

    /**
     * 售卖人昵称(订单推广信息)
     */
    private String roleName;

    /**
     * 售卖人角色类型：[货主：1] [达人：2] [授权号：3]
     */
    private Integer roleType;

    /**
     * 售卖人id
     */
    private Long roleId;

    /**
     * 快手商品skuid
     */
    private Long skuId;

    /**
     * 服务商商品skuid
     */
    private Long relSkuId;

    /**
     * sku商品规格快照（型号
     */
    private String skuDesc;

    /**
     * sku编码(商品编码)唯一值
     */
    private String skuNick;

    /**
     * 商品id
     */
    private Long itemId;

    /**
     * 服务商商品id
     */
    private Long relItemId;

    /**
     * 商品链接
     */
    private String itemLinkUrl;

    /**
     * 商品名称
     */
    private String itemTitle;

    /**
     * 商品图片地址
     */
    private String itemPicUrl;

    /**
     * sku数量
     */
    private Integer num;

    /**
     * 商品促销前单价快照，单位为分
     */
    private BigDecimal originalPrice;

    /**
     * 折扣金额，单位为分
     */
    private BigDecimal discountFee;

    /**
     * 商品单价快照，单位为分
     */
    private BigDecimal price;

    /**
     * 1自建商品 2 闪电购商品
     */
    private Integer itemType;
    /**
     * 卖家id 打印需要
     */
    private String sellerOpenId;

    /**
     * 型号 快手和飞轮一致性通信
     */
    private String goodsModel;

    /**
     * 型号编码
     */
    private String modelCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}