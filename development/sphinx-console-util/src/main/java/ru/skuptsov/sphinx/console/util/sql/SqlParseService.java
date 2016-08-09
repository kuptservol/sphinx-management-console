package ru.skuptsov.sphinx.console.util.sql;

import java.util.*;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.exception.SqlApplicationException;

@Component
public class SqlParseService {
	private static final Logger logger = LoggerFactory.getLogger( SqlParseService.class);
	
    private static Map<String, String> reservedWordsToTempReplacements = new HashMap<String, String>();
    
    private static Map<String, String> reservedPhrasesToTempReplacements = new HashMap<String, String>();
	
	static {
		reservedWordsToTempReplacements.put("key", "key_");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1000/1000000::numeric) at time zone 'UTC'", "66666");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1001/1000000::numeric) at time zone 'UTC'", "77777");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1000 / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'", "88888");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1001 / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'", "99999");
		
		reservedPhrasesToTempReplacements.put("to_timestamp(-1000/1000000 - 1)::timestamp without time zone", "44444");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1001/1000000 + 1)::timestamp without time zone", "55555");
		
		
	}
	
	private static Map<String, String> tempReplacementsToReservedWords = new HashMap<String, String>();
	
	private static Map<String, String> tempReplacementsToReservedPhrases = new HashMap<String, String>();
	
	static {
		tempReplacementsToReservedWords.put("key_", "key");
		tempReplacementsToReservedPhrases.put("66666", "to_timestamp($start/1000000::numeric) at time zone 'UTC'");
		tempReplacementsToReservedPhrases.put("77777", "to_timestamp($end/1000000::numeric) at time zone 'UTC'");
		tempReplacementsToReservedPhrases.put("88888", "to_timestamp($start / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'");
		tempReplacementsToReservedPhrases.put("99999", "to_timestamp($end / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'");
		
		tempReplacementsToReservedPhrases.put("44444", "to_timestamp($start/1000000 - 1)::timestamp without time zone");
		tempReplacementsToReservedPhrases.put("55555", "to_timestamp($end/1000000 + 1)::timestamp without time zone");
	}
	
	public void parseSqlQuery(String sqlQuery) {
        String queryToBeParsed = sqlQuery;
		try {
        	
        	for (String key : reservedWordsToTempReplacements.keySet()) {
        		queryToBeParsed = queryToBeParsed.replaceAll("\\b" + key + "\\b", reservedWordsToTempReplacements.get(key));
			}
			
        	queryToBeParsed = queryToBeParsed.replace("$start", "-1000");
        	queryToBeParsed = queryToBeParsed.replace("$end", "-1001");
			
			for (String key : reservedPhrasesToTempReplacements.keySet()) {
				queryToBeParsed = queryToBeParsed.replace(key, reservedPhrasesToTempReplacements.get(key));
			}
        	
			CCJSqlParserUtil.parse(queryToBeParsed);
			
			// TO DO
			//PostgresqlSelectDeparser parser = new PostgresqlSelectDeparser();
			//http://www.google.ru/url?url=http://sparqlmap.googlecode.com/hg-history/e2133cd85884f1a97da8edf672cdbe335197d07c/src/main/java/org/aksw/sparqlmap/config/syntax/DBAccess.java&rct=j&frm=1&q=&esrc=s&sa=U&ei=5jGWVYO3LaGAywOHhInQDg&ved=0CBkQFjAB&usg=AFQjCNFWQWCa1mhufEkkYMPq16_vdnU7_g
		    //http://code.google.com/r/rithuparnars-raj/source/browse/src/main/java/org/aksw/sparqlmap/config/syntax/DBConnectionConfiguration.java?name=6c89d4c0d0077655ae9230cf4f17dd0d6a4f3f81&r=5be7763966f4e101d4b6f3412802c28c5ae3dbfd&spec=svn6c89d4c0d0077655ae9230cf4f17dd0d6a4f3f81
        } catch (JSQLParserException e) {
			logger.error("ERROR OCCURED WHILE PARSING SQL: ", e);
			logger.info("SQL: " + sqlQuery);
			throw new SqlApplicationException(org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage(e));
		}
			
	}
	
	public List<String> getSelectFields(String sqlQuery) {
        final List<String> fields = new LinkedList<String>();
        /*парсер валидирует запросы в соответствии со стандартом языка SQL,
        поэтому часто не может обработать запросы действительно валидные в определенной СУБД.
        * поэтому парсим селект*/
         String selectQuery = sqlQuery.substring(sqlQuery.indexOf("select") + 7, sqlQuery.indexOf("from"));
        for(String field : selectQuery.split(",")){
            field = field.trim();
            if(field.contains(" ")){
				String[] words = field.split("\\s+");
                field = words[words.length-1].trim();
            }
            fields.add(field);
        }


/*
		//предыдущая реализация определения полей в запросе, основанная на парсинге запроса в соответствии с стандартом SQL95
		Select select = null;
        try {
        	
        	for (String key : reservedWordsToTempReplacements.keySet()) {
        		sqlQuery = sqlQuery.replaceAll("\\b" + key + "\\b", reservedWordsToTempReplacements.get(key));
			}
			
        	sqlQuery = sqlQuery.replace("$start", "-1000");
        	sqlQuery = sqlQuery.replace("$end", "-1001");
			
			for (String key : reservedPhrasesToTempReplacements.keySet()) {
				sqlQuery = sqlQuery.replace(key, reservedPhrasesToTempReplacements.get(key));
			}
        	
			select = (Select)CCJSqlParserUtil.parse(sqlQuery);
			
			// TO DO
			//PostgresqlSelectDeparser parser = new PostgresqlSelectDeparser();
			//http://www.google.ru/url?url=http://sparqlmap.googlecode.com/hg-history/e2133cd85884f1a97da8edf672cdbe335197d07c/src/main/java/org/aksw/sparqlmap/config/syntax/DBAccess.java&rct=j&frm=1&q=&esrc=s&sa=U&ei=5jGWVYO3LaGAywOHhInQDg&ved=0CBkQFjAB&usg=AFQjCNFWQWCa1mhufEkkYMPq16_vdnU7_g
		    //http://code.google.com/r/rithuparnars-raj/source/browse/src/main/java/org/aksw/sparqlmap/config/syntax/DBConnectionConfiguration.java?name=6c89d4c0d0077655ae9230cf4f17dd0d6a4f3f81&r=5be7763966f4e101d4b6f3412802c28c5ae3dbfd&spec=svn6c89d4c0d0077655ae9230cf4f17dd0d6a4f3f81
        } catch (JSQLParserException e) {
			e.printStackTrace();
			logger.error("ERROR OCCURED WHILE PARSING SQL: ", e);
			logger.info("SQL: " + sqlQuery);
			throw new SqlApplicationException(org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage(e));
		}
        
    
        // start processing
        select.getSelectBody().accept(new SelectVisitor() {

        		    @Override
        		    public void visit(PlainSelect plain) {
        		      
        		    	for (final SelectItem item: plain.getSelectItems()) {
        					 
        					item.accept(new SelectItemVisitorAdapter() {
        						private String columnName = null;
        						
        						@Override
        						public void visit(SelectExpressionItem column) {
        							
        							columnName = column.getAlias() != null ?column.getAlias().getName():null;
        							
        							column.getExpression().accept(new ExpressionVisitorAdapter() {
        								@Override
        								public void visit(Column column) {
        									if (columnName == null) {
        									    columnName = column.getColumnName();
        									}
        								}
        								
        		                        public void visit(CaseExpression caseExpression) {
        									logger.info("CASE EXPRESSION: " + caseExpression);
        								}

        							});
        							
        							fields.add(columnName);
        							
        							
        						}
        						
        					});
        		    	}
        		    	
        		    }

        		    @Override
        		    public void visit(SetOperationList sol) {
        		        logger.info("visitor:" + sol.getPlainSelects());
        		        logger.info("visitor:" + sol.getOperations());
        		        
        		        List<PlainSelect> plainSelects = sol.getPlainSelects();
        		        
        		        for (PlainSelect plain : plainSelects) {
        		        	visit(plain);
        		        }
        		    }

        		    @Override
        		    public void visit(WithItem wi) {
        		        
        		    }
        		});
        		// end processing

		*/
		return fields;
	}
}
