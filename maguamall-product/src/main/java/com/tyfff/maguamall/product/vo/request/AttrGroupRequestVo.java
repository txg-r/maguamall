package com.tyfff.maguamall.product.vo.request;

import com.tyfff.maguamall.product.entity.AttrGroupEntity;
import lombok.Data;

@Data
public class AttrGroupRequestVo extends AttrGroupEntity {


    private Long[] catelogPath;
}
