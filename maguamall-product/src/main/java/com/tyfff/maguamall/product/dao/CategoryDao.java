package com.tyfff.maguamall.product.dao;

import com.tyfff.maguamall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-25 17:04:09
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
