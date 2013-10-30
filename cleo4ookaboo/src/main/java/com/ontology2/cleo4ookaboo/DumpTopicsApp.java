package com.ontology2.cleo4ookaboo;

import com.ontology2.centipede.shell.CommandLineApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;

public class DumpTopicsApp extends CommandLineApplication {
    @Autowired
    DriverManagerDataSource dataSource;

    @Override
    protected void _run(String[] arguments) throws Exception {
        mysqlScan();
    }

    private void mysqlScan() {
        JdbcTemplate t = new JdbcTemplate(dataSource);
        List<String> col = t
                .queryForList("SELECT dbpedia_resource"
                        + " FROM topic"
                        + " WHERE workflow_status>600"
                        + " AND dbpedia_resource IS NOT NULL", String.class);

        String cutPrefix="http://dbpedia.org/resource/";
        for(String s:col) {
            if (s.startsWith(cutPrefix)) {
                System.out.println(s.substring(cutPrefix.length()));
            }
        };
    }

}
