package com.seeease.flywheel.serve.financial.enums;

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
public enum MaycurStatusEnum implements IEnum<String> {

    //对应飞轮 未开票
    DELETED("DELETED", "已删除"),
    ABANDON("ABANDON", "已作废"),
    REJECTED("REJECTED", "被驳回"),
    SUBMIT_FAILED("SUBMIT_FAILED", "提交失败"),
    DRAFT("DRAFT", "草稿"),
    CLOSED("CLOSED", "已停用"),

    //对应飞轮 开票中
    APPROVING("APPROVING", "审批中"),
    AUDITING("AUDITING", "审核中"),
    CHANGED("CHANGED", "已变更"),

    //对应飞轮已开票
    COMPLETED("COMPLETED", "已完成"),

    ;
    private String value;
    private String desc;

    public static MaycurStatusEnum fromCode(String value) {
        return Arrays.stream(MaycurStatusEnum.values())
                .filter(t -> t.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
