package com.xqh.financial.utils;

import com.google.common.base.Preconditions;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/5/3.
 */
public class ExampleBuilder {

    /**
     * 分隔符
     */
    public static final String SEPARATION = "_";

    public Example example;

    private Search search;

    private Fields fields;

    private Sort sort;

    public Class<?> entityClass;

    public ExampleBuilder(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.example = new Example(entityClass);
    }

    public ExampleBuilder search(Search search)
    {
        this.search = search;
        return this;
    }

    public ExampleBuilder search(Map<String, Object> search)
    {
        this.search = new Search(search);
        return this;
    }

    public ExampleBuilder fields(Fields fields) {
        this.fields = fields;
        return this;
    }

    public ExampleBuilder sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public ExampleBuilder sort(List<String> sort) {
        this.sort = new Sort(sort);
        return this;
    }

    public Example build() {
        buildFields();
        buildSearch();
        buildSort();
        return this.example;
    }

    private void buildSearch() {

        Example.Criteria criteria = this.example.createCriteria();

        if(null != this.search) {
            for (String key : this.search.keySet()) {

                int index = key.lastIndexOf(SEPARATION);
                Preconditions.checkArgument(index != -1 && index != 0 && index != key.length()-1);

                String property = key.substring(0, index);
                ConditionEunm conditionEunm = ConditionEunm.valueOf(key.substring(index+1, key.length()));

                switch (conditionEunm) {
                    case eq:
                        criteria.andEqualTo(property, search.get(key));
                        break;
                    case neq:
                        criteria.andNotEqualTo(property, search.get(key));
                        break;
                    case gt:
                        criteria.andGreaterThan(property, search.get(key));
                        break;
                    case gte:
                        criteria.andGreaterThanOrEqualTo(property, search.get(key));
                        break;
                    case lt:
                        criteria.andLessThan(property, search.get(key));
                        break;
                    case lte:
                        criteria.andLessThanOrEqualTo(property, search.get(key));
                        break;
                    case in:
                        criteria.andIn(property, (Iterable) search.get(key));
                        break;
                    case nin:
                        criteria.andNotIn(property, (Iterable) search.get(key));
                        break;
                    case like:
                        criteria.andLike(property, "%" + (String) search.get(key) + "%");
                        break;
                    case nlike:
                        criteria.andLike(property, (String) search.get(key));
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("search条件%s有误", key));
                }

            }
        }


    }

    private void buildFields() {

        if(null != this.fields && this.fields.size() > 0) {

            String[] properties = this.fields.toArray(new String[this.fields.size()]);
            this.example.selectProperties(properties);
        }

    }

    private void buildSort() {
        if(null != this.sort && this.sort.size() > 0) {
            for (String s : this.sort) {

                int index = s.lastIndexOf(SEPARATION);
                Preconditions.checkArgument(index != -1 && index != 0 && index != s.length()-1);

                String property = s.substring(0, index);
                OrderEnum orderEnum = OrderEnum.valueOf(s.substring(index+1, s.length()));
                switch (orderEnum) {
                    case asc:
                        this.example.orderBy(property).asc();
                        break;
                    case desc:
                        this.example.orderBy(property).desc();
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("order条件%s有误", s));
                }


            }
        }
    }

    public enum  ConditionEunm {
        eq,
        neq,
        gt,
        gte,
        lt,
        lte,
        in,
        nin,
        like,
        nlike
    }

    public enum OrderEnum {
        asc,
        desc
    }

}
