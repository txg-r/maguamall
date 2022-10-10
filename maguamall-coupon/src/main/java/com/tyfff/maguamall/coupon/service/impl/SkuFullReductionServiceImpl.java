package com.tyfff.maguamall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.to.SkuReductionTo;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.coupon.dao.SkuFullReductionDao;
import com.tyfff.maguamall.coupon.entity.MemberPriceEntity;
import com.tyfff.maguamall.coupon.entity.SkuFullReductionEntity;
import com.tyfff.maguamall.coupon.entity.SkuLadderEntity;
import com.tyfff.maguamall.coupon.service.MemberPriceService;
import com.tyfff.maguamall.coupon.service.SkuFullReductionService;
import com.tyfff.maguamall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void save(SkuReductionTo skuReductionTo) {
        //sku的优惠、满减等信息；sms_sku_ladder\sms_sku_full_reduction\sms_member_price
        //sms_sku_ladder
        if (skuReductionTo.getFullCount() > 0 && skuReductionTo.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }
        //sms_sku_full_reduction
        if (skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0 && skuReductionTo.getReducePrice().compareTo(BigDecimal.ZERO) > 0) {
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
            skuFullReductionEntity.setAddOther(skuReductionTo.getCountStatus());
            this.save(skuFullReductionEntity);
        }
        //sms_member_price
        List<MemberPriceEntity> memberPriceEntities = skuReductionTo.getMemberPrice().stream().filter(memberPrice -> {
            return memberPrice.getPrice().compareTo(BigDecimal.ZERO) > 0;
        }).map(memberPrice -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            return memberPriceEntity;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntities);
    }

}