package com.seeease.flywheel.serve.dict.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.dict.constant.StockConsts;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.entity.StockDict;
import com.seeease.flywheel.serve.dict.mapper.DictDataMapper;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.dict.service.StockDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【dict_data(字典数据表)】的数据库操作Service实现
 * @createDate 2023-02-14 15:19:31
 */
@Service
@Slf4j
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData>
        implements DictDataService {
    @Resource
    private DictDataMapper dictDataMapper;
    @Resource
    private StockDictService stockDictService;

    @Override
    public Map dictData(Integer stockId,Integer isCard,String warrantyDate){

        //1.dictChildList
        //2.attachmentLabel

        Map<String, Object> map1 = MapUtil.newHashMap();
        try {
            List<Map> list = new ArrayList<>();

            //1.1 封装attachmentLabel
            List<Integer> stockIdList = new ArrayList<>();
            stockIdList.add(stockId);
            List<StockDict> stockDicts = stockDictService.selectByStockIdList(stockIdList);
            if (CollectionUtil.isNotEmpty(stockDicts)) {
                List<Long> collect = stockDicts.stream().map(StockDict::getDictId).collect(Collectors.toList());
                List<DictData> dictData = selectByDictCodeList(collect);
                if (CollectionUtil.isNotEmpty(dictData)) {
                    dictData.forEach(d -> {
                        Map dictMap = new HashMap();
                        dictMap.put("dictLabel",d.getDictLabel());
                        dictMap.put("dictCode",d.getDictCode());
                        list.add(dictMap);
                    });
                }
            }
            String attachmentLabel = StrUtil.EMPTY;

            List<DictData> dictDataList = dictDataMapper.selectLikeDictDataByType("stock");

            Map<String, Object> map = MapUtil.newHashMap();

            if (CollUtil.isNotEmpty(list)) {

                attachmentLabel = StrUtil.join(StrPool.SLASH, list.stream().map(test -> test.get("dictLabel")).collect(Collectors.toList()));

                //1.2 封装 dictChildList
                List<Object> codeList = list.stream().map(test -> test.get("dictCode")).collect(Collectors.toList());

                Map<String, List<DictData>> collect = dictDataList.stream().collect(Collectors.groupingBy(DictData::getDictType));

                for (Map.Entry<String, List<DictData>> entry : collect.entrySet()) {

                    String key = entry.getKey();
                    List<DictData> value = entry.getValue();

                    List<String> dataTypeList = CollUtil.newArrayList();

                    for (DictData dictData : value) {
                        boolean b = codeList.stream().anyMatch(l -> Long.parseLong(String.valueOf(l)) == dictData.getDictCode().longValue());

                        if (b) {
                            dataTypeList.add(dictData.getDictValue());
                        }
                    }

                    boolean b = dataTypeList.stream().allMatch(a -> NumberUtil.isNumber(a));

                    if (b) {

                        List<Integer> collect1 = dataTypeList.stream().map(a -> Integer.parseInt(a)).collect(Collectors.toList());

                        map.put(key, collect1);
                    } else {
                        map.put(key, dataTypeList);
                    }
                }

                map1.put("dictChildList", map);
            }

            String s1 = StockConsts.map.get(isCard);

            if (StrUtil.isNotBlank(s1)) {

                if (StrUtil.isBlank(attachmentLabel)) {
                    attachmentLabel = StrUtil.join(StrPool.SLASH, s1);
                } else {
                    attachmentLabel = StrUtil.join(StrPool.SLASH, attachmentLabel, s1);
                }
            }

            if (StrUtil.isNotBlank(warrantyDate)) {

                attachmentLabel = attachmentLabel + "({})";

                attachmentLabel = StrUtil.format(attachmentLabel, warrantyDate);
            }

            if (MapUtil.isEmpty(map)) {
                Map<String, List<DictData>> collect = dictDataList.stream().collect(Collectors.groupingBy(DictData::getDictType));
                Map<String, Object> map2 = MapUtil.newHashMap();
                for (String s : collect.keySet()) {
                    map2.put(s, CollUtil.newArrayList());
                }
                map1.put("dictChildList", map2);
            }

            map1.put("attachmentLabel", attachmentLabel);
        }catch (Exception e){
            log.error("查询附件信息异常,{}", e.getMessage(), e);
        }
        return map1;
    }

    @Override
    public List<DictData> selectByDictCodeList(List<Long> dicCodeList) {
        LambdaQueryWrapper<DictData> dictDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictDataLambdaQueryWrapper.in(DictData::getDictCode, dicCodeList);
        return dictDataMapper.selectList(dictDataLambdaQueryWrapper);
    }
}




