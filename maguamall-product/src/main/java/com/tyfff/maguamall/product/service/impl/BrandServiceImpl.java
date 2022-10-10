package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.product.dao.BrandDao;
import com.tyfff.maguamall.product.entity.BrandEntity;
import com.tyfff.maguamall.product.entity.CategoryBrandRelationEntity;
import com.tyfff.maguamall.product.service.BrandService;
import com.tyfff.maguamall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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

    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        this.updateById(brand);
        if (StringUtils.hasText(brand.getName())) {
            //同步更新关联表中的brandName
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setBrandName(brand.getName());
            categoryBrandRelationService.update(
                    categoryBrandRelationEntity,
                    new LambdaQueryWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getBrandId, brand.getBrandId()));
        }

    }

    @Override
    @Transactional
    public void removeDetailByIds(List<Long> brandIds) {
        //删除关联信息
        categoryBrandRelationService.remove(new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                .in(CategoryBrandRelationEntity::getBrandId, brandIds));

        //删除本表信息
        this.removeByIds(brandIds);
    }

}