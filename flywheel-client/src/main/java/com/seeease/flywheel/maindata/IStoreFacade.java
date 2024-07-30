package com.seeease.flywheel.maindata;


import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.StoreQuotaImportRequest;
import com.seeease.flywheel.maindata.request.StoreQuotaAddRequest;
import com.seeease.flywheel.maindata.request.StoreQuotaQueryRequest;
import com.seeease.flywheel.maindata.result.StoreQuotaQueryResult;
import com.seeease.flywheel.maindata.result.TransferUsableQuotaQueryResult;


public interface IStoreFacade {


    Integer quotaSubmit(StoreQuotaAddRequest request);

    PageResult<StoreQuotaQueryResult> quotaPage(StoreQuotaQueryRequest request);

    void importDate(StoreQuotaImportRequest request);

    Integer quotaDel(Integer id);

    public TransferUsableQuotaQueryResult query(Integer shopId);

}
