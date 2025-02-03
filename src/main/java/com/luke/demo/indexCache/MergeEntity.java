package com.luke.demo.indexCache;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/17 17:45
 **/
public interface MergeEntity<T> {
    T merge(T t);
}
