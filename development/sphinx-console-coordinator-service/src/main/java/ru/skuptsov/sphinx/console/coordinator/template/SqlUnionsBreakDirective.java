package ru.skuptsov.sphinx.console.coordinator.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

public class SqlUnionsBreakDirective extends Directive {
	@Override
	public String getName() {
		return "sqlUnionsBreakBlock";
	}

	@Override
	public int getType() {
		return BLOCK;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node)
			throws IOException, ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		
		StringWriter internalwriter = new StringWriter();

        node.jjtGetChild(0).render(context, internalwriter);

        String sourcecontent = internalwriter.toString();
        internalwriter.close();
        
        
        if (!sourcecontent.contains("\\")) {
        	sourcecontent = sourcecontent.replaceAll("(UNION ALL|union|union all|UNION)", "UNION  ALL \\\\" + System.getProperty("line.separator"));
			
		} 
		
        writer.write(sourcecontent);
   
       
		return true;
	}
}
