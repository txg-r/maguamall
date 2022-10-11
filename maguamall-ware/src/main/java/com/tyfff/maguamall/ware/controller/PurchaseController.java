package com.tyfff.maguamall.ware.controller;

import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.R;
import com.tyfff.maguamall.ware.entity.PurchaseEntity;
import com.tyfff.maguamall.ware.service.PurchaseService;
import com.tyfff.maguamall.ware.vo.request.PurchaseDoneVo;
import com.tyfff.maguamall.ware.vo.request.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-26 15:42:40
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/unreceive/list")
    public R unreceive() {
        PageUtils page = purchaseService.queryPageUnrecerive();

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 合并采购需求
     * @param mergeVo   vo对象
     * @return  R
     */
    @PostMapping("/merge")
    public R merge(@RequestBody PurchaseMergeVo mergeVo){
        purchaseService.merge(mergeVo);

        return R.ok();
    }


    /**
     * 领取采购单
     * @param purchaseIdS   被领取采购单的id
     * @return  R
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> purchaseIdS){
        purchaseService.received(purchaseIdS);

        return R.ok();
    }

    @PostMapping("done")
    public R done(@RequestBody PurchaseDoneVo doneVo){
        purchaseService.done(doneVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
