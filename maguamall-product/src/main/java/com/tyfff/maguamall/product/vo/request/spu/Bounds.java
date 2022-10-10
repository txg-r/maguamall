/**
  * Copyright 2019 bejson.com 
  */
package com.tyfff.maguamall.product.vo.request.spu;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class Bounds {

    private BigDecimal buyBounds;
    private BigDecimal growBounds;


}