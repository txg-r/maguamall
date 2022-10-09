package com.tyfff.maguamall.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.tyfff.maguamall.product.vo.response.BrandResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tyfff.maguamall.product.entity.CategoryBrandRelationEntity;
import com.tyfff.maguamall.product.service.CategoryBrandRelationService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam Long brandId) {
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.lambdaQuery().eq(CategoryBrandRelationEntity::getBrandId, brandId).list();

        return R.ok().put("data", data);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/brands/list")
    public R brandList(@RequestParam(required = true) Long catId) {
        LambdaQueryChainWrapper<CategoryBrandRelationEntity> query = categoryBrandRelationService.lambdaQuery();
        List<CategoryBrandRelationEntity> data;
        if (catId != null) {
            data = query
                    .eq(CategoryBrandRelationEntity::getCatelogId, catId)
                    .select(CategoryBrandRelationEntity::getBrandId,CategoryBrandRelationEntity::getBrandName)
                    .list();
        } else {
            data = query.select(CategoryBrandRelationEntity::getBrandId,CategoryBrandRelationEntity::getBrandName)
                    .list();
        }
        List<Object> list = data.stream().map(c -> {
            BrandResVo brandResVo = new BrandResVo();
            brandResVo.setBrandId(c.getBrandId());
            brandResVo.setBrandName(c.getBrandName());
            return brandResVo;
        }).collect(Collectors.toList());
        return R.ok().put("data", list);


    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
