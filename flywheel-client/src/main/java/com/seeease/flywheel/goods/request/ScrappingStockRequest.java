package com.seeease.flywheel.goods.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ScrappingStockRequest implements Serializable {


    private List<Integer> ids;

    private String scrapReason;
}
