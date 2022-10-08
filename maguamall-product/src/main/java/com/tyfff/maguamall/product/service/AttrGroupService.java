package com.tyfff.maguamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.product.entity.AttrEntity;
import com.tyfff.maguamall.product.entity.AttrGroupEntity;
import com.tyfff.maguamall.product.vo.request.AttrGroupReqRelationVo;
import com.tyfff.maguamall.product.vo.request.AttrGroupReqVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Integer catelogId);

    AttrGroupReqVo getVoById(Long attrGroupId);

    List<AttrEntity> getAttrByRelation(Integer attrgroupId);

    void deleteAttrRelation(AttrGroupReqRelationVo[] vo);

    void getNoAttrByRelation(Map<String, Object> params, Integer attrgroupId);
}

