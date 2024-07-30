package com.seeease.flywheel.account.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 15:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountQueryImportResult implements Serializable {

//    private List<Integer> list;

    private Integer id;

//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class AccountQueryImportResultDTO implements Serializable {
//
//    }

}
