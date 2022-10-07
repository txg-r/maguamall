package com.tyfff.maguamall.order.dao;

import com.tyfff.maguamall.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-26 15:17:23
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
