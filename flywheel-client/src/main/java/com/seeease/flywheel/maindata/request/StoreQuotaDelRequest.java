package com.seeease.flywheel.maindata.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreQuotaDelRequest implements Serializable{

    /**
     *
     */
    private Integer id;

}
