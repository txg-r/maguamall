package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;

import com.tyfff.maguamall.product.dao.AttrGroupDao;
import com.tyfff.maguamall.product.entity.AttrGroupEntity;
import com.tyfff.maguamall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

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
        if (catelogId!=0){
            //为0查询所有分类属性分组
            wrapper.eq(AttrGroupEntity::getCatelogId,catelogId);
        }
        //添加模糊查询
        String key = (String) params.get("key");
        wrapper.like(AttrGroupEntity::getAttrGroupId,key)
                .or()
                .like(AttrGroupEntity::getAttrGroupName,key);

        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}