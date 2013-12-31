package com.ontology2.elasticSearchLoader;

import org.springframework.stereotype.Component;

@Component("loadSourceFulltext")
public class LoadSourceFulltext extends LoadFulltext {
    @Override
    String getType() {
        return "source";
    }
}
