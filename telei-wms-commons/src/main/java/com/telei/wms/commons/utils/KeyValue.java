package com.telei.wms.commons.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: leo
 * @Date: 2020/6/24 16:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue<K,V > {
    private K key;
    private V value;
}
