package com.seeease.flywheel.serve.maindata.entity;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.type.JsonArrayTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 门店配额
 *
 * @TableName tag
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "store_quota",autoResultMap = true)
@Data
public class StoreQuota extends BaseDomain {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer smId;
    private Date startDate;
    private Date endDate;
    private Integer isCtl;
    @TableField(typeHandler = Line.Handler.class)
    private List<Line> ctLines;
    @TableField(typeHandler = Line.Handler.class)
    private List<Line> osLines;




    @Data
    public static class Line implements Serializable{
        /**
         * 品牌id
         */
        private Integer brandId;
        /**
         * 控制的额度
         */
        private BigDecimal quota;

        public static class Handler extends JsonArrayTypeHandler<Line> {

            public Handler(Class<List<Line>> clazz) {
                super(clazz);
            }

            @Override
            protected TypeReference<List<Line>> specificType() {
                return new TypeReference<List<Line>>() {
                };

            }
        }
    }
}