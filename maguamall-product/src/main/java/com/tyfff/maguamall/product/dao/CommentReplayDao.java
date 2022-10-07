package com.tyfff.maguamall.product.dao;

import com.tyfff.maguamall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:10
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
