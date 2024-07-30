package com.seeease.flywheel.web.common.express.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpressPlaceOrder implements Serializable {
    /**
     * 请求唯一标识
     */
    private String requestID;
    /**
     * 选择下单渠道
     */
    private ExpressChannelTypeEnum channelType;
    /**
     * 顺丰产品编码
     */
    private SFProductCodeEnum sfProductCode;
    /**
     * 订单信息
     */
    private OrderInfo orderInfo;
    /**
     * 业务订单号，下单唯一
     */
    private String businessNo;
    /**
     * 发件人信息
     */
    private ContactsInfo senderInfo;
    /**
     * 收件人信息
     */
    private ContactsInfo receiverInfo;

    /**
     * @return
     */
    public boolean checkFailed() {
        return Objects.isNull(channelType)
                || StringUtils.isBlank(businessNo)
                || Objects.isNull(orderInfo)
                || orderInfo.checkFailed()
                || Objects.isNull(senderInfo)
                || Objects.isNull(receiverInfo)
                || senderInfo.checkFailed()
                || receiverInfo.checkFailed();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactsInfo implements Serializable {
        /**
         * 公司名称
         */
        private String company;
        /**
         * 省
         */
        private String province;
        /**
         * 城市
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
         * 详细门牌号
         */
        private String addressDetail;
        /**
         * 联系人姓名
         */
        private String contactName;
        /**
         * 联系电话
         */
        private String contactTel;
        /**
         * 物流商标准模版信息
         */
        private String templateUrl;

        /**
         * 自定义模版信息
         */
        private String customTemplateUrl;

        /**
         * @return
         */
        private boolean checkFailed() {
            return StringUtils.isEmpty(this.getProvince())
                    || StringUtils.isEmpty(this.getCity())
                    || StringUtils.isEmpty(this.getTown())
                    || StringUtils.isEmpty(this.getAddressDetail())
                    || StringUtils.isEmpty(this.getContactName())
                    || StringUtils.isEmpty(this.getContactTel());
        }

    }


    /**
     * 订单信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderInfo implements Serializable {
        /**
         * 飞轮销售门店
         */
        private Integer saleShopId;
        /**
         * 销售备注
         */
        private String saleRemarks;
        /**
         * 订单号
         */
        private String orderNo;
        /**
         * 抖音门店id
         */
        private Long douYinShopId;

        /**
         * 抖音订单号
         */
        private String douYinOrderId;
        /**
         * 【合单情况数组】
         */
        private List<String> douYinOrderIds;

        /**
         * 快手门店id
         */
        private Long kuaiShouShopId;

        /**
         * 快手订单号
         */
        private String kuaiShouOrderId;
        /**
         * 【合单情况数组】
         */
        private List<String> kuaiShouOrderIds;

        /**
         * 商品信息
         */
        private List<GoodsInfo> goodsInfoList;

        private boolean checkFailed() {
            return StringUtils.isEmpty(this.getOrderNo())
                    || Objects.isNull(this.getSaleShopId());
        }
    }


    /**
     * 订单商品信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoodsInfo implements Serializable {
        /**
         * 商品描述
         */
        private String info;
        /**
         * 品牌
         */
        private String brandName;

        /**
         * 系列
         */
        private String seriesName;

        /**
         * 型号
         */
        private String model;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 商品编码
         */
        private String wno;
        /**
         * 质检码
         */
        private String btsCode;
    }

    //**********快手打印需要数据**************

    /**
     * 卖家id 打印需要
     */
    private String sellerOpenId;

    /**
     * 快手门店名称（sellerNick）
     */
    private String kuaiShouShopName;

    /**
     * 商品名称
     */
    private String itemTitle;


    //**********快手打印需要数据**************
}
