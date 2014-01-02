package com.ontology2.elasticSearchLoader;

import com.ontology2.centipede.shell.CommandLineApplication;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("searchFor")
public class SearchFor extends CommandLineApplication {
    @Resource(name="esClient")
    Client client;

    @Override
    protected void _run(String[] strings) throws Exception {
        String type=strings[0];
        String query=strings[1];

        SearchResponse response=client.prepareSearch("ookaboo")
                .setTypes(type)
                .setSearchType(SearchType.DEFAULT)
                .setQuery(QueryBuilders.matchQuery("_all",query))
                .setFrom(0).setSize(20)
                .execute()
                .actionGet();

        System.out.println(response);
    }
}
