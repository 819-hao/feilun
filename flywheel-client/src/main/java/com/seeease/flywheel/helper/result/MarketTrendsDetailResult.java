package com.seeease.flywheel.helper.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class MarketTrendsDetailResult implements Serializable {

    /**
     * 评论列表
     */
    private List<Comment> comments;
    /**
     * 采购趋势
     */
    private List<PriceTrends> purchaseTrends;
    /**
     * 销售趋势
     */
    private List<PriceTrends> saleTrends;

    @Builder
    @Data
    public static class PriceTrends implements Serializable{
        /**
         * 日期
         */
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private Date date;
        /**
         * 单表金额
         */
        private String priceSingle = "0";
        /**
         * 20年全套金额
         */
        private String price20 = "0";
        /**
         * 21年全套金额
         */
        private String price21 = "0";
        /**
         * 22年全套金额
         */
        private String price22 = "0";
    }
    @Builder
    @Data
    public static class Comment implements Serializable{
        /**
         * 姓名
         */
        private String userName;
        /**
         * 头像
         */
        private String avatar;
        /**
         * 评论内容
         */
        private String content;
        /**
         * 创建时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;
    }
}
