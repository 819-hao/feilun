package com.seeease.flywheel.allocate.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/8/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateTaskCreateResult implements Serializable {

    /**
     * 失败结构
     */
    private List<String> errStockSnList;
}
