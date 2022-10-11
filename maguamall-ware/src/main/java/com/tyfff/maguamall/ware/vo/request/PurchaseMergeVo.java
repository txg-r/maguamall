package com.tyfff.maguamall.ware.vo.request;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseMergeVo {
    private Long purchaseId;

    private List<Long> items;
}
