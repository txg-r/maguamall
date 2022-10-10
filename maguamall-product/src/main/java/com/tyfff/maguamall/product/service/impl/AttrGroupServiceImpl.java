package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.product.dao.AttrGroupDao;
import com.tyfff.maguamall.product.entity.AttrAttrgroupRelationEntity;
import com.tyfff.maguamall.product.entity.AttrEntity;
import com.tyfff.maguamall.product.entity.AttrGroupEntity;
import com.tyfff.maguamall.product.entity.CategoryEntity;
import com.tyfff.maguamall.product.service.AttrAttrgroupRelationService;
import com.tyfff.maguamall.product.service.AttrGroupService;
import com.tyfff.maguamall.product.service.AttrService;
import com.tyfff.maguamall.product.service.CategoryService;
import com.tyfff.maguamall.product.vo.request.AttrGroupReqRelationVo;
import com.tyfff.maguamall.product.vo.request.AttrGroupReqVo;
import com.tyfff.maguamall.product.vo.response.AttrGroupResVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

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
    public AttrGroupReqVo getVoById(Long attrGroupId) {
        //查询attrGroup基本属性
        AttrGroupEntity attrGroup = this.getById(attrGroupId);
        AttrGroupReqVo attrGroupReqVo = new AttrGroupReqVo();
        BeanUtils.copyProperties(attrGroup, attrGroupReqVo);
        //查询并设置vo其他属性
        attrGroupReqVo.setCatelogPath(findCategoryPath(attrGroup.getCatelogId()));
        return attrGroupReqVo;
    }

    @Override
    public List<AttrEntity> getAttrByRelation(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = relationService.lambdaQuery()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId)
                .list();
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if (attrIds.isEmpty()) {
            return null;
        }
        return attrService.getByIds(attrIds);
    }

    @Override
    public PageUtils getNoAttrByRelation(Map<String, Object> params, Integer attrgroupId) {
        //查询分组关联属性id
        List<AttrAttrgroupRelationEntity> relationEntities = relationService.lambdaQuery()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId)
                .list();
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>();
        if (!attrIds.isEmpty()) {
            //构造不关联属性wrapper
            wrapper.notIn(AttrEntity::getAttrId, attrIds);
        }
        //添加模糊查询
        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.and(w -> {
                w.eq(AttrEntity::getAttrId, key)
                        .or()
                        .like(AttrEntity::getAttrName, key);
            });
        }
        return attrService.queryPageByWrapper(params, wrapper);
    }

    @Override
    public List<AttrGroupResVo> getAttrGroupWithAttrByCatelogId(Long catelogId) {
        //查询属性分组
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(wrapper);

        //封装成vo返回
        return attrGroupEntities.stream().map(group -> {
            AttrGroupResVo attrGroupResVo = new AttrGroupResVo();
            BeanUtils.copyProperties(group, attrGroupResVo);
            //查询分组的属性列表
            attrGroupResVo.setAttrs(this.getAttrByRelation(group.getAttrGroupId()));
            return attrGroupResVo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeDetailByIds(List<Long> groupIds) {
        //删除关联信息
        relationService.remove(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId,groupIds));
        //删除分组
        this.removeByIds(groupIds);

    }

    @Override
    public void deleteAttrRelation(AttrGroupReqRelationVo[] vos) {
        if (vos.length == 0) {
            return;
        }
        for (AttrGroupReqRelationVo vo : vos) {
            relationService.remove(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrGroupId, vo.getAttrGroupId())
                    .eq(AttrAttrgroupRelationEntity::getAttrId, vo.getAttrId()));
        }
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
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        Long parentCid = categoryEntity.getParentCid();
        if (parentCid != 0) {
            findCategoryPath(parentCid, list);
        }
        list.add(catelogId);
    }

}