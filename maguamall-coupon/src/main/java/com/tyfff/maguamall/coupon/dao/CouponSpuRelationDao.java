package com.tyfff.maguamall.coupon.dao;

import com.tyfff.maguamall.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author tyfff
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-26 17:03:52
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
