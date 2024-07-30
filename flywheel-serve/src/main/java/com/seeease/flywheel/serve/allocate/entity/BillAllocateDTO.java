package com.seeease.flywheel.serve.allocate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillAllocateDTO implements Serializable {
    private BillAllocate allocate;
    private List<BillAllocateLine> lines;
}
