package com.ontology2.cleo4ookaboo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import cleo.search.Element;
import cleo.search.SimpleElement;

import com.google.common.collect.Lists;
import com.ontology2.centipede.shell.CommandLineApplication;

@Component
public class BuildIndexApp extends CommandLineApplication {

	@Autowired DriverManagerDataSource dataSource;
	
	@Override
	protected void _run(String[] arguments) throws Exception {
		long startTime = System.nanoTime();

		mysqlScan();
		long endTime = System.nanoTime();

		long duration = endTime - startTime;
		
		System.out.println("Took "+duration/1e9+ " seconds");
	}

	class RowGroup {
		int id=-1;
		List<String> terms;
		String title;
		String slug;
		float score;
	}
	
	private void mysqlScan() {
		JdbcTemplate t=new JdbcTemplate(dataSource);
		SqlRowSet row=t.queryForRowSet( 
				"SELECT topic.id,title,slug,quality_score_2,name"
				+" FROM topic,topic_alt_name"
				+" WHERE workflow_status>600"
				+" AND topic.id=topic_alt_name.id"
				+" LIMIT 100"
				);
		
		int cnt=0;
			
		RowGroup g=null;
		
		row.next();
		while(!row.isAfterLast()) {
			int id=row.getInt(1);
			if (g==null || g.id!=id) {
				if (g!=null) {
					flush(g);
				}
				g=new RowGroup();
				g.id=id;
				g.terms=Lists.newArrayList();
				g.title=row.getString(2);
				g.slug=row.getString(3);
				g.score=row.getFloat(4);
			}
			g.terms.add(row.getString(5));
			row.next();
		}
		
		flush(g);
		System.out.println(cnt);
	}

	private void flush(RowGroup g) {
		System.out.println(g.id+" "+g.title+" ["+g.score+"]");
		for(String term:g.terms) {
			System.out.println("   "+term);
		}
		
	}

}
