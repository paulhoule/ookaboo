package com.ontology2.elasticSearchLoader;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component("loadPictureFulltext")
public class LoadPictureFulltext extends LoadFulltext {
    @Override
    String getType() {
        return "picture";
    }
}
