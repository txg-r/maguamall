package com.tyfff.maguamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tyfff.common.utils.PageUtils;
import com.tyfff.maguamall.product.entity.SpuInfoEntity;
import com.tyfff.maguamall.product.vo.request.spu.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);
}

