package com.seeease.flywheel.serve.sale.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Getter
@AllArgsConstructor
public enum SaleOrderChannelEnum implements IEnum<Integer> {
    T_MALL(2, "天猫国际"),
    DOU_YIN(3, "抖音"),
    STORE(4, "门店"),
    SI_YU(8, "私域"),
    XI_YI_SHOP(14, "稀蜴商城"),
    JD(15, "京东"),
    XIAO_HONG_SHU(16, "小红书"),
    ALIPAY(17, "支付宝"),
    KUAI_SHOU(18, "快手"),
    PEER(19, "同行"),
    OTHER(0, "其它"),
    XIAN_YU(20, "闲鱼"),
    TAO_BAO(21, "淘宝"),
    WY_APP(23,"物鱼APP"),
    ;
    private Integer value;
    private String desc;

    public static SaleOrderChannelEnum fromCode(int value) {
        return Arrays.stream(SaleOrderChannelEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
