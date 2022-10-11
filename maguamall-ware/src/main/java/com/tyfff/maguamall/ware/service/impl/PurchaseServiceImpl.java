package com.tyfff.maguamall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyfff.common.constant.WareConstant;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.common.utils.Query;
import com.tyfff.maguamall.ware.dao.PurchaseDao;
import com.tyfff.maguamall.ware.entity.PurchaseDetailEntity;
import com.tyfff.maguamall.ware.entity.PurchaseEntity;
import com.tyfff.maguamall.ware.entity.WareSkuEntity;
import com.tyfff.maguamall.ware.service.PurchaseDetailService;
import com.tyfff.maguamall.ware.service.PurchaseService;
import com.tyfff.maguamall.ware.service.WareSkuService;
import com.tyfff.maguamall.ware.vo.request.PurchaseDoneVo;
import com.tyfff.maguamall.ware.vo.request.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnrecerive() {
        LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<PurchaseEntity>()
                .eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.CREATED.getCode())
                .or()
                .eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(new HashMap<>()),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void merge(PurchaseMergeVo mergeVo) {
        //两种合并方式,不传purchaseId为新建合并,传为直接合并
        if (mergeVo.getPurchaseId() == null) {
            //新建采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            Date now = new Date();
            purchaseEntity.setCreateTime(now);
            purchaseEntity.setUpdateTime(now);
            baseMapper.insert(purchaseEntity);
            mergeVo.setPurchaseId(purchaseEntity.getId());
        }

        if (!mergeVo.getItems().isEmpty()) {
            //将采购项合并到采购单(更新采购项的purchaseId字段)
            List<PurchaseDetailEntity> purchaseDetailEntities = mergeVo.getItems().stream()
                    .filter(purchaseDetailId -> {
                        //过滤掉状态不对的采购项
                        PurchaseEntity purchaseEntity = baseMapper.selectById(purchaseDetailId);
                        return purchaseEntity.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.CREATED.getCode())
                                || purchaseEntity.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                    })
                    .map(purchaseDetailId -> {
                        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                        purchaseDetailEntity.setId(purchaseDetailId);
                        purchaseDetailEntity.setPurchaseId(mergeVo.getPurchaseId());
                        purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                        return purchaseDetailEntity;
                    }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        }
    }

    @Override
    @Transactional
    public void received(List<Long> purchaseIdS) {
        //检验采购单id是否能够被领取
        List<PurchaseEntity> purchaseEntities = baseMapper.selectBatchIds(purchaseIdS).stream()
                .filter(purchaseEntity -> {
                    //只有已分配的采购单能够被领取
                    return purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
                })
                .peek(purchaseEntity -> {
                    purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    purchaseEntity.setUpdateTime(new Date());
                    //更新采购单中采购项状态
                    List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService
                            .list(new LambdaQueryWrapper<PurchaseDetailEntity>().eq(PurchaseDetailEntity::getPurchaseId, purchaseEntity.getId()));
                    purchaseDetailEntities.forEach(purchaseDetailEntity -> {
                        purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.IN_PURCHASE.getCode());
                    });
                    purchaseDetailService.updateBatchById(purchaseDetailEntities);
                }).collect(Collectors.toList());

        this.updateBatchById(purchaseEntities);
    }

    @Override
    @Transactional
    public void done(PurchaseDoneVo doneVo) {
        //1.修改采购项
        //2.修改库存
        //3.修改猜哦股单


        //检查采购单
        PurchaseEntity purchaseEntity = baseMapper.selectById(doneVo.getId());
        if (purchaseEntity == null) {
            throw new RuntimeException(doneVo.getId() + ",该采购单不存在");
        }
        if (!purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.RECEIVE.getCode())) {
            throw new RuntimeException(doneVo.getId() + ",采购单状态异常,无法完成");
        }


        //修改采购项
        List<PurchaseDetailEntity> purchaseDetailEntities = doneVo.getItems().stream().map(item -> {
            //检查采购项
            PurchaseDetailEntity purchaseDetail = purchaseDetailService.getById(item.getItemId());
            if (purchaseDetail == null) {
                throw new RuntimeException(item.getItemId() + ",采购项不存在");
            }
            if (!purchaseDetail.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.IN_PURCHASE.getCode())) {
                throw new RuntimeException(item.getItemId() + ",采购项状态异常,无法提交");
            }

            Integer status = item.getStatus();
            if (!status.equals(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode()) &&
                    !status.equals(WareConstant.PurchaseDetailStatusEnum.PURCHASE_FAIL.getCode())) {
                throw new RuntimeException(item.getStatus() + ":状态值错误");
            }

            purchaseDetail.setStatus(status);
            return purchaseDetail;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(purchaseDetailEntities);


        //修改库存
        List<WareSkuEntity> wareSkuEntities = purchaseDetailEntities.stream()
                .filter(purchaseDetailEntity -> {
                    //只留下采购成功的采购项
                    return purchaseEntity.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                })
                .map(purchaseDetailEntity -> {
                    LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(WareSkuEntity::getSkuId, purchaseDetailEntity.getSkuId());
                    WareSkuEntity wareSkuEntity = wareSkuService.getOne(wrapper);
                    if (wareSkuEntity == null) {
                        //新建库存
                        wareSkuEntity = new WareSkuEntity();
                        wareSkuEntity.setSkuId(purchaseDetailEntity.getSkuId());
                        wareSkuEntity.setStock(0);
                        // TODO: 2022/10/11 设置skuName
                        wareSkuService.save(wareSkuEntity);
                    }
                    //修改库存数量
                    wareSkuEntity.setStock(wareSkuEntity.getStock() + purchaseDetailEntity.getSkuNum());
                    return wareSkuEntity;
                }).collect(Collectors.toList());
        wareSkuService.updateBatchById(wareSkuEntities);


        //修改采购单
        //首先对数据库中该采购单的所有的采购项检查,看是否都完成
        List<PurchaseDetailEntity> checkPurchaseDetailList = purchaseDetailService
                .list(new LambdaQueryWrapper<PurchaseDetailEntity>().eq(PurchaseDetailEntity::getPurchaseId, purchaseEntity.getId()));
        //标志位,代表是否采购项全完成
        boolean purchaseFlag = true;
        for (PurchaseDetailEntity purchaseDetailEntity : checkPurchaseDetailList) {
            if (!purchaseDetailEntity.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode())) {
                purchaseFlag = false;
                break;
            }
        }
        purchaseEntity.setStatus(purchaseFlag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HAS_ERROR.getCode());
        baseMapper.updateById(purchaseEntity);
    }

}