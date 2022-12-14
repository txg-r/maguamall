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
        //??????????????????,??????purchaseId???????????????,??????????????????
        if (mergeVo.getPurchaseId() == null) {
            //???????????????
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            Date now = new Date();
            purchaseEntity.setCreateTime(now);
            purchaseEntity.setUpdateTime(now);
            baseMapper.insert(purchaseEntity);
            mergeVo.setPurchaseId(purchaseEntity.getId());
        }

        if (!mergeVo.getItems().isEmpty()) {
            //??????????????????????????????(??????????????????purchaseId??????)
            List<PurchaseDetailEntity> purchaseDetailEntities = mergeVo.getItems().stream()
                    .filter(purchaseDetailId -> {
                        //?????????????????????????????????
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
        //???????????????id?????????????????????
        List<PurchaseEntity> purchaseEntities = baseMapper.selectBatchIds(purchaseIdS).stream()
                .filter(purchaseEntity -> {
                    //??????????????????????????????????????????
                    return purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
                })
                .peek(purchaseEntity -> {
                    purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    purchaseEntity.setUpdateTime(new Date());
                    //?????????????????????????????????
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
        //1.???????????????
        //2.????????????
        //3.??????????????????


        //???????????????
        PurchaseEntity purchaseEntity = baseMapper.selectById(doneVo.getId());
        if (purchaseEntity == null) {
            throw new RuntimeException(doneVo.getId() + ",?????????????????????");
        }
        if (!purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.RECEIVE.getCode())) {
            throw new RuntimeException(doneVo.getId() + ",?????????????????????,????????????");
        }


        //???????????????
        List<PurchaseDetailEntity> purchaseDetailEntities = doneVo.getItems().stream().map(item -> {
            //???????????????
            PurchaseDetailEntity purchaseDetail = purchaseDetailService.getById(item.getItemId());
            if (purchaseDetail == null) {
                throw new RuntimeException(item.getItemId() + ",??????????????????");
            }
            if (!purchaseDetail.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.IN_PURCHASE.getCode())) {
                throw new RuntimeException(item.getItemId() + ",?????????????????????,????????????");
            }

            Integer status = item.getStatus();
            if (!status.equals(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode()) &&
                    !status.equals(WareConstant.PurchaseDetailStatusEnum.PURCHASE_FAIL.getCode())) {
                throw new RuntimeException(item.getStatus() + ":???????????????");
            }

            purchaseDetail.setStatus(status);
            return purchaseDetail;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(purchaseDetailEntities);


        //????????????
        List<WareSkuEntity> wareSkuEntities = purchaseDetailEntities.stream()
                .filter(purchaseDetailEntity -> {
                    //?????????????????????????????????
                    return purchaseEntity.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                })
                .map(purchaseDetailEntity -> {
                    LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(WareSkuEntity::getSkuId, purchaseDetailEntity.getSkuId());
                    WareSkuEntity wareSkuEntity = wareSkuService.getOne(wrapper);
                    if (wareSkuEntity == null) {
                        //????????????
                        wareSkuEntity = new WareSkuEntity();
                        wareSkuEntity.setSkuId(purchaseDetailEntity.getSkuId());
                        wareSkuEntity.setStock(0);
                        // TODO: 2022/10/11 ??????skuName
                        wareSkuService.save(wareSkuEntity);
                    }
                    //??????????????????
                    wareSkuEntity.setStock(wareSkuEntity.getStock() + purchaseDetailEntity.getSkuNum());
                    return wareSkuEntity;
                }).collect(Collectors.toList());
        wareSkuService.updateBatchById(wareSkuEntities);


        //???????????????
        //????????????????????????????????????????????????????????????,??????????????????
        List<PurchaseDetailEntity> checkPurchaseDetailList = purchaseDetailService
                .list(new LambdaQueryWrapper<PurchaseDetailEntity>().eq(PurchaseDetailEntity::getPurchaseId, purchaseEntity.getId()));
        //?????????,??????????????????????????????
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