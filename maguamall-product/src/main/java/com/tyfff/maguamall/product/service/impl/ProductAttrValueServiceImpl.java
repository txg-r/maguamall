package com.tyfff.maguamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.product.dao.ProductAttrValueDao;
import com.tyfff.maguamall.product.entity.AttrEntity;
import com.tyfff.maguamall.product.entity.ProductAttrValueEntity;
import com.tyfff.maguamall.product.service.AttrService;
import com.tyfff.maguamall.product.service.ProductAttrValueService;
import com.tyfff.maguamall.product.vo.request.spu.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {
    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(Long spuId, List<BaseAttrs> baseAttrs) {
        //查询出所有要的attrName并用map存储
        List<Long> attrIdList = baseAttrs.stream().map(BaseAttrs::getAttrId).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrService.lambdaQuery().in(AttrEntity::getAttrId, attrIdList).select(AttrEntity::getAttrId, AttrEntity::getAttrName).list();
        Map<Long, String> attrNameMap = attrEntities.stream().collect(Collectors.toMap(AttrEntity::getAttrId, AttrEntity::getAttrName));
        //存储
        List<ProductAttrValueEntity> attrValueEntities = baseAttrs.stream().map(baseAttr -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setSpuId(spuId);
            attrValueEntity.setAttrId(baseAttr.getAttrId());
            attrValueEntity.setAttrName(attrNameMap.get(baseAttr.getAttrId()));
            attrValueEntity.setAttrValue(baseAttr.getAttrValues());
            attrValueEntity.setQuickShow(baseAttr.getShowDesc());
            return attrValueEntity;
        }).collect(Collectors.toList());

        this.saveBatch(attrValueEntities);
    }

}