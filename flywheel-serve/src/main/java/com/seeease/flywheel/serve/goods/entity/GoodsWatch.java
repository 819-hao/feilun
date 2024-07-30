package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * 商品手表
 * @TableName goods_watch
 */
@TableName(value ="goods_watch")
@Data
public class GoodsWatch extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 系列id
     */
    private Integer seriesId;

    /**
     * 型号官方图
     */
    private String image;

    /**
     * 型号
     */
    private String model;
    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 公价
     */
    private BigDecimal pricePub;
    /**
     * 当前行情价
     */
    private BigDecimal currentPrice;
    /**
     * 20年行情价
     */
    private BigDecimal twoZeroFullPrice;
    /**
     * 22年行情价
     */
    private BigDecimal twoTwoFullPrice;
    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 性别
     */
    private String sex;

    /**
     * 别称
     */
    private String nickName;

    /**
     * 限量 上限9999
     */
    private Integer limited;

    /**
     * 针数 正整数，上限99
     */
    private Integer pinNumber;

    /**
     * 机芯型号 20字，非中文
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
     * 机芯印记 选择项、默认为无，还可以选1、2、3、4、5
     */
    private String mark;

    /**
     * 表径 正整数，上限99， 单位毫米
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
     * 表盘材质 金属表盘、贵金属表盘、珐琅盘、贝母盘、陨石盘、瓷盘、人工蓝宝石盘、其他
     */
    private String material;

    /**
     * 表扣材质:单选项，钢、银、钛、镀金、钢间金、18K黄金、18K白金、18K红金、铂金、钢间陶瓷、皮、其他瓷、钢和镀金   陶瓷、钛和金、陶瓷、钯金、橡胶、树脂和陶
     */
    private String claspMaterial;

    /**
     * 表壳材质:单选项，钢、银、钛、镀金、钢间金、18K黄金、18K白金、18K红金、铂金、钢间陶瓷、皮、其他瓷、钢和镀金   陶瓷、钛和金、陶瓷、钯金、橡胶、树脂和陶
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
     * 上市时间
     */
    private String listingDate;

    /**
     * 第几代
     */
    private String generations;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private String createBy;

    /**
     * 
     */
    private String updateBy;

    /**
     * 
     */
    private Integer delFlag;

    /**
     * 缩略去.的型号
     */
    private String simplifyModel;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}