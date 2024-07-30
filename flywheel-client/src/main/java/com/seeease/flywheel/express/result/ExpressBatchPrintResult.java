package com.seeease.flywheel.express.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressBatchPrintResult implements Serializable {
    /**
     * 成功结果
     */
    private List<DdExpressPrintResult> douYinList;

    /**
     * 成功结果
     */
    private List<ExpressPrintResult> sfList;

    /**
     * 快手打印
     */
    private List<KsExpressPrintResult> kuaiShouList;

    /**
     * 失败结构
     */
    private List<String> errList;

}
