package com.ontology2.cleo4ookaboo;

import cleo.search.Hit;
import cleo.search.TypeaheadElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.TypeaheadConfigFactory;
import com.ontology2.centipede.shell.CommandLineApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

public class RunQueryApp extends CommandLineApplication {
    GenericTypeahead gta;

    @Override
    protected void _run(String[] arguments) throws Exception {
        long startTime = System.nanoTime();
        gta=createTypeahead(new ClassPathResource("/com/ontology2/cleo4ookaboo/typeahead_config.properties"));
        doQuery();
        long endTime = System.nanoTime();

        long duration = endTime - startTime;

        System.out.println("Took " + duration / 1e9 + " seconds");
    }

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

    public void doQuery() {
        System.out.println(gta.getMaxElementScore());
        Collector<TypeaheadElement> collector;
        collector=new SortedCollector<TypeaheadElement>(10,100);
        gta.search(0,new String[] {"los"},collector);
        for(Hit<TypeaheadElement> hit:collector.hits()) {
            System.out.println(hit.getScore()+ " "+ hit.getElement().getLine1());
        }
    };
}
