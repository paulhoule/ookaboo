package com.ontology2.elasticSearchLoader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.ontology2.centipede.shell.CommandLineApplication;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("loadPictureFulltext")
public class LoadPictureFulltext extends CommandLineApplication {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    Client client;
    ObjectMapper objectMapper;

    @Override
    protected void _run(String[] strings) throws Exception {
        client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

        objectMapper = new ObjectMapper();
        mysqlScan();
        client.close();
    }
    final static int BITE_SIZE=10000;

    private void mysqlScan() throws Exception {
        int count=jdbcTemplate.queryForInt("SELECT MAX(id) FROM topic");
        for(int i=0;i<count;i+=BITE_SIZE) {
            mysqlScan(i,i+BITE_SIZE);
        }
    }

    private void mysqlScan(int from,int to) throws Exception {
        SqlRowSet row = jdbcTemplate
                .queryForRowSet("SELECT id,model1"
                        + " FROM picture_fulltext"
                        + " WHERE id>=?"
                        + " AND id<?", from, to);

        while(row.next()) {
            index(row.getInt(1),row.getString(2));
        }
    }

    private void index(int id, String model1) throws JsonProcessingException {
        index(id,model1,null);
    }

    private void index(int id, String model1, String model3) throws JsonProcessingException {
        Map<String,Object> document= Maps.newHashMap();
        document.put("model1",model1);
        if(model3!=null) {
            document.put("model3",model3);
        }
        IndexRequest request=new IndexRequest().id(Integer.toString(id)).source(document).index("ookaboo").type("picture");
        client.index(request).actionGet();
        System.out.println(id);
    }
}
