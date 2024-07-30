package com.seeease.flywheel.account.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountQueryRequest extends PageRequest implements Serializable {

    private String completeDateStart;

    private String completeDateEnd;

    private String companyName;

    private String shopName;

    private String accountGroup;

    private String accountType;

    private Integer pageType;
}
