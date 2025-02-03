package com.luke.demo.indexCache;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;

import java.lang.reflect.Method;

/**
 * @Description: M-metaInfo-实体类, ID-metaInfo的key
 * @author: lulu
 * @Date: 2025/1/19 23:56
 **/
public class CQEngineCache<M, ID> {

    private final IndexedCollection<M> indexedCollection;

    public CQEngineCache() {
        this.indexedCollection = new ConcurrentIndexedCollection<>();
    }

    public IndexedCollection<M> getIndexedCollection() {
        return indexedCollection;
    }

    public <ID extends Comparable<ID>> void addIndex(Attribute<M, ID> Attribute) {
        indexedCollection.addIndex(NavigableIndex.onAttribute(Attribute));

        // 另一个复杂而抽象的方式，添加index
//        indexedCollection.addIndex(NavigableIndex.onAttribute(getAttribute(NodeMetaInfo.class, String.class, "nodeId")));
    }

    public void add(M item) {
        indexedCollection.add(item);
    }

    public void remove(M item) {
        indexedCollection.remove(item);
    }

    public ResultSet<M> search(Query<M> query) {
        return indexedCollection.retrieve(query);
    }

    public <ID extends Comparable<ID>> Attribute<M, ID> getAttribute(Class<M> clazz, Class<ID> attributeType, String attributeName) {
        return new SimpleAttribute<M, ID>(clazz, attributeType, attributeName) {
            @Override
            public ID getValue(M object, QueryOptions queryOptions) {
                try {
                    Method method = clazz.getMethod("get" + capitalize(attributeName));
                    return (ID) method.invoke(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
