package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class InExceptionStockRequest implements Serializable {


    private List<Integer> ids;
}
