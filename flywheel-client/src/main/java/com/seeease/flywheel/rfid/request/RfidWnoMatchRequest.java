package com.seeease.flywheel.rfid.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidWnoMatchRequest implements Serializable {
    /**
     * 商品编码列表
     */
    private List<String> wnoList;


}
