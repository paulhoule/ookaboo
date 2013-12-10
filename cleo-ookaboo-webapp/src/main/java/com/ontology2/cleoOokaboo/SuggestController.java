package com.ontology2.cleoOokaboo;

import cleo.search.Hit;
import cleo.search.TypeaheadElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.typeahead.Typeahead;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SuggestController {

    @Autowired
    Typeahead<TypeaheadElement> typeahead;

    @RequestMapping(value="/suggest",produces="application/json",method= RequestMethod.GET)
    public @ResponseBody List<Result> suggest(@RequestParam String term) {
        Collector<TypeaheadElement> collector;
        collector=new SortedCollector<TypeaheadElement>(20,100);
        typeahead.search(1,new String[] {term.toLowerCase()},collector);
        List<Result> results= Lists.newArrayList();
        for(final Hit<TypeaheadElement> h:collector.hits()) {
            results.add(new Result() {{
                id=h.getElement().getElementId();
                label=h.getElement().getLine1();
                value=h.getElement().getLine1();
            }});
        }

        return results;
    }
}
