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
        //??????????????????
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        if (catelogId != 0) {
            //???0??????????????????????????????
            wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        }
        //??????????????????
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
        //??????attrGroup????????????
        AttrGroupEntity attrGroup = this.getById(attrGroupId);
        AttrGroupReqVo attrGroupReqVo = new AttrGroupReqVo();
        BeanUtils.copyProperties(attrGroup, attrGroupReqVo);
        //???????????????vo????????????
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
        //????????????????????????id
        List<AttrAttrgroupRelationEntity> relationEntities = relationService.lambdaQuery()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId)
                .list();
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>();
        if (!attrIds.isEmpty()) {
            //?????????????????????wrapper
            wrapper.notIn(AttrEntity::getAttrId, attrIds);
        }
        //??????????????????
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
        //??????????????????
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(wrapper);

        //?????????vo??????
        return attrGroupEntities.stream().map(group -> {
            AttrGroupResVo attrGroupResVo = new AttrGroupResVo();
            BeanUtils.copyProperties(group, attrGroupResVo);
            //???????????????????????????
            attrGroupResVo.setAttrs(this.getAttrByRelation(group.getAttrGroupId()));
            return attrGroupResVo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeDetailByIds(List<Long> groupIds) {
        //??????????????????
        relationService.remove(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId,groupIds));
        //????????????
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
     * ??????categoryId????????????????????????Id
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