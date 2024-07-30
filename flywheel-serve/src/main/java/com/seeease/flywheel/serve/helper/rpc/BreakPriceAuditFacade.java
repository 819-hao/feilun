package com.seeease.flywheel.serve.helper.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.IBreakPriceAuditFacade;
import com.seeease.flywheel.helper.request.BreakPriceAuditPageRequest;
import com.seeease.flywheel.helper.request.BreakPriceAuditSubmitRequest;
import com.seeease.flywheel.helper.request.BreakPriceAuditRequest;
import com.seeease.flywheel.helper.result.BreakPriceAuditHistoryResult;
import com.seeease.flywheel.helper.result.BreakPriceAuditPageResult;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.helper.convert.BreakPriceAuditConvert;
import com.seeease.flywheel.serve.helper.convert.BreakPriceAuditHistoryConvert;
import com.seeease.flywheel.serve.helper.enmus.BreakPriceAuditStatusEnum;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAudit;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAuditHistory;
import com.seeease.flywheel.serve.helper.service.BreakPriceAuditHistoryService;
import com.seeease.flywheel.serve.helper.service.BreakPriceAuditService;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@DubboService(version = "1.0.0")
public class BreakPriceAuditFacade implements IBreakPriceAuditFacade {


    @Resource
    private BreakPriceAuditService breakPriceAuditService;
    @Resource
    private BreakPriceAuditHistoryService breakPriceAuditHistoryService;
    @Resource
    private StockService stockService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer submit(BreakPriceAuditSubmitRequest request) {
        BreakPriceAudit entity = BreakPriceAuditConvert.INSTANCE.to(request);
        entity.setStatus(BreakPriceAuditStatusEnum.WAIT);
        if (entity.getId() == null){
            entity.setShopId(UserContext.getUser().getStore().getId());
            entity.setSerial(SerialNoGenerator.generateBreakCPriceSerialNo());
        }else {
            BreakPriceAudit byId = breakPriceAuditService.getById(request.getId());
            entity.setCreatedTime(new Date());
            Assert.isTrue(byId.getStatus() != BreakPriceAuditStatusEnum.OK,"成功的记录无法编辑");
        }
        breakPriceAuditService.saveOrUpdate(entity);
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void audit(BreakPriceAuditRequest request) {
        breakPriceAuditService.listByIds(request.getIds()).stream()
                .filter(v-> v.getStatus() != BreakPriceAuditStatusEnum.WAIT)
                .findFirst().ifPresent(v-> {
                    throw new IllegalArgumentException("存在无法审核的记录");
                });

        BreakPriceAuditStatusEnum statusEnum = BreakPriceAuditStatusEnum.of(request.getStatus());
        List<BreakPriceAudit> auditRecord = request
                .getIds()
                .stream()
                .map(id -> BreakPriceAudit.builder().id(id).status(statusEnum).failReason(request.getFailReason()).build())
                .collect(Collectors.toList());
        breakPriceAuditService.updateBatchById(auditRecord);

        List<Integer> stockIds = breakPriceAuditService.listByIds(request.getIds()).stream().map(BreakPriceAudit::getStockId).collect(Collectors.toList());
        stockService.updateUnderselling(
                stockIds,
                request.getStatus().equals(BreakPriceAuditStatusEnum.OK.getValue()) ? StockUndersellingEnum.ALLOW : StockUndersellingEnum.NOT_ALLOW
        );


        List<BreakPriceAuditHistory> history = auditRecord.stream().map(record -> {
            return BreakPriceAuditHistory.builder()
                    .auditId(record.getId())
                    .changeStatus(record.getStatus())
                    .failReason(record.getFailReason())
                    .build();
        }).collect(Collectors.toList());

        breakPriceAuditHistoryService.saveBatch(history);


    }

    @Override
    public PageResult<BreakPriceAuditPageResult> pageOf(BreakPriceAuditPageRequest request) {
        Integer shopId = UserContext.getUser().getStore().getId();
        if (shopId != FlywheelConstant._ZB_ID){
            request.setShopId(shopId);
        }
        Page<BreakPriceAuditPageResult> page = breakPriceAuditService.pageOf(request);

        page.getRecords().forEach(v->{
            List<BreakPriceAuditHistoryResult> history = history(v.getId());
            if (history.size() == 1){
                v.setUpdatedBy("");
                v.setUpdatedTime("");
            }else {
                for (BreakPriceAuditHistoryResult his : history){
                    if (!his.getCreatedBy().equals(v.getUpdatedBy())){
                        v.setUpdatedBy(his.getCreatedBy());
                        v.setUpdatedTime(his.getCreatedTime());
                        break;
                    }
                }
            }
        });


        return PageResult.<BreakPriceAuditPageResult>builder()
                .totalPage(page.getPages())
                .totalCount(page.getTotal())
                .result(page.getRecords())
                .build();
    }

    @Override
    public List<BreakPriceAuditHistoryResult> history(Integer auditId) {

        LambdaQueryWrapper<BreakPriceAuditHistory> wq = Wrappers.<BreakPriceAuditHistory>lambdaQuery()
                .eq(BreakPriceAuditHistory::getAuditId, auditId)
                .orderByDesc(BreakPriceAuditHistory::getId);
        List<BreakPriceAuditHistory> list = breakPriceAuditHistoryService.list(wq);
        return BreakPriceAuditHistoryConvert.INSTANCE.toList(list);
    }
}
