package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tyfff.maguamall.product.dao.CategoryDao;
import com.tyfff.maguamall.product.entity.CategoryEntity;
import com.tyfff.maguamall.product.vo.request.AttrGroupRequestVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;

import com.tyfff.maguamall.product.dao.AttrGroupDao;
import com.tyfff.maguamall.product.entity.AttrGroupEntity;
import com.tyfff.maguamall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private CategoryDao categoryDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Integer catelogId) {
        //创建查询条件
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        if (catelogId != 0) {
            //为0查询所有分类属性分组
            wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        }
        //添加模糊查询
        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.and(w -> {
                w.eq(AttrGroupEntity::getAttrGroupId, key)
                        .or()
                        .like(AttrGroupEntity::getAttrGroupName, key)
                        .or()
                        .like(AttrGroupEntity::getDescript, key);
            });

        }


        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public AttrGroupRequestVo getVoById(Long attrGroupId) {
        //查询attrGroup基本属性
        AttrGroupEntity attrGroup = this.getById(attrGroupId);
        AttrGroupRequestVo attrGroupRequestVo = new AttrGroupRequestVo();
        BeanUtils.copyProperties(attrGroup, attrGroupRequestVo);
        //查询并设置vo其他属性
        attrGroupRequestVo.setCatelogPath(findCategoryPath(attrGroup.getCatelogId()));
        return attrGroupRequestVo;
    }

    /**
     * 通过categoryId递归寻找所有祖先Id
     *
     * @param catelogId id of category
     * @return idPath
     */
    private Long[] findCategoryPath(Long catelogId) {
        ArrayList<Long> list = new ArrayList<>();
        findCategoryPath(catelogId, list);
        return list.toArray(new Long[0]);
    }

    private void findCategoryPath(Long catelogId, ArrayList<Long> list) {
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        Long parentCid = categoryEntity.getParentCid();
        if (parentCid != 0) {
            findCategoryPath(parentCid, list);
        }
        list.add(catelogId);
    }

}