package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.to.SkuReductionTo;
import com.tyfff.common.to.SpuBoundTo;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.product.dao.SpuInfoDao;
import com.tyfff.maguamall.product.entity.SkuImagesEntity;
import com.tyfff.maguamall.product.entity.SkuInfoEntity;
import com.tyfff.maguamall.product.entity.SkuSaleAttrValueEntity;
import com.tyfff.maguamall.product.entity.SpuInfoEntity;
import com.tyfff.maguamall.product.feign.CouponFeignService;
import com.tyfff.maguamall.product.service.*;
import com.tyfff.maguamall.product.vo.request.spu.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Override

    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.and(w -> {
                w.eq(SpuInfoEntity::getId, key)
                        .or()
                        .like(SpuInfoEntity::getSpuName, key)
                        .or()
                        .like(SpuInfoEntity::getSpuDescription, key);
            });
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.hasText(brandId)) {
            wrapper.eq(SpuInfoEntity::getBrandId,brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.hasText(catelogId)) {
            wrapper.eq(SpuInfoEntity::getCatalogId,catelogId);
        }

        String status = (String) params.get("status");
        if (StringUtils.hasText(status)) {
            wrapper.eq(SpuInfoEntity::getPublishStatus,status);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        Date now = new Date();
        spuInfoEntity.setCreateTime(now);
        spuInfoEntity.setUpdateTime(now);
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        //2、保存Spu的描述图片 pms_spu_info_desc
        String descript = String.join(",", spuSaveVo.getDecript());
        spuInfoDescService.save(spuId, descript);
        //3、保存spu的图片集 pms_spu_imgs
        spuImagesService.save(spuId, spuSaveVo.getImages());
        //4.保存spu的规格参数;pms_product_attr_values
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        attrValueService.save(spuId, baseAttrs);
        //5、保存spu的积分信息；sms_spu_bounds
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        spuBoundTo.setSpuId(spuId);
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundTo);
        if (couponFeignService.saveBounds(spuBoundTo).getCode() != 0) {
            log.error("couponFeignService.saveBounds调用失败");
        }

        //6、保存当前spu对应的所有sku信息；
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && !skus.isEmpty()) {
            skus.forEach(skuVo -> {
                //6.1）、sku的基本信息；pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(skuVo, skuInfoEntity);
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
                skuInfoEntity.setCatalogId(spuSaveVo.getCatalogId());
                skuInfoEntity.setSaleCount(0L);

                //找出defaultImg并设置
                String defaultImg = null;
                List<Images> imgs = skuVo.getImages();
                if (imgs != null && !imgs.isEmpty()) {
                    for (Images img : imgs) {
                        if (img.getDefaultImg() == 1) {
                            defaultImg = img.getImgUrl();
                            break;
                        }
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();

                //6.2).sku的图片信息;pms_sku_images
                if (imgs != null && !imgs.isEmpty()) {
                    List<SkuImagesEntity> skuImagesEntities = imgs.stream().map(img -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setImgUrl(img.getImgUrl());
                        return skuImagesEntity;
                    }).filter(image -> {
                        return StringUtils.hasText(image.getImgUrl());
                    }).collect(Collectors.toList());
                    skuImagesService.saveBatch(skuImagesEntities);
                }

                //6.3).sku的销售属性信息;pms_sku_sale_attr_value
                List<Attr> attrs = skuVo.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //6.4）、sku的优惠、满减等信息；sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                skuReductionTo.setSkuId(skuId);
                BeanUtils.copyProperties(skuVo, skuReductionTo);
                if (couponFeignService.saveSkuReduction(skuReductionTo).getCode() != 0) {
                    log.error("couponFeignService.saveSkuReduction调用失败");
                }
            });
        }


    }

}