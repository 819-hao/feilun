package com.seeease.flywheel.serve.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.dict.entity.DictData;

import java.util.List;

/**
* @author dmmasxnmf
* @description 针对表【dict_data(字典数据表)】的数据库操作Mapper
* @createDate 2023-02-14 15:19:31
* @Entity com.seeease.flywheel.DictData
*/
public interface DictDataMapper extends BaseMapper<DictData> {
    List<DictData> selectLikeDictDataByType(String dictType);
}




