package com.seeease.flywheel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 导入参数
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Data
public class ImportRequest<T> implements Serializable {
    /**
     * 导入数据
     */
    List<T> dataList;

}
