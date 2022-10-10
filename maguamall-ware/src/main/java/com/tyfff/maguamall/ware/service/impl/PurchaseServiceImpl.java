package com.tyfff.maguamall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.ware.dao.PurchaseDao;
import com.tyfff.maguamall.ware.entity.PurchaseEntity;
import com.tyfff.maguamall.ware.service.PurchaseService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnrecerive() {
        LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<PurchaseEntity>()
                .eq(PurchaseEntity::getStatus, 0)
                .or()
                .eq(PurchaseEntity::getStatus, 1);

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(new HashMap<>()),
                wrapper
        );

        return new PageUtils(page);
    }

}