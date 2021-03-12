package com.lxy.openplatform.search.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.search.service.LogIndexService;
import com.lxy.openplatform.search.utils.LogIndexUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 对es的业务操作 存储日志信息到es
* */

@Service
public class LogIndexServiceServiceImpl implements LogIndexService {

    @Value("${spring.data.elasticsearch.index}")
    private String index;

    @Value("${spring.data.elasticsearch.type}")
    private String type;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean existIndex(String index) throws Exception {
        GetIndexRequest request=new GetIndexRequest();
        request.indices(index);
        return restHighLevelClient.indices().exists(request,RequestOptions.DEFAULT);
    }

    @Override
    public void createIndexAndType(String index, String type) throws Exception {
        if (!existIndex(index)) {
            //创建索引和type
            CreateIndexRequest request = new CreateIndexRequest(index);
            //创建mapping
            request.settings(Settings.builder().put("number_of_shards",2).put("number_of_replicas","2"));
            LogIndexUtils.buildMapping(type,request);
            restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        }
    }

    @Override
    public void add(String json) throws Exception {
        IndexRequest request = new IndexRequest(index,type);
        request.source(json, XContentType.JSON);
        restHighLevelClient.index(request,RequestOptions.DEFAULT);
    }

    @Override
    public List<Map> search(String params) throws Exception {

        boolean isHighLight=false;//默认不开启高亮

        List<Map> list = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(index);
        //设置条件
        Map jsonMap = objectMapper.readValue(params, Map.class);
        //获取关键字
        String requestContent = (String) jsonMap.get("requestContent");
        SearchSourceBuilder searchSourceBuilder = LogIndexUtils.getSearchSourceBuilder(jsonMap);

        if (!StringUtils.isEmpty(requestContent)) {
            //只有设置了查询条件才有可能会有高亮
            String highLightPreTag = (String) jsonMap.get("highLightPreTag");
            String highLightPostTag = (String) jsonMap.get("highLightPostTag");
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.requireFieldMatch(false).field("content").preTags(highLightPreTag).postTags(highLightPostTag);
            searchSourceBuilder.highlighter(highlightBuilder);
            isHighLight = true;
        }


        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            if (isHighLight) {
                //如果设置了高亮
                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                HighlightField highlightField = highlightFields.get("content");
                if (highlightField != null) {
                    Text[] fragments = highlightField.getFragments();

                    if (fragments != null) {
                        String highLightString = fragments[0].toString();
                        sourceAsMap.put("content", highLightString);//用高亮替换原始数据
                    }
                }
            }
            list.add(sourceAsMap);
        }

        return list;
    }

    @Override
    public long count(String params) throws Exception {
        Map jsonMap = objectMapper.readValue(params, Map.class);
        SearchSourceBuilder searchSourceBuilder = LogIndexUtils.getSearchSourceBuilder(jsonMap);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits().getTotalHits();
    }

    @Override
    public Map<String, Integer> statAvg(String receiveStartTime, String receiveStopTime) throws Exception {

        Map<String, Integer> result = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.rangeQuery("receiveTime").from(receiveStartTime,true).to(receiveStopTime,true));
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("apiName").field("apiName");//因为每位有很多不同的接口,需要区分,按照什么方式区分
        aggregationBuilder.subAggregation(AggregationBuilders.count("count").field("apiName"));
        aggregationBuilder.subAggregation(AggregationBuilders.avg("avg_request_time").field("totalTime"));
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        Terms aggregation = aggregations.get("apiName");
        List<? extends Terms.Bucket> buckets = aggregation.getBuckets();


        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();//服务的id
            Avg avg_request_time = bucket.getAggregations().get("avg_request_time");
            Double value = avg_request_time.getValue();//平均值
            result.put(key.toString(), value.intValue());
        }

        return result;
    }
}
