package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.stereotype.Service;

import java.util.Map;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;

import com.tyfff.maguamall.product.dao.BrandDao;
import com.tyfff.maguamall.product.entity.BrandEntity;
import com.tyfff.maguamall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        LambdaQueryWrapper<BrandEntity> wrapper = new LambdaQueryWrapper<BrandEntity>()
                .like(BrandEntity::getBrandId, key)
                .or()
                .like(BrandEntity::getName, key);

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}