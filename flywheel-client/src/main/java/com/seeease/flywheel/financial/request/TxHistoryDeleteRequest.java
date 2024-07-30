package com.seeease.flywheel.financial.request;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author dmm
 */
@Data
@NoArgsConstructor
public class TxHistoryDeleteRequest implements Serializable {

    private List<Integer> idList;

}
