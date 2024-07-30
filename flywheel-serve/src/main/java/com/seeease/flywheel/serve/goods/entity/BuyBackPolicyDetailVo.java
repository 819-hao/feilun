package com.seeease.flywheel.serve.goods.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 回购政策详情表
 * @TableName buy_back_policy_detail
 */
@NoArgsConstructor
@AllArgsConstructor
public class BuyBackPolicyDetailVo implements Serializable {

    /**
     * 回购时间
     */
    private String buyBackTime;

    /**
     * 折扣
     */
    private BigDecimal discount;

    private Integer type;

    /**
     * 置换折扣
     */
    private BigDecimal replacementDiscounts;

    private BigDecimal priceThreshold;

    private static final long serialVersionUID = 1L;

    public String getBuyBackTime() {
        return buyBackTime;
    }

    public void setBuyBackTime(String buyBackTime) {
        this.buyBackTime = buyBackTime;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getReplacementDiscounts() {
        return replacementDiscounts;
    }

    public void setReplacementDiscounts(BigDecimal replacementDiscounts) {
        this.replacementDiscounts = replacementDiscounts;
    }

    public BigDecimal getPriceThreshold() {
        return priceThreshold;
    }

    public void setPriceThreshold(BigDecimal priceThreshold) {
        this.priceThreshold = priceThreshold;
    }

    @Override
    public String toString() {
        return "{" +
                "buyBackTime='" + buyBackTime + '\'' +
                ", discount=" + discount +
                ", type=" + type +
                ", replacementDiscounts=" + replacementDiscounts +
                ", priceThreshold=" + priceThreshold +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuyBackPolicyDetailVo that = (BuyBackPolicyDetailVo) o;
        return buyBackTime.equals(that.buyBackTime) && discount.equals(that.discount) && type.equals(that.type) && replacementDiscounts.equals(that.replacementDiscounts) && priceThreshold.equals(that.priceThreshold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buyBackTime, discount, type, replacementDiscounts, priceThreshold);
    }
}