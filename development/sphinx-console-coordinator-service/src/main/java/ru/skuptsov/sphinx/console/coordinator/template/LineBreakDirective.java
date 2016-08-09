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

public class LineBreakDirective extends Directive {

	@Override
	public String getName() {
		return "lineBreakBlock";
	}

	@Override
	public int getType() {
		return BLOCK;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node)
			throws IOException, ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		
		 // first, parse the entire block and retrieve the content
        StringWriter internalwriter = new StringWriter();

        // the node's children are the arguments and the body.
        // here there should be only one child, since there are no arguments
        node.jjtGetChild(0).render(context, internalwriter);

        String sourcecontent = internalwriter.toString();
        internalwriter.close();

        String[] strs = sourcecontent.split("\\r\\n|\\r|\\n");
		StringBuilder builder = new StringBuilder();
		for (int i = 0 ; i < strs.length; i++) {
			if (i < strs.length - 1) {
			    builder.append(strs[i].trim() + " \\" + System.getProperty("line.separator"));
			} else {
				builder.append(strs[i] + System.getProperty("line.separator"));
			}
		}
		
		String formatted = builder.toString();
		
		if (!formatted.contains("\\")) {
			formatted = formatted.replaceAll("(UNION ALL|union|union all|UNION)", "UNION  ALL \\\\" + System.getProperty("line.separator"));
			
		} 
		
        
        writer.write(formatted);
   
       
		return true;
	}

}
