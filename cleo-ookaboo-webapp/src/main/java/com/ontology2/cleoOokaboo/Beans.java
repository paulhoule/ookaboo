package com.ontology2.cleoOokaboo;

import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.TypeaheadElement;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.TypeaheadConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Configuration
public class Beans {
    @Autowired
    ClassPathResource configFile;

    @Bean
    public GenericTypeahead<TypeaheadElement> typeahead() throws Exception {
        Properties p = new Properties();
        p.putAll(System.getProperties());
        InputStream inStream = configFile.getInputStream();
        InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
        p.load(reader);
        GenericTypeaheadConfig<TypeaheadElement> config = TypeaheadConfigFactory
                .createGenericTypeaheadConfig(p);

        config.setSelectorFactory(new ScoredElementSelectorFactory<TypeaheadElement>());
        GenericTypeaheadInitializer<TypeaheadElement> initializer = new GenericTypeaheadInitializer<TypeaheadElement>(
                config);

        return (GenericTypeahead<TypeaheadElement>) initializer.getTypeahead();
    }
}
