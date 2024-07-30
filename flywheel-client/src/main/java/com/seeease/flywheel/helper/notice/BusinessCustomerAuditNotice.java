package com.seeease.flywheel.helper.notice;

import com.seeease.flywheel.notify.entity.BaseNotice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCustomerAuditNotice extends BaseNotice {
    private Integer id;
}
