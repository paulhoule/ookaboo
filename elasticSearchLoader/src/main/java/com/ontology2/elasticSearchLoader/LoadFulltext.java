package com.ontology2.elasticSearchLoader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.ontology2.centipede.shell.CommandLineApplication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.annotation.Resource;
import java.util.Map;

abstract public class LoadFulltext extends CommandLineApplication {
    @Autowired JdbcTemplate jdbcTemplate;
    @Resource(name="esClient") Client client;
    @Autowired ObjectMapper objectMapper;
    @Autowired String indexName;
    final static int BITE_SIZE=10000;
    final static Log log=LogFactory.getLog(LoadFulltext.class);

    @Override
    protected void _run(String[] strings) throws Exception {
        mysqlScan();
    }

    private void mysqlScan(int from,int to) throws Exception {
        SqlRowSet row = jdbcTemplate
                .queryForRowSet("SELECT id,model1,model3"
                        + " FROM "+getTable()
                        + " WHERE id>=?"
                        + " AND id<?", from, to);

        while(row.next()) {
            index(row.getInt(1),row.getString(2));
        }
    }

    private void mysqlScan() throws Exception {
        int count=jdbcTemplate.queryForInt("SELECT MAX(id) FROM topic");
        for(int i=0;i<count;i+=BITE_SIZE) {
            log.info((100*i)/count+"% done ");
            mysqlScan(i,i+BITE_SIZE);
        }
    }

    private void index(int id, String model1) throws JsonProcessingException {
        index(id,model1,null);
    }

    private void index(int id, String model1, String model3) throws JsonProcessingException {
        Map<String,Object> document= Maps.newHashMap();
        document.put("model1",model1);
        if(model3!=null && !model3.isEmpty()) {
            document.put("model3",model3);
        }
        IndexRequest request=new IndexRequest().id(Integer.toString(id)).source(document).index(indexName).type(getType());
        client.index(request).actionGet();
    }

    String getTable() {
        return getType()+"_fulltext";
    }

    abstract String getType();
}
