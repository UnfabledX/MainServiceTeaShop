package com.leka.teashop.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PageContext {

    private Integer pageNo;
    private Integer pageSize;
    private String sortField;
    private String sortDirection;

    public PageContext(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
