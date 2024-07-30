package com.seeease.flywheel.serve.storework.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 入库事件
 * <p>目前只有总部操作</p>
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Getter
@AllArgsConstructor
public class InStorageEvent implements BillHandlerEvent {

    private List<BillStoreWorkPre> workPreList;

}
