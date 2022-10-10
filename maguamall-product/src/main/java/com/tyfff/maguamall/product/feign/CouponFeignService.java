package com.tyfff.maguamall.product.feign;

import com.tyfff.common.to.SkuReductionTo;
import com.tyfff.common.to.SpuBoundTo;
import com.tyfff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("maguamall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveBounds(SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveTo")
    R saveSkuReduction(SkuReductionTo skuReductionTo);
}
