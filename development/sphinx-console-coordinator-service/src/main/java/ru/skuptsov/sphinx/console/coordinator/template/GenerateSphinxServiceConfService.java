package ru.skuptsov.sphinx.console.coordinator.template;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;


@Component
public class GenerateSphinxServiceConfService {
private static final Logger logger = LoggerFactory.getLogger(GenerateSphinxServiceConfService.class);
	
    @Value("${processname}")
    private String processname;
    
    @Value("${path}")
    private String path;
    
    @Value("${wdir}")
    private String wdir;

    @Value("${os.user}")
    private String user;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	public String generateContent(String indexName) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("D", "$");
		model.put("processname", processname);
		model.put("wdir", wdir);
        model.put("user", user);
		model.put("path", path);
		model.put("index_name", indexName);
		
		String templateLocation = "sphinx_service_conf.vm";
		
		String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, "UTF-8",  model);
		content = content.replaceAll("\r\n", "\n");
        logger.info("SPHINX SERVICE CONF GENERATED CONTENT: " + content);
		return content;
	}
	
}
