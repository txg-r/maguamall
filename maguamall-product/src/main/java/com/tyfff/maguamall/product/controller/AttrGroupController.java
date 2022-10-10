package com.tyfff.maguamall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.tyfff.maguamall.product.entity.AttrEntity;
import com.tyfff.maguamall.product.service.AttrAttrgroupRelationService;
import com.tyfff.maguamall.product.vo.request.AttrGroupReqRelationVo;
import com.tyfff.maguamall.product.vo.request.AttrGroupReqVo;
import com.tyfff.maguamall.product.vo.response.AttrGroupResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tyfff.maguamall.product.entity.AttrGroupEntity;
import com.tyfff.maguamall.product.service.AttrGroupService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.R;



/**
 * 属性分组
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Integer catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupReqVo attrGroupReqVo = attrGroupService.getVoById(attrGroupId);

        return R.ok().put("attrGroup", attrGroupReqVo);
    }

    @GetMapping("{catelogId}/withattr")
    public R catelogAttrInfo(@PathVariable Long catelogId){
        List<AttrGroupResVo> data = attrGroupService.getAttrGroupWithAttrByCatelogId(catelogId);

        return R.ok().put("data",data);
    }

    /**
     * 获取指定分组关联的所有属性
     * @param attrgroupId   分组id
     * @return  所有属性
     */
    @GetMapping("{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable Long attrgroupId){
        List<AttrEntity> attrList = attrGroupService.getAttrByRelation(attrgroupId);
        return R.ok().put("data",attrList);
    }

    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性
     * @param params    分页查询数据
     * @return 没有关联的本分类里面的其他基本属性
     */
    @GetMapping("{attrgroupId}/noattr/relation")
    public R noattrRelation(@RequestParam Map<String, Object> params, @PathVariable Integer attrgroupId){
        PageUtils noAttr = attrGroupService.getNoAttrByRelation(params, attrgroupId);
        return R.ok().put("page",noAttr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    @PostMapping("attr/relation")
    public R saveAttrRelation(@RequestBody AttrGroupReqRelationVo[] relationVo){
        relationService.saveByVo(relationVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeDetailByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody AttrGroupReqRelationVo[] vo){
        attrGroupService.deleteAttrRelation(vo);
        return R.ok();
    }

}
