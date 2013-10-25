package com.ontology2.cleo4ookaboo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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

	private void mysqlScan() {
		JdbcTemplate t=new JdbcTemplate(dataSource);
		SqlRowSet row=t.queryForRowSet( 
				"SELECT topic.id,title,slug,quality_score_2,name"
				+" FROM topic,topic_alt_name"
				+" WHERE workflow_status>600"
				+" AND topic.id=topic_alt_name.id"
				);
		
		int cnt=0;
		while(!row.isAfterLast()) {
			cnt++;
			if (cnt%1000==0) {
				System.out.print(cnt);
				System.out.print(' ');
				System.out.println(row.getString(2));
				}
			row.next();
		}
		
		System.out.println(cnt);
	}

}
