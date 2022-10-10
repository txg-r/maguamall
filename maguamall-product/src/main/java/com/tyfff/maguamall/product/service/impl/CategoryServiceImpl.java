package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.product.dao.CategoryDao;
import com.tyfff.maguamall.product.entity.CategoryBrandRelationEntity;
import com.tyfff.maguamall.product.entity.CategoryEntity;
import com.tyfff.maguamall.product.service.CategoryBrandRelationService;
import com.tyfff.maguamall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //拿到所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //对分类进行分级并返回
        return entities.stream().filter(entity -> {
            return entity.getParentCid().equals(0L);
        }).peek(entity -> findChildren(entity, entities)).sorted((entity1, entity2) -> {
            Integer sort1 = entity1.getSort();
            Integer sort2 = entity2.getSort();
            return (sort1 == null ? 0 : sort1) - (sort2 == null ? 0 : sort2);
        }).collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> idList) {
        // TODO: 2022/10/2 检查要删除的菜单是否被别处引用
        baseMapper.deleteBatchIds(idList);
    }

    @Transactional
    @Override
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        if (StringUtils.hasText(category.getName())) {
            //同步更新关联表中的Name
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setCatelogName(category.getName());
            categoryBrandRelationService.update(
                    categoryBrandRelationEntity,
                    new LambdaQueryWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getCatelogId, category.getCatId()));
        }
    }

    private void findChildren(CategoryEntity parent, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(e -> {
            return e.getParentCid().equals(parent.getCatId());
        }).peek(entity -> findChildren(entity, all)).sorted((entity1, entity2) -> {
            Integer sort1 = entity1.getSort();
            Integer sort2 = entity2.getSort();
            return (sort1 == null ? 0 : sort1) - (sort2 == null ? 0 : sort2);
        }).collect(Collectors.toList());
        parent.setChildren(children);
    }

}