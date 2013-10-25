package com.ontology2.cleo4ookaboo;

import org.springframework.stereotype.Component;

import com.ontology2.centipede.shell.CommandLineApplication;

@Component
public class BuildIndexApp extends CommandLineApplication {

	@Override
	protected void _run(String[] arguments) throws Exception {
		System.out.println("foo");
	}

}
