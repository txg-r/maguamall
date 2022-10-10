package com.tyfff.maguamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

    void removeDetailByIds(List<Long> brandIds);
}

