package com.seeease.flywheel.serve.base;

import com.seeease.flywheel.serve.dict.entity.DictData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/3/9
 */
public class StringTools {
    /**
     * 空字符串
     */
    private static final String NULL_STR = "";
    /**
     * 空null
     */
    private static final String NULL = "null";

    /**
     * 拼接附件信息
     *
     * @param dataList
     * @param itemList
     * @param isCard
     * @param warrantyDate
     * @return
     */
    public static String convert(List<DictData> dataList, List<Integer> itemList, Integer isCard, String warrantyDate) {
        StringBuffer sb = new StringBuffer("");
        if (CollectionUtils.isEmpty(dataList) || CollectionUtils.isEmpty(itemList) || ObjectUtils.isEmpty(isCard) || ObjectUtils.isEmpty(warrantyDate))
            return sb.toString();
        if (ObjectUtils.isNotEmpty(isCard) && isCard == 1)
            sb.append(org.apache.commons.lang3.StringUtils.replace("保卡(date)", "date", warrantyDate));
        else if (ObjectUtils.isNotEmpty(isCard) && isCard == 0)
            sb.append("空白保卡");

        String attachment = itemList.stream()
                .flatMap(item -> dataList.stream()
                        .filter(dictData -> item.intValue() == (dictData.getDictCode().intValue())))
                .map(dictData -> dictData.getDictLabel()).collect(Collectors.joining("/"));
        if (!"".equals(sb.toString()))
            sb.append("/");
        sb.append(attachment);
        return sb.toString();
    }

    /**
     * 拼接数据 当前只有一个数的时候 返回当前字符串
     *
     * @param parentSerialNo
     * @param andDecrement
     * @return
     */
    public static String dataSplicing(String parentSerialNo, int andDecrement) {
        if (andDecrement == 1)
            return parentSerialNo;
        return parentSerialNo + "-" + andDecrement;
    }

    /**
     * 判断目标是否在 源字符串里
     *
     * @param source
     * @param target
     * @return
     */
    public static Boolean calibrationParameters(String source, String target, Boolean flag) {
        if (isNotEmpty(source))
            for (String s : source.split(",")) {
                if (s.equals(target))
                    return false;
            }
        return flag;
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULL_STR.equals(str.trim()) || NULL.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * @param str
     * @return
     */
    public static String purification(String str) {
        if (Objects.isNull(str)) {
            return null;
        }
        return str.trim().replaceAll("\\.", "");
    }

    /**
     * 从地址串中解析提取出省市区等信息
     *
     * @param address 地址信息
     * @return 解析后的地址Map
     */
    private static Map<String, String> addressResolution(String address) {
        //1.地址的正则表达式
        String regex = "(?<province>[^省]+省|.+自治区|[^澳门]+澳门|[^香港]+香港|[^市]+市)?(?<city>[^自治州]+自治州|[^特别行政区]+特别行政区|[^市]+市|.*?地区|.*?行政单位|.+盟|市辖区|[^县]+县)(?<county>[^县]+县|[^市]+市|[^镇]+镇|[^区]+区|[^乡]+乡|.+场|.+旗|.+海域|.+岛)?(?<address>.*)";
        //2、创建匹配规则
        Matcher m = Pattern.compile(regex).matcher(address);
        String province;
        String city;
        String county;
        String detailAddress;
        Map<String, String> map = new HashMap<>(16);

        if (m.matches()) {
            //加入省
            province = m.group("province");
            map.put("province", province == null ? "" : province.trim());
            //加入市
            city = m.group("city");
            map.put("city", city == null ? "" : city.trim());
            //加入区
            county = m.group("county");
            map.put("county", county == null ? "" : county.trim());
            //详细地址
            detailAddress = m.group("address");
            map.put("address", detailAddress == null ? "" : detailAddress.trim());
        }
        return map;
    }
}
