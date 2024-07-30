package com.seeease.flywheel.serve.goods.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/2/8
 */
@Getter
@AllArgsConstructor
public enum StockCardEnum implements IEnum<Integer> {
    KEY_CARD(1, "身份卡(%s)"),
    CARD(1, "保卡(%s)"),
    BLANK_CARD(2, "空白保卡"),
    ;
    private Integer value;
    private String desc;

    /**
     * 保卡规则
     *
     * @param isCard
     * @param warrantyDate
     * @return
     */
    public static String joinCard(Integer isCard, String warrantyDate, Integer seriesType) {
        if (Objects.isNull(isCard)) {
            return StringUtils.EMPTY;
        } else if (StockCardEnum.CARD.getValue() == isCard) {
            return String.format(SeriesTypeEnum.BAGS.getValue().equals(seriesType) ? StockCardEnum.KEY_CARD.getDesc() : StockCardEnum.CARD.getDesc(), warrantyDate);
        } else if (StockCardEnum.BLANK_CARD.getValue() == isCard) {
            return StockCardEnum.BLANK_CARD.getDesc();
        }
        return StringUtils.EMPTY;
    }
}
