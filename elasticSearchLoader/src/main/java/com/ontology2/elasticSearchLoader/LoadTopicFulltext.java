package com.ontology2.elasticSearchLoader;

import org.springframework.stereotype.Component;

@Component("loadTopicFulltext")
public class LoadTopicFulltext extends LoadFulltext {
    @Override
    String getType() {
        return "topic";
    }
}
