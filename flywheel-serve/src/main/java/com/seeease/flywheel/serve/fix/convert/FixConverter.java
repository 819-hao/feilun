package com.seeease.flywheel.serve.fix.convert;

import com.seeease.flywheel.fix.request.*;
import com.seeease.flywheel.fix.result.*;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.entity.FixProjectMapper;
import com.seeease.flywheel.serve.fix.entity.LogFixOpt;
import com.seeease.flywheel.serve.goods.entity.AttachmentConsumeLog;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/18 10:51
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FixConverter extends EnumConvert {

    FixConverter INSTANCE = Mappers.getMapper(FixConverter.class);


    /**
     * 入参转换
     *
     * @param dto
     * @return
     */
    BillFix convert(FixCreateRequest dto);


    /**
     * id转换
     *
     * @param dto
     * @return
     */
    @Mappings(value = {
            @Mapping(source = "fixId", target = "id"),
    })
    BillFix convertList(FixReceiveRequest.FixReceiveListRequest dto);

    @Mappings(value = {
            @Mapping(source = "fixId", target = "id"),
    })
    BillFix convert(FixFinishRequest dto);

    @Mappings(value = {
            @Mapping(source = "fixId", target = "id"),
    })
    BillFix convert(QtFixRequest dto);

    /**
     * 响应转换
     *
     * @param billFix
     * @return
     */
    FixCreateResult convertBillFixCreateResult(BillFix billFix);

    /**
     * 响应转化
     *
     * @param billFix
     * @return
     */
    FixFinishResult convertBillFixFinishResult(BillFix billFix);

    FixEditResult convertBillFixEditResult(BillFix billFix);
    FixEditResultResult convertFixEditResultResult(BillFix billFix);
    FixMaintenanceResult convertFixMaintenanceResult(BillFix billFix);

    FixDelayResult convertBillFixDelayResult(BillFix billFix);

    QtFixResult convertQtBillFixResult(BillFix billFix);

    /**
     * 格式化
     *
     * @param billFix
     * @return
     */
    FixDetailsResult convertFixDetailsResult(BillFix billFix);

    FixProjectMapper conver(FixEditRequest.FixProjectMapper fixProjectMapper);

    BillFix conver(FixEditResultRequest request);
    BillFix conver(FixMaintenanceRequest request);

    /**
     * 映射
     *
     * @param fixProjectMapper
     * @return
     */
    @Mappings(value = {
            @Mapping(source = "fixId", target = "id")
    })
    BillFix conver(FixEditRequest fixProjectMapper);

    @Mappings(value = {
            @Mapping(source = "fixId", target = "id")
    })
    BillFix conver(FixDelayRequest fixProjectMapper);

    @Mappings(value = {
            @Mapping(source = "fixId", target = "id"),
    })
    BillFix convert(FixRepairRequest dto);

    @Mappings(value = {
            @Mapping(source = "fixId", target = "id"),
    })
    BillFix convert(FixAllotRequest dto);

    @Mappings(value = {
    })
    FixAllotResult convertFixAllotResult(BillFix dto);


    @Mappings(value = {
            @Mapping(source = "fixId", target = "id"),
    })
    BillFix convert(FixForeignRequest dto);


    @Mappings(value = {
    })
    FixForeignResult convertFixForeignResult(BillFix dto);

    @Mappings(value = {

    })
    FixListResult convertFixListResult(BillFix dto);


    @Mappings(value = {
            @Mapping(target = "parentFixId", ignore = true),
            @Mapping(target = "customerName", ignore = true),
            @Mapping(target = "customerPhone", ignore = true),
            @Mapping(target = "customerAddress", ignore = true),
            @Mapping(target = "deliveryExpressNo", ignore = true),
            @Mapping(target = "storeId", ignore = true),
            @Mapping(target = "parentStoreId", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "content", ignore = true),
            @Mapping(target = "maintenanceMasterId", ignore = true),
            @Mapping(target = "finishType", ignore = true),
            @Mapping(target = "deliverExpressNo", ignore = true),
            @Mapping(target = "attachmentIdList", ignore = true),
            @Mapping(target = "defectDescription", ignore = true),
            @Mapping(target = "fixDay", ignore = true),
            @Mapping(target = "attachmentCostPrice", ignore = true),
            @Mapping(target = "fixSiteId", ignore = true),
    })
    FixCreateRequest convertFixCreateRequest(BillFix dto);

    @Mappings(value = {
            @Mapping(target = "fixNode", source = "fixState")
    })
    FixLog convertFixLog(LogFixOpt logFixOpt);


    FixDetailsResult.AttachmentMapper convertAttachmentMapper(AttachmentConsumeLog attachmentConsumeLog);

    FixListResult.AttachmentMapper convertAttachmentMapper2(AttachmentConsumeLog attachmentConsumeLog);
    @Mappings(value = {
            @Mapping(source = "purchasePrice", target = "costPrice"),
    })
    FixDetailsResult.AttachmentMapper convertAttachmentMapper(WatchDataFusion watchDataFusion);
    @Mappings(value = {
            @Mapping(source = "purchasePrice", target = "costPrice"),
    })
    FixListResult.AttachmentMapper convertAttachmentMapper2(WatchDataFusion watchDataFusion);

    com.seeease.flywheel.fix.result.FixGanttChartResult.StockTaskMapper.FixProjectMapper convertAttachmentMapper(FixProjectMapper fixProjectMapper);
}
