package com.tyfff.maguamall.member.dao;

import com.tyfff.maguamall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author tyf
 * @email tanxiaogang2020@outlook.com
 * @date 2022-09-26 15:24:38
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
