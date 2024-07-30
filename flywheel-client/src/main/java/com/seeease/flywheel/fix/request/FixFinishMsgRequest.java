package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修完成通知
 * @Date create in 2023/11/15 15:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixFinishMsgRequest implements Serializable {

    private String serialNo;

}
