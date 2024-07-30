package com.seeease.flywheel.web.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/7/21
 */
@Data
public class BrandNotify  implements Serializable {
    private String name;
    private Integer number;
}
