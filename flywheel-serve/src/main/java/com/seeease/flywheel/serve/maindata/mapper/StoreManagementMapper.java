package com.seeease.flywheel.serve.maindata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.maindata.entity.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【store_management(门店管理)】的数据库操作Mapper
 * @createDate 2023-01-31 16:30:14
 * @Entity com.seeease.flywheel.StoreManagement
 */
public interface StoreManagementMapper extends BaseMapper<StoreManagement> {

    /**
     * 查门店信息
     *
     * @param ids
     * @return
     */
    List<StoreManagementInfo> selectByIds(@Param("ids") List<Integer> ids);

    @MapKey("id")
    List<StoreManagementForTag> selectAllList();


    /**
     * @return
     */
    List<ShopDto> listShop();

    /**
     * @param sidList
     * @return
     */
    List<ShopMemberDto> listShopMember(@Param("sidList") List<Integer> sidList, @Param("roleKeyList") List<String> roleKeyList);

    StoreManagement selectByStoreId(@Param("storeId") Integer storeId);

    StoreManagement selectByShopId(@Param("shopId") Integer shopId);

    List<ShopDto> listShopByName(@Param("tagNameList") List<String> tagNameList);

    @Select("SELECT\n" +
            "\tu.userid userId \n" +
            "FROM\n" +
            "\t`user` u\n" +
            "\tINNER JOIN user_tag ut ON ut.`user_id` = u.id\n" +
            "\tINNER JOIN store_management sm ON sm.tag_id = ut.tag_id\n" +
            "\tINNER JOIN user_role ur ON ur.`user_id` = u.id \n" +
            "WHERE\n" +
            "\tur.role_id = #{roleId} \n" +
            "\tAND sm.id = #{storeManagerId} ")
    List<String> listByShopManager(@Param("storeManagerId")Integer storeManagerId, @Param("roleId")Integer roleId);
}




