package com.seeease.flywheel.serve.storework.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 出库补充表身号事件
 *
 * @author Tiro
 * @date 2023/3/22
 */
@Getter
@AllArgsConstructor
public class OutStorageSupplyStockEvent implements BillHandlerEvent {

    private List<BillStoreWorkPre> outWorkList;

}