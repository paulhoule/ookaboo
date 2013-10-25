package com.ontology2.cleo4ookaboo;

import java.util.List;

import com.google.common.collect.Lists;
import com.ontology2.centipede.shell.CentipedeShell;

public class Main extends CentipedeShell {

    public List<String> getApplicationContextPath() {
        return Lists.newArrayList("com/ontology2/cleo4ookaboo/applicationContext.xml");
    }

    @Override
    public String getShellName() {
        return "cleo4ookaboo";
    }
    
    public static void main(String[] args) {
        new Main().run(args);
    }
}
