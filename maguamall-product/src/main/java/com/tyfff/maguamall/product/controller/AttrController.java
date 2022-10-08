package com.tyfff.maguamall.product.controller;

import java.util.Map;

import com.tyfff.maguamall.product.vo.request.AttrReqVo;
import com.tyfff.maguamall.product.vo.response.AttrResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tyfff.maguamall.product.service.AttrService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.R;



/**
 * 商品属性
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/base/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Integer catelogId){
        PageUtils page = attrService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrResVo attr = attrService.getByVoId(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrReqVo attr){
		attrService.saveVo(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrReqVo attr){
		attrService.updateVo(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeDetail(attrIds);

        return R.ok();
    }

}
