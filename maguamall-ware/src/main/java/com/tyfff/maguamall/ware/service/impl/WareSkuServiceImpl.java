package com.tyfff.maguamall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.ware.dao.WareSkuDao;
import com.tyfff.maguamall.ware.entity.WareSkuEntity;
import com.tyfff.maguamall.ware.service.WareSkuService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (StringUtils.hasText(skuId)){
            wrapper.eq(WareSkuEntity::getSkuId,skuId);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.hasText(wareId)){
            wrapper.eq(WareSkuEntity::getWareId,wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}