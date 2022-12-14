package com.tyfff.maguamall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.to.SkuReductionTo;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author tyfff
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-26 17:03:52
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(SkuReductionTo skuReductionTo);
}

