package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StockGoodQueryRequest extends PageRequest {

    /**
     * 是否活动
     */
    private Integer whetherPromotion;

    private List<Integer> idList;
    /**
     * 入库开始时间
     */
    private String beginCreateTime;
    /**
     * 入库结束时间
     */
    private String endCreateTime;
    /**
     * 所处仓库(库存所属)
     */
    private Integer locationId;


    /**
     * 商品归属
     */
    private Integer belongId;

    /**
     * 库存来源
     */
    private Integer stockSrc;

    /**
     * 型号
     */
    private String model;
    /**
     * 库存来源
     */
    private Integer stockSourceType;


    /**
     * 成色
     */
    private String finess;
    /**
     * 品牌
     */
    private List<String> brandName;

    private String seriesName;
    /**
     * 表身号
     */
    private String sn;

    /**
     * 库存状态
     */
    private Integer stockStatus;
    /**
     * 库龄
     */
    private Integer stockAge;


    /**
     * 库位
     */
    private Integer storageLocation;

    /**
     * 是否查询借货商品
     */
    private Boolean queryBatchBorrowing = false;

    /**
     * 移动端综合查询
     */
    private String phoneComprehensiveSearch;


    /**
     * 移动端成色查询
     */
    private String[] phoneFiness;

    /**
     * 移动端库存来源查询
     */
    private Integer[] phoneStockSrc;

    /**
     * 移动端公价区间查询
     */
    private List<BigDecimal> phonePricePub;

    /**
     * 移动端吊牌价区间查询
     */
    private List<BigDecimal> phoneTagPrice;

    /**
     * 移动端b端价格区间查询
     */
    private List<BigDecimal> phonePriceB;

    /**
     * 移动端c端价格区间查询
     */
    private List<BigDecimal> phonePriceC;

    /**
     * 移动端可售|不可售状态查询
     */
    private Integer phoneIsSale;

    /**
     * 商品编号 XYW+8位阿拉伯数字
     */
    private String wno;

    /**
     * 开始库龄
     */
    private String beginStorageAge;

    /**
     * 结束库龄
     */
    private String endStorageAge;

    private BigDecimal minToBPriceRange;
    private BigDecimal maxToBPriceRange;
    private BigDecimal minToCPriceRange;
    private BigDecimal maxToCPriceRange;
    private BigDecimal minTagPriceRange;
    private BigDecimal maxTagPriceRange;


    private Integer offset;
    private Integer batchType;
    private Date startDateTime;
    private Date endDateTime;

    /**
     * 1.3.3 门店库龄 性别
     */
    private Integer beginStoreAge;

    private Integer endStoreAge;
    /**
     * 性别
     */
    private String sex;
    //经营权
    private Integer rightOfManagement;
    private Integer storeId;
    /**
     * 需求门店id
     */
    private Integer demandId;

    /**
     * 在途查询标记
     */
    private Integer logisticsType;

    /**
     * 商品分级
     */
    private String level;

    /**
     * TOB_C(0, "B/C可同销"),
     * TOB(1, "仅B端销售"),
     * TOC(2, "仅C端销售"),
     */
    private Integer salesPriority;

    private boolean visibleShop;

    private String boxNumber;

    /**
     * 是否根据goods_id进行分组。用来商品折叠
     */
    private Boolean groupingGoodsId = Boolean.TRUE;

    /**
     * goods_id
     */
    private Integer goodsId;

    /**
     * 商品id
     */
    private Integer id;
    /**
     * 表壳材质
     */
    private String watchcaseMaterial;

    /**
     * 表冠材质
     */
    private String headMaterial;

    /**
     * 使用场景
     */
    private UseScenario useScenario;

    public enum UseScenario {
        /**
         * 查询库存
         */
        INVENTORY_LIST,

        /**
         * 商品列表
         */
        GOODS_LIST;
    }
}
