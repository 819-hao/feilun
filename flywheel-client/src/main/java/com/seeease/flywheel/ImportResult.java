package com.seeease.flywheel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 导入结果
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult<T> implements Serializable {
    /**
     * 成功结果
     */
    private List<T> successList;
    /**
     * 失败结构
     */
    private List<String> errList;

}
