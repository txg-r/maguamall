package com.tyfff.maguamall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.ware.entity.PurchaseEntity;
import com.tyfff.maguamall.ware.vo.request.PurchaseDoneVo;
import com.tyfff.maguamall.ware.vo.request.PurchaseMergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-26 15:42:40
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnrecerive();

    void merge(PurchaseMergeVo mergeVo);

    void received(List<Long> purchaseIdS);

    void done(PurchaseDoneVo doneVo);
}

