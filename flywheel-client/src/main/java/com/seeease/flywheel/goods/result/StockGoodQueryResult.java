package com.seeease.flywheel.goods.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockGoodQueryResult implements Serializable {
    /**
     * 商品图片
     */

    private String image;

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
     * 型号编码
     */
    private String modelCode;
    /**
     * 表身号
     */

    private String sn;

    /**
     * 商品编号 XYW+8位阿拉伯数字
     */
    private String wno;

    /**
     * 商品id
     */
    private Integer id;
    /**
     * 供应商
     */
    private String customerName;
    /**
     * 供应商类别
     */
    private Integer customerType;
    /**
     * 库存来源
     */

    private Integer stockSrc;
    /**
     * 入库时间
     */
    private String rkTime;

    /**
     * 出库时间
     */
    private Date ckTime;

    /**
     * 成色 1。N级/全新、2.S级/99新未使用、3.SA级/98新未使用、4.A级/95新、5.AB级/9新
     */

    private String finess;
    /**
     * 附件
     */

    private String attachment;
    /**
     * 库存状态
     */
    private Integer stockStatus;
    /**
     * 公价
     */

    private BigDecimal pricePub;
    /**
     * 采购价格
     */

    private BigDecimal purchasePrice;
    /**
     * 加点
     */

    private BigDecimal addPrice;
    /**
     * 门店采购总部价格
     */

    private BigDecimal storePrice;
    /**
     * tob价
     */

    private BigDecimal tobPrice;

    /**
     * toc价
     */

    private BigDecimal tocPrice;

    /**
     * 吊牌价
     */

    private BigDecimal tagPrice;
    /**
     * 总价
     */

    private BigDecimal totalPrice;
    /**
     * 维修周期
     */

    private String fixDay;
    /**
     * 库龄
     */
    private String storageAge;
    /**
     * 总库龄
     */
    private String totalStorageAge;
    /**
     * 库位
     */
    private String storageLocation;
    /**
     * 备注
     */
    private String remark;
    /**
     * 商品归属
     */
    private String belongName;
    /**
     * 所处仓库(库存所属)
     */
    private String locationName;
    private Integer locationId;
    /**
     * 商品采购来源
     */
    private String purchaseSubject;
    private String tobActualPerformance;
    private String tocActualPerformance;
    private String sex;
    private String movement;
    private String watchSize;
    /**
     * 成交价格
     */
    private BigDecimal clinchPrice;

    /**
     * 策略建议 1.推荐门店销售  2.推荐商家销售
     */
    private String strategySuggestion;

    /**
     * 1.3.1 新增 响应日期返回
     */
    private String warrantyDate;

    /**
     * 是否有盒子 0 否
     */
    private Integer isBox;

    /**
     * 是否有保修卡 0 否
     */
    private Integer isCard;

    /**
     * 是否延期 0 否
     */
    private Integer isWarranty;

    /**
     * 是否有发票
     */
    private Integer isInvoice;

    /**
     * 是否有说明书
     */
    private Integer isInstruction;
    private String storageName;//库位名称
    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;
    /**
     * 经营权
     */
    private String managementOf;
    /**
     * 仓库id
     */
    private Integer storeId;
    /**
     * 仓库名称
     */
    private String storeName;

    /**
     * 需求门店名称
     */
    private String demandName;


    /**
     * 调入调出
     */
    private  String fromStore;

    private  String toStore;


    /**
     * 销售等级，1-仅B端销售、2-仅C端销售、0-B/C可同销
     */
    private Integer salesPriority;

    private Integer defectOrNot;

    private String defectDescription;

    private Integer useConfig;

    private Integer rightOfManagement;


    private Object attachmentLabel;

    private Object dictChildList;

    private String level;

    /**
     * 活动价
     */
    private BigDecimal promotionPrice;

    private String boxNumber;

    private Integer goodsId;
    /**
     * 前端用来折叠的id
     */
    private Integer parentId;

    /**
     * 商品总库存
     */
    private Integer stockCount = 1;

    public void convert(){
        if(Objects.isNull(this.tobPrice)){
            this.tobPrice = BigDecimal.ZERO;
        }
        if(Objects.isNull(this.tocPrice)){
            this.tocPrice = BigDecimal.ZERO;
        }
    }
}
