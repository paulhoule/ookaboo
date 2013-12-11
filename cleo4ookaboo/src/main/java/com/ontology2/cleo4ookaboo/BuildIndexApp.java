package com.ontology2.cleo4ookaboo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import cleo.search.Element;
import cleo.search.SimpleElement;
import cleo.search.SimpleTypeaheadElement;
import cleo.search.TypeaheadElement;
import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.TypeaheadConfigFactory;

import com.google.common.collect.Lists;
import com.ontology2.centipede.shell.CommandLineApplication;

@Component
public class BuildIndexApp extends CommandLineApplication {

    @Autowired
    DriverManagerDataSource dataSource;
    GenericTypeahead<TypeaheadElement> gta;

    @Override
    protected void _run(String[] arguments) throws Exception {
        long startTime = System.nanoTime();
        gta=createTypeahead(new ClassPathResource("/com/ontology2/cleo4ookaboo/typeahead_config.properties"));
        mysqlScan();
        gta.flush();
        List <TypeaheadElement> y=gta.search(1,new String[] {"los"});
        if (y.isEmpty()) {
            System.out.println("====> FAILURE NO SEARCH RESULTS");
            System.exit(99);
        }
        System.out.println("Number of search results: "+y.size());
        long endTime = System.nanoTime();

        long duration = endTime - startTime;

        System.out.println("Took " + duration / 1e9 + " seconds");
    }

    //
    // this has a few improvements over the example given in the cleo examples,  in particular,
    // it bring in System.properties as base properties,  which can be substituted into other
    // properties with the Properties.resolveProperties() method inside cleo
    //
    
    public static GenericTypeahead<TypeaheadElement> createTypeahead(
            Resource configFile) throws Exception {

        Properties p = new Properties();
        p.putAll(System.getProperties());
        InputStream inStream = configFile.getInputStream();
        InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
        p.load(reader);
        System.out.println(p.getProperty("user.home"));
        GenericTypeaheadConfig<TypeaheadElement> config = TypeaheadConfigFactory
                .createGenericTypeaheadConfig(p);
        System.out.println(config.getConnectionsStoreDir());
        config.setSelectorFactory(new ScoredElementSelectorFactory<TypeaheadElement>());

        // Create typeahead initializer
        GenericTypeaheadInitializer<TypeaheadElement> initializer = new GenericTypeaheadInitializer<TypeaheadElement>(
                config);

        return (GenericTypeahead<TypeaheadElement>) initializer.getTypeahead();
    }

    class RowGroup {
        int id = -1;
        List<String> terms;
        String title;
        String slug;
        float score;
    }

    final static int BITE_SIZE=10000;

    private void mysqlScan() throws Exception {
        JdbcTemplate t = new JdbcTemplate(dataSource);
        int count=t.queryForInt("SELECT MAX(id) FROM topic");
        for(int i=0;i<count;i+=BITE_SIZE) {
            mysqlScan(i,i+BITE_SIZE);
        }
    }

    private void mysqlScan(int from,int to) throws Exception {
        JdbcTemplate t = new JdbcTemplate(dataSource);

        SqlRowSet row = t
                .queryForRowSet("SELECT topic.id,title,slug,quality_score_2"
                        + " FROM topic"
                        + " WHERE workflow_status>600"
                        + " AND topic.id>=?"
                        + " AND topic.id<?",from,to);

        while(row.next()) {
            RowGroup g = new RowGroup();
            g.id=row.getInt(1);
            g.title = row.getString(2);
            g.terms = Lists.newArrayList(g.title.toLowerCase());
            g.slug = row.getString(3);
            g.score = row.getFloat(4);
            flush(g);
        }


    }

    private void flush(RowGroup g) throws Exception {
        TypeaheadElement e=new SimpleTypeaheadElement(g.id);
        e.setLine1(g.title);
        e.setLine2("");
        e.setLine3("");
        e.setMedia(g.id+"/"+g.slug);
        e.setScore(g.score);
        e.setTerms(g.terms.toArray(new String[] {}));
        e.setTimestamp(System.currentTimeMillis());
        gta.index(e);
    }

}
