package com.seeease.flywheel.recycle.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/19 10:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RecycleReplaceUserRequest implements Serializable {

    private Integer id;

    private Integer demandId;

    private Integer userId;

}
