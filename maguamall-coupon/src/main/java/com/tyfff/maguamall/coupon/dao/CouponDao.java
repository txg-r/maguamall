package com.tyfff.maguamall.coupon.dao;

import com.tyfff.maguamall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author tyfff
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-26 17:03:53
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
