package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.product.dao.SkuInfoDao;
import com.tyfff.maguamall.product.entity.SkuInfoEntity;
import com.tyfff.maguamall.product.service.SkuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.and(w -> {
                w.eq(SkuInfoEntity::getSkuId, key)
                        .or()
                        .like(SkuInfoEntity::getSkuName, key);
            });
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.hasText(brandId)&&!brandId.equals("0")) {
            wrapper.eq(SkuInfoEntity::getBrandId,brandId);
        }

        String min = (String) params.get("min");
        if (StringUtils.hasText(min)){
            if (new BigDecimal(min).compareTo(BigDecimal.ZERO)>0) {
                wrapper.ge(SkuInfoEntity::getPrice,min);
            }
        }

        String max = (String) params.get("max");
        if (StringUtils.hasText(max)){
            if (new BigDecimal(max).compareTo(BigDecimal.ZERO)>0) {
                wrapper.le(SkuInfoEntity::getPrice,max);
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}