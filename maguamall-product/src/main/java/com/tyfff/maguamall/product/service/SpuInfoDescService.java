package com.tyfff.maguamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:09
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(Long spuId, String descript);
}

