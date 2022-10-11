package com.tyfff.maguamall.ware.vo.request;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseDoneVo {

    private Long id;

    List<PurchaseDetailDoneVo> items;


}
