package com.leka.teashop.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class PageContext {

    private final Integer pageNo;
    private final Integer pageSize;
    private final String sortField;
    private final String sortDirection;

}
