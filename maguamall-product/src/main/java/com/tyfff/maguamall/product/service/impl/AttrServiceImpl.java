package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tyfff.maguamall.product.dao.AttrAttrgroupRelationDao;
import com.tyfff.maguamall.product.dao.AttrGroupDao;
import com.tyfff.maguamall.product.dao.CategoryDao;
import com.tyfff.maguamall.product.entity.AttrAttrgroupRelationEntity;
import com.tyfff.maguamall.product.entity.AttrGroupEntity;
import com.tyfff.maguamall.product.entity.CategoryEntity;
import com.tyfff.maguamall.product.vo.request.AttrReqVo;
import com.tyfff.maguamall.product.vo.response.AttrResVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;

import com.tyfff.maguamall.product.dao.AttrDao;
import com.tyfff.maguamall.product.entity.AttrEntity;
import com.tyfff.maguamall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params, Integer catelogId) {
        //基本分页模糊查询
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();
        if (catelogId != 0) {
            wrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.and(w -> {
                w.eq(AttrEntity::getAttrId, key)
                        .or()
                        .like(AttrEntity::getAttrName, key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        //将分页数据转为Vo
        List<AttrResVo> responseVos = page.getRecords().stream().map(attrEntity -> {
            AttrResVo responseVo = new AttrResVo();
            BeanUtils.copyProperties(attrEntity, responseVo);
            //查询属性的属性分组id
            AttrAttrgroupRelationEntity relationEntity = relationDao.
                    selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));

            //查询分组
            if (relationEntity!=null){
                AttrGroupEntity groupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                //设置Vo中分组名
                responseVo.setAttrGroupId(groupEntity.getAttrGroupId());
                responseVo.setAttrGroupName(groupEntity.getAttrGroupName());
            }
            //查询分类
            CategoryEntity categoryEntity = categoryDao
                    .selectOne(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getCatId, attrEntity.getCatelogId()));
            if (categoryEntity!=null){
                //设置分类名
                responseVo.setCatelogName(categoryEntity.getName());
            }
            return responseVo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(responseVos);
        return pageUtils;
    }

    @Override
    @Transactional
    public void saveVo(AttrReqVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        //保存属性
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        //保存属性与属性分组关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationDao.insert(relationEntity);
    }

    @Override
    public AttrResVo getByVoId(Long attrId) {
        AttrEntity attrEntity = getById(attrId);
        AttrResVo responseVo = new AttrResVo();
        BeanUtils.copyProperties(attrEntity, responseVo);
        //查询属性的属性分组id
        AttrAttrgroupRelationEntity relationEntity = relationDao.
                selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
        if (relationEntity!=null){
            //设置分组id
            responseVo.setAttrGroupId(relationEntity.getAttrGroupId());
        }
        if (attrEntity.getCatelogId()!=null){
            //查询分类path
            Long[] categoryPath = findCategoryPath(attrEntity.getCatelogId());
            responseVo.setCatelogPath(categoryPath);
        }
        return responseVo;
    }

    @Override
    @Transactional
    public void updateVo(AttrReqVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        //修改属性基本信息
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        //修改关联信息
        //首先检查是否存在关联信息
        AttrAttrgroupRelationEntity relation = relationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .eq(AttrAttrgroupRelationEntity::getAttrId, attrVo.getAttrId())
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrVo.getAttrGroupId()));
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        if (relation!=null){
            //存在则修改
            relationDao.update(
                    relationEntity
                    , new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attrVo.getAttrId()));
        }else {
            //不存在插入
            relationEntity.setAttrId(attrVo.getAttrId());
            relationDao.insert(relationEntity);
        }


    }

    @Override
    @Transactional
    public void removeDetail(Long[] attrIds) {
        List<Long> list = Arrays.asList(attrIds);
        //删除基本信息
        this.removeByIds(list);
        //删除关联信息
        relationDao.delete(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().in(AttrAttrgroupRelationEntity::getAttrId,list));
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