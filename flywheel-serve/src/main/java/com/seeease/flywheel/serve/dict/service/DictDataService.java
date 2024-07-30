package com.seeease.flywheel.serve.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.dict.entity.DictData;

import java.util.List;
import java.util.Map;

/**
* @author dmmasxnmf
* @description 针对表【dict_data(字典数据表)】的数据库操作Service
* @createDate 2023-02-14 15:19:31
*/
public interface DictDataService extends IService<DictData> {

    Map dictData(Integer stockId, Integer isCard, String warrantyDate);

    List<DictData> selectByDictCodeList(List<Long> dicCodeList);
}
