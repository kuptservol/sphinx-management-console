package ru.skuptsov.sphinx.console.test.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lnovikova on 06.11.2015.
 */
@Service
public class ConverterService {

    public <T> List<T> convertResponseList(List<T> sourceList, Class targetClass) {
        List<T> list = new ArrayList<T>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object map : sourceList) {
            list.add((T)objectMapper.convertValue(map, targetClass));
        }

        return list;
    }

    public <K, V> Map<K, V> convertResponseMap(Map sourceMap, Class keyClass, Class valueClass) {
        Map<K, V> result = new LinkedHashMap<K, V>();
        ObjectMapper objectMapper = new ObjectMapper();
        K key;
        V value;
        for (Object map : sourceMap.keySet()) {
            key = (K)objectMapper.convertValue(map, keyClass);
            value = (V)objectMapper.convertValue(sourceMap.get(key), valueClass);
            result.put(key, value);
        }

        return result;
    }
}
