package com.lxy.openplatform.search.utils;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.StringUtils;

import java.util.Map;

// es创建查询工具类
public class LogIndexUtils {

    public static void buildMapping(String type, CreateIndexRequest request) throws Exception {
        XContentBuilder builder= JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("app_key").field("type","keyword").endObject()
                .startObject("apiName").field("type","keyword").endObject()
                .startObject("remoteIp").field("type","ip").endObject()
                .startObject("serverIp").field("type","ip").endObject()
                .startObject("receiveTime").field("type","date").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                .startObject("responseTime").field("type","date").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                .startObject("totalTime").field("type","long").endObject()
                .startObject("content").field("type","text").field("analyzer","ik_max_word").endObject()
                .endObject()
                .endObject();
        request.mapping(type,builder);
    }

    /**
     * 根据参数查询的bulder
     */
    public static SearchSourceBuilder getSearchSourceBuilder(Map jsonMap) {
        //如果有分页,我们要求传递的是start rows两个参数
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        int start = (int) jsonMap.get("start"); //分页的信息
        int rows = (int) jsonMap.get("rows");
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(rows);
        //获取关键字
        String requestContent = (String) jsonMap.get("requestContent");

        BoolQueryBuilder booleanQueryBuilder = null;

        if (!StringUtils.isEmpty(requestContent)) {
            //设置了查询的具体条件的值
            booleanQueryBuilder=  booleanQueryBuilder == null ? QueryBuilders.boolQuery() : booleanQueryBuilder;
            booleanQueryBuilder.must(QueryBuilders.matchQuery("content", requestContent));
        }
        String app_key = (String) jsonMap.get("appKeys");

        if (!StringUtils.isEmpty(app_key)) {
            //设置了查询的具体条件的值
            booleanQueryBuilder=  booleanQueryBuilder == null ? QueryBuilders.boolQuery() : booleanQueryBuilder;
            booleanQueryBuilder.must(QueryBuilders.termQuery("app_key", app_key));
        }

        String apiName = (String) jsonMap.get("apiName");

        if (!StringUtils.isEmpty(apiName)) {
            //设置了查询的具体条件的值
            booleanQueryBuilder=  booleanQueryBuilder == null ? QueryBuilders.boolQuery() : booleanQueryBuilder;
            booleanQueryBuilder.must(QueryBuilders.termQuery("apiName", apiName));
        }

        String startTime = (String) jsonMap.get("startTime");

        if (!StringUtils.isEmpty(startTime)) {
            //设置了查询的具体条件的值
            booleanQueryBuilder=  booleanQueryBuilder == null ? QueryBuilders.boolQuery() : booleanQueryBuilder;
            booleanQueryBuilder.must(QueryBuilders.rangeQuery("receiveTime").gt(startTime));
        }

        String endTime = (String) jsonMap.get("endTime");

        if (!StringUtils.isEmpty(endTime)) {
            //设置了查询的具体条件的值
            booleanQueryBuilder=  booleanQueryBuilder == null ? QueryBuilders.boolQuery() : booleanQueryBuilder;
            booleanQueryBuilder.must(QueryBuilders.rangeQuery("responseTime").gt(endTime));
        }

        if (booleanQueryBuilder != null) {
            searchSourceBuilder.query(booleanQueryBuilder);
        }


        return searchSourceBuilder;
    }
}
