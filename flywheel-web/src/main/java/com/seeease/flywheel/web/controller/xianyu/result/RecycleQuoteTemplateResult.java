package com.seeease.flywheel.web.controller.xianyu.result;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Tiro
 * @date 2023/10/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleQuoteTemplateResult extends QiMenBaseResult {
    private String template;
    private boolean supportPrepay = false;
}
