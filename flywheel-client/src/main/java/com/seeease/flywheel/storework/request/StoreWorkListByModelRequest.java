package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/4 11:24
 */
@Data
public class StoreWorkListByModelRequest extends StoreWorkListRequest{

    private List<String> list;
}
