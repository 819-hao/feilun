package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/4/19 16:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAttachmentResult implements Serializable {

    private Boolean success;
}
