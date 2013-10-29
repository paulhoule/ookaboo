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
    GenericTypeahead gta;

    @Override
    protected void _run(String[] arguments) throws Exception {
        long startTime = System.nanoTime();
        gta=createTypeahead(new ClassPathResource("/com/ontology2/cleo4ookaboo/typeahead_config.properties"));
        mysqlScan();
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

        Properties p = new Properties(System.getProperties());
        InputStream inStream = configFile.getInputStream();
        InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
        p.load(reader);
        GenericTypeaheadConfig<TypeaheadElement> config = TypeaheadConfigFactory
                .createGenericTypeaheadConfig(p);
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

    private void mysqlScan() {
        JdbcTemplate t = new JdbcTemplate(dataSource);
        SqlRowSet row = t
                .queryForRowSet("SELECT topic.id,title,slug,quality_score_2,name"
                        + " FROM topic,topic_alt_name"
                        + " WHERE workflow_status>600"
                        + " AND topic.id=topic_alt_name.id");

        int cnt = 0;

        RowGroup g = null;

        row.next();
        while (!row.isAfterLast()) {
            int id = row.getInt(1);
            if (g == null || g.id != id) {
                if (g != null) {
                    flush(g);
                }
                g = new RowGroup();
                g.id = id;
                g.terms = Lists.newArrayList();
                g.title = row.getString(2);
                g.slug = row.getString(3);
                g.score = row.getFloat(4);
            }
            g.terms.add(row.getString(5));
            row.next();
        }

        flush(g);
        System.out.println(cnt);
    }

    private void flush(RowGroup g) {
        TypeaheadElement e=new SimpleTypeaheadElement(g.id);
        e.setLine1(g.title);
        e.setMedia(g.id+"/"+g.slug);
        e.setScore(g.score);
        e.setTerms(g.terms.toArray(new String[] {}));
        e.setTimestamp(System.currentTimeMillis());
    }

}
