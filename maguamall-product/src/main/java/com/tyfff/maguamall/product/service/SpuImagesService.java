package com.tyfff.maguamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(Long spuId, List<String> images);
}

