package com.tyfff.maguamall.ware.vo.request;

import lombok.Data;

@Data
public class PurchaseDetailDoneVo {
    private Long itemId;

    private Integer status;

    private String reason;
}
