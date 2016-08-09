package ru.skuptsov.sphinx.console.coordinator.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.Distributed;
import ru.skuptsov.sphinx.console.coordinator.task.DistributedTask;

@Component
public class GenerateDistributedSphinxConfService {
private static final Logger logger = LoggerFactory.getLogger(GenerateDistributedSphinxConfService.class);
	
	@Value("${base.dir}")
    private String baseDir;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	public String generateContent(Task t) {
		Distributed task = null;
		if (t != null) {
		    task = (DistributedTask)t;
		} 
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		
		model.put("root_index_name", task.getCollection().getName());
		model.put("index_name", task.getProcessName());
		model.put("agentConfigs", task.getAgentConfigs());
		
		logger.info("AGENT CONFIGS: " + task.getAgentConfigs());
		
		Configuration configuration = task.getSearchConfiguration();
		String templateLocation = "sphinx_distributed_conf.vm";
		String pathPrefix = "searching";
		
		model.put("pathPrefix", pathPrefix);
		model.put("baseDir", baseDir);
		
		if (configuration.getSearchConfigurationTemplate() != null) {
            Set<ConfigurationFields> searchConfigurationFields = configuration.getSearchConfigurationTemplate().getConfigurationFields();
            ConfigurationFields port = new ConfigurationFields();
            port.setFieldKey("listen");
            port.setFieldValue(task.getSearchConfigurationPort() + ":mysql41");
            searchConfigurationFields.add(port);
            
		    model.put("searchd", searchConfigurationFields);
		}
		if (configuration.getConfigurationTemplate() != null) {
		    model.put("index", configuration.getConfigurationTemplate().getConfigurationFields());
		}
		
		String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, "UTF-8",  model);
        logger.info("SPHINX CONF GENERATED CONTENT: " + content);
		return content;
	}

}
