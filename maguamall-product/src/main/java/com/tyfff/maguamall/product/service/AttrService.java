package com.tyfff.maguamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.product.entity.AttrEntity;
import com.tyfff.maguamall.product.vo.request.AttrReqVo;
import com.tyfff.maguamall.product.vo.response.AttrResVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params, Integer catelogId);

    void saveVo(AttrReqVo attr);

    AttrResVo getByVoId(Long attrId);

    void updateVo(AttrReqVo attr);

    void removeDetail(Long[] attrIds);
}

