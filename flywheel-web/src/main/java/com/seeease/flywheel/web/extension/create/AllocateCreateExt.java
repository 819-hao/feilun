package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.allocate.result.AllocateCreateResult;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.request.StockExt1;
import com.seeease.flywheel.maindata.IStoreFacade;
import com.seeease.flywheel.maindata.result.TransferUsableQuotaQueryResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 调拨创建
 *
 * @author Tiro
 * @date 2023/3/8
 */
@Service
@Slf4j
@Extension(bizId = BizCode.ALLOCATE, useCase = UseCase.PROCESS_CREATE)
public class AllocateCreateExt implements CreateExtPtI<AllocateCreateRequest, AllocateCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IAllocateFacade allocateFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStoreFacade storeFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade stockFacade;



    @Override
    public AllocateCreateResult create(CreateCmd<AllocateCreateRequest> cmd) {
        return allocateFacade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(AllocateCreateRequest request, AllocateCreateResult result) {
        return result.getAllocateDtoList()
                .stream()
                .map(t -> {
                    Map<String, Object> workflowVar = new HashMap<>();
                    workflowVar.put(VariateDefinitionKeyEnum.ALLOCATE_WORK_SERIAL_JSON_LIST.getKey(),
                            t.getWorkSerialNoList()
                                    .stream()
                                    .map(i -> {
                                        JSONObject obj = new JSONObject();
                                        obj.put(VariateDefinitionKeyEnum.CK_SERIAL_NO.getKey(), i.getCkSerialNo());
                                        obj.put(VariateDefinitionKeyEnum.RK_SERIAL_NO.getKey(), i.getRkSerialNo());
                                        return obj;
                                    })
                                    .collect(Collectors.toList()));
                    workflowVar.put(VariateDefinitionKeyEnum.FROM_SHORTCODES.getKey(), t.getFromShopShortcodes());
                    workflowVar.put(VariateDefinitionKeyEnum.TO_SHORTCODES.getKey(), t.getToShopShortcodes());
                    return ProcessInstanceStartDto.builder()
                            .serialNo(t.getSerialNo())
                            .process(AllocateType.getKey(t.getAllocateType()))
                            .variables(workflowVar)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(AllocateCreateRequest request, AllocateCreateResult result) {
        List<StockLifeCycleResult> res = result.getAllocateDtoList()
                .stream()
                .map(t -> t.getStockIdList()
                        .stream()
                        .map(id -> StockLifeCycleResult.builder()
                                .stockId(id)
                                .originSerialNo(t.getSerialNo())
                                .operationDesc(OperationDescConst.ALLOCATE_CREATE)
                                .build())
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        try {
            Map<Integer, AllocateCreateRequest.AllocateLineDto> map = request.getDetails().stream().collect(Collectors.toMap(AllocateCreateRequest.AllocateLineDto::getStockId, Function.identity()));
            res.forEach(t -> {
                AllocateCreateRequest.AllocateLineDto dto = map.get(t.getStockId());
                if (dto.getWhetherCardManage() == 1 && dto.getGuaranteeCardManage() == 0) {
                    t.setOperationDesc(OperationDescConst.ALLOCATE_CREATE + "(保卡未调出)");
                } else if (dto.getWhetherCardManage() == 1 && dto.getGuaranteeCardManage() == 1) {
                    t.setOperationDesc(OperationDescConst.ALLOCATE_CREATE + "(保卡调出)");
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

    @Override
    public Class<AllocateCreateRequest> getRequestClass() {
        return AllocateCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<AllocateCreateRequest> cmd) {

        Assert.notNull(cmd, "参数不能为空");
        Assert.notNull(cmd.getRequest().getAllocateType(), "调入类型不能为空");
        Assert.notNull(AllocateType.getKey(cmd.getRequest().getAllocateType()), "调入类型不能为空");
        Assert.notNull(cmd.getRequest().getToId(), "调入方不能为空");
        Assert.notNull(cmd.getRequest().getToStoreId(), "调入仓库不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDetails()), "详情不能为空");
        Assert.isTrue(cmd.getRequest().getDetails().stream().allMatch(t -> Objects.nonNull(t.getGoodsId()) || Objects.nonNull(t.getStockId())), "商品id不能为空");

        TransferUsableQuotaQueryResult query = storeFacade.query(cmd.getRequest().getToId());

        System.out.println("query:" + JSONObject.toJSONString(query));
        if (query.getIsCtl() == 1) {
            List<Integer> stockIdList = cmd.getRequest()
                    .getDetails()
                    .stream()
                    .map(AllocateCreateRequest.AllocateLineDto::getStockId)
                    .collect(Collectors.toList());


            Map<Integer, AllocateCreateRequest.AllocateLineDto> collect = cmd.getRequest().getDetails().stream()
                    .collect(Collectors.toMap(AllocateCreateRequest.AllocateLineDto::getStockId, Function.identity()));




            List<StockExt1> stock = stockFacade.selectByStockIdList(stockIdList);
            System.out.println("stock:" + JSONObject.toJSONString(stock));

            Map<Integer, List<StockExt1>> os = stock.stream().filter(s -> s.getLevel().equals("压货"))
                    .collect(Collectors.groupingBy(StockExt1::getBrandId));

            System.out.println("os:" + JSONObject.toJSONString(os));

            Map<Integer, TransferUsableQuotaQueryResult.Item> map = query.getList()
                    .stream()
                    .collect(Collectors.toMap(TransferUsableQuotaQueryResult.Item::getBrandId, Function.identity()));



            os.forEach((k, v) -> {

                System.out.println("当前品牌id:" + k);


                TransferUsableQuotaQueryResult.Item item = map.get(k);
                if (item != null){
                    System.out.println("当前品牌id可用额度：" + item.getOsQuota());

                    BigDecimal bigDecimal = v.stream().map(i -> collect.get(i.getStockId()).getTransferPrice()).reduce(BigDecimal::add).get();

                    System.out.println("当前品牌id使用额度：" + bigDecimal);

                    if (item.getOsQuota().compareTo(bigDecimal) < 0){
                        throw new OperationRejectedException(OperationExceptionCodeEnum.SETP_1);
                    }
                }


            });

        }
    }

    @Getter
    @AllArgsConstructor
    enum AllocateType {
        //寄售
        CONSIGN(1, ProcessDefinitionKeyEnum.HQ_ALLOCATION),
        //寄售归还
        CONSIGN_RETURN(2, ProcessDefinitionKeyEnum.SHOP_ALLOCATION_TO_HQ),
        //平调
        FLAT(3, ProcessDefinitionKeyEnum.SHOP_ALLOCATION_TO_SHOP),
        //借调
        BORROW(4, ProcessDefinitionKeyEnum.SHOP_ALLOCATION_TO_SHOP),
        ;
        private Integer value;
        private ProcessDefinitionKeyEnum key;

        public static ProcessDefinitionKeyEnum getKey(int value) {
            return Arrays.stream(AllocateType.values())
                    .filter(t -> value == t.getValue())
                    .map(AllocateType::getKey)
                    .findFirst()
                    .orElse(null);
        }
    }
}
