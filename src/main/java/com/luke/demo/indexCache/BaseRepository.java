package com.luke.demo.indexCache;

import java.util.List;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/17 15:35
 **/
public interface BaseRepository<T, ID> {
    /**
     * 根据主键查询（不同 repository主键不同，因此只能自己实现，不能再抽象类AbstractCacheRepository实现）
     * @param id
     * @return
     */
    T findByKeyId(ID id);

    List<T> getAll();

    T insert(T t);

    boolean update(T t);

    void deleteByKeyId(ID id);


}
