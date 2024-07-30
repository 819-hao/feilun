package com.seeease.flywheel.account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 删除id
 * @Date create in 2023/7/18 16:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDeleteRequest implements Serializable {

    private List<Integer> list;
}
