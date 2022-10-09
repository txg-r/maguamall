package com.tyfff.maguamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.product.entity.AttrAttrgroupRelationEntity;
import com.tyfff.maguamall.product.vo.request.AttrGroupReqRelationVo;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveByVo(AttrGroupReqRelationVo[] relationVo);
}

