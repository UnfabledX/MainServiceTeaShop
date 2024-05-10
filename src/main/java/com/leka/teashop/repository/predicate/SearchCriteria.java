package com.leka.teashop.repository.predicate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class represents filtering criteria which holds the details
 * to represent a constraint:
 * Key is a field name, e.g. status, name etc.
 * Operation is operations such as <, >, = needed for filtering
 * Value is a field value, e.g. PRESENT, "some name"
 */
@Getter
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
}