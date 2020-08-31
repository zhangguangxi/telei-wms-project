package com.telei.wms.commons.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: leo
 * @Date: 2020/6/24 16:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueCommonResponse<K,V> {
    private List<KeyValue> items;

    public static KeyValueCommonResponse create(Map source) {
        return new KeyValueCommonResponse((List<KeyValue>) (source.keySet().stream().map(v -> new KeyValue(v, source.get(v))).collect(Collectors.toList())));
    }
}
