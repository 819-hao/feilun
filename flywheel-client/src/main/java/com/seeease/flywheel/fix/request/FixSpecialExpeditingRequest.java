package com.seeease.flywheel.fix.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/3 11:33
 */
@Data
public class FixSpecialExpeditingRequest implements Serializable {

    private List<Integer> fixIdList;

}
