package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.product.dao.CategoryBrandRelationDao;
import com.tyfff.maguamall.product.entity.BrandEntity;
import com.tyfff.maguamall.product.entity.CategoryBrandRelationEntity;
import com.tyfff.maguamall.product.entity.CategoryEntity;
import com.tyfff.maguamall.product.service.BrandService;
import com.tyfff.maguamall.product.service.CategoryBrandRelationService;
import com.tyfff.maguamall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        CategoryEntity categoryEntity = categoryService.getById(categoryBrandRelation.getCatelogId());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        BrandEntity brandEntity = brandService.getById(categoryBrandRelation.getBrandId());
        categoryBrandRelation.setBrandName(brandEntity.getName());
        //检查重复插入
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                .eq(CategoryBrandRelationEntity::getBrandName, brandEntity.getName())
                .eq(CategoryBrandRelationEntity::getCatelogName, categoryEntity.getName()));
        if (count==0){
            this.save(categoryBrandRelation);
        }else{
            throw new RuntimeException("数据已存在");
        }
    }


}