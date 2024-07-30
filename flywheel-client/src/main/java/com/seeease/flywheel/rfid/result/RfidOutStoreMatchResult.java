package com.seeease.flywheel.rfid.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidOutStoreMatchResult implements Serializable {

    /**
     * 匹配到的数据
     */
    private List<Match> matched = new LinkedList<>();
    /**
     * 未匹配到的数据
     */
    private List<String> unmatched = new LinkedList<>();


    @Data
    @Builder
    public static class Match implements Serializable{
        private String brand;
        private String model;
        private Integer count;
    }

}
