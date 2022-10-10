package com.tyfff.maguamall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.ware.dao.PurchaseDetailDao;
import com.tyfff.maguamall.ware.entity.PurchaseDetailEntity;
import com.tyfff.maguamall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();

        String wareId = (String) params.get("wareId");
        if (StringUtils.hasText(wareId)) {
            wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }

        String status = (String) params.get("status");
        if (StringUtils.hasText(status)) {
            wrapper.eq(PurchaseDetailEntity::getStatus, status);
        }

        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.and(w -> {
                w.eq(PurchaseDetailEntity::getId, key)
                        .or()
                        .eq(PurchaseDetailEntity::getPurchaseId, key)
                        .or()
                        .eq(PurchaseDetailEntity::getSkuId, key);
            });
        }


        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}