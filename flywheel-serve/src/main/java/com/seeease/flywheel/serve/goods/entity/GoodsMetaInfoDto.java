package com.seeease.flywheel.serve.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@Data
public class GoodsMetaInfoDto implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品状态
     */
    private Integer stockStatus;

    /**
     * 成色
     */
    private String finess;

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
     * 附件
     */
    private String attachment;


    /**
     * store_management 所在地 ID 急售商品所在地
     */
    private Long locationId;

    /**
     *
     */
    private String locationName;


    /**
     * 经营权（门店ID）
     */
    private Long rightOfManagement;


    /**
     * 锁门店表
     */
    private Integer lockDemand;

    /**
     * 是否有瑕疵
     */
    private Integer defectOrNot;

    /**
     * 瑕疵说明
     */
    private String defectDescription;


    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 类型
     */
    private Integer seriesType;

    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 型号
     */
    private String model;

    /**
     * 型号官方图
     */
    private String modelImage;

    /**
     * 性别
     */
    private String sex;

    /**
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 别称
     */
    private String nickName;

    /**
     * 限量
     */
    private Integer limited;

    /**
     * 针数
     */
    private Integer pinNumber;

    /**
     * 机芯型号
     */
    private String movementModel;

    /**
     * 出厂厂商
     */
    private String manufacturer;

    /**
     * 技术认证
     */
    private String technologyCertification;

    /**
     * 摆轮
     */
    private String balanceWheel;

    /**
     * 机芯宝石数
     */
    private String gemsNum;

    /**
     * 振频
     */
    private String vibrationFrequency;

    /**
     * 机芯印记
     */
    private String mark;

    /**
     * 表径
     */
    private String watchSize;

    /**
     * 表背样式
     */
    private String backThrough;

    /**
     * 重量
     */
    private String weight;

    /**
     * 场合
     */
    private String occasion;

    /**
     * 表盘形状
     */
    private String shape;

    /**
     * 表盘颜色
     */
    private String color;

    /**
     * 表带颜色
     */
    private String braceletColor;

    /**
     * 腕周
     */
    private String week;

    /**
     * 防水深度
     */
    private String depth;

    /**
     * 防水类型
     */
    private String waterproof;

    /**
     * 表盘材质
     */
    private String material;

    /**
     * 表扣材质
     */
    private String claspMaterial;

    /**
     * 表壳材质
     */
    private String watchcaseMaterial;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 表冠材质
     */
    private String headMaterial;

    /**
     * 表镜材质
     */
    private String crystalMaterial;

    /**
     * 时标类型
     */
    private String timescaleType;

    /**
     * 表耳间距
     */
    private String watchEarSpacing;

    /**
     * 表扣类型
     */
    private String buckleType;

    /**
     * 宝石镶嵌
     */
    private String gemsMosaic;

    /**
     * 动力储备
     */
    private String powerReserve;

    /**
     * 功能
     */
    private String capacity;

    /**
     * 特点
     */
    private String trait;

    /**
     * 缩略去.的型号
     */
    private String simplifyModel;

    /**
     * 是否新表:1-全新表，2-非全新表
     */
    private Integer brandNew;

    /**
     * 图片库
     */
    private String images;

    /**
     * 销售优先等级:1=仅B端销售,0=B/C可同销,2=仅C端销售
     */
    private String salesPriority;

    /**
     * 商品分级:G1,G2,G3
     */
    private String goodsLevel;

    /**
     * 商品编号：XYW+8位阿拉伯数字
     */
    private String wno;

    /**
     * 箱包-款式
     */
    private String bagStyle;

    /**
     * 箱包-材质
     */
    private String bagMaterial;

    /**
     * 箱包-尺寸规格
     */
    private String bagSize;

    /**
     * 箱包-颜色
     */
    private String bagColor;

    /**
     * 箱包-重量
     */
    private String bagWeight;

    /**
     * 箱包-开合方式
     */
    private String bagOcMode;

    /**
     * 箱包-内部结构
     */
    private String bagStructure;

    /**
     * 箱包-产地
     */
    private String bagOrigin;

    /**
     * 饰品-款式
     */
    private String jewelsStyle;

    /**
     * 饰品-材质
     */
    private String jewelsMaterial;

    /**
     * 饰品-尺寸规格
     */
    private String jewelsSize;

    /**
     * 饰品-重量
     */
    private String jewelsWeight;

    /**
     * 饰品-镶嵌材质
     */
    private String jewelsSetMaterial;
}