package ru.skuptsov.sphinx.console.sphinx.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.linux.service.LinuxAtomaryCommands;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSphinxService implements SphinxService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${base.dir}")
    private String baseDirPath;

    @Value("${sudo.path}")
    protected String sudoPath;

    @Autowired
	protected LinuxAtomaryCommands linuxAtomaryCommands;


    public void copyAndRenameFiles(String processName, String indexFileName) throws IOException {
        copyAndRenameFiles("data/indexing/" + processName + "/",
                           "data/searching/" + processName + "/",
                           indexFileName + "_delta",
                           indexFileName + "_delta.new");
    }

    public void copyIndexFiles(String indexFileName, String fromServer, String toServer, String fromDir, String toDir, String indexType, boolean isStrictCopy) throws IOException {
        List<IndexType> indexTypes = new ArrayList<IndexType>();
        
        logger.info("INDEX TYPE: " + indexType);
        logger.info("INDEX FILE NAME: " + indexFileName);
        logger.info("IS STRICT COPY: " + isStrictCopy);
        logger.info("FROM SERVER: " + fromServer);
        logger.info("TO SERVER: " + toServer);
        
        String[] extentions = new String[]{".spa",".spd",".spe",".sph",".spi",".spk",".spm",".spp",".sps"};
        
        if (indexType.equals(IndexType.ALL.getTitle())) {
            indexTypes.addAll(IndexType.getDeltaAndMainList());
        } else {
            indexTypes.add(IndexType.getByTitle(indexType));
        }
        
        for(IndexType type : indexTypes){
        	String fromIndexFileName = "";
        	if(!isStrictCopy) {
                
        		for (String extention : extentions) {
        		    String file = baseDirPath + fromDir + indexFileName + "_" + type.getTitle() + extention;
        		    logger.info("EXISTED FILE: " + file);
        		    
        		    
        		    String newFile = baseDirPath + fromDir + indexFileName + "_" +  type.getTitle() + ".new" + extention;
        		    logger.info("TARGET FILE: " + newFile);
        		    
        		    linuxAtomaryCommands.createFileWithNewName(file, newFile);
        		}
        		
        		fromIndexFileName = "$(find " + baseDirPath + fromDir + " -type f -name '" + indexFileName + "*" + type.getTitle() + ".new*')";
        		//scp -r -C -c  blowfish $(find  /opt/sphinx/data/indexing/new_series_14_1/ -type f  -name 'new_series_14*delta.new*')  /opt/sphinx/data/searching/new_series_14_3/
        		logger.info("COPY FROM EXPRESSION: " + fromIndexFileName); 
        		
        		//fromIndexFileName = indexFileName + "_" + type.getTitle() + ".new" + "*.*";	
            } else {
            	
            	fromIndexFileName = "$(find " + baseDirPath + fromDir + " -type f -name '" + indexFileName + "*" + type.getTitle() +  "*' ! -name '" + indexFileName + "*" + type.getTitle() + ".new*')";
            	// scp -r -C -c  blowfish $(find  /opt/sphinx/data/indexing/new_series_14_1/ -type f  -name 'new_series_14*delta*' ! -name 'new_series_14*delta.new*')  /opt/sphinx/data/searching/new_series_14_3/

            	logger.info("COPY FROM EXPRESSION: " + fromIndexFileName);
            	
            	//fromIndexFileName = indexFileName + "_" + type.getTitle() + "*.*";	
            }
        	
        	
            if(!fromServer.equals(toServer)){
                linuxAtomaryCommands.scpCopyIndexFiles(fromIndexFileName, fromDir, toDir, fromServer, toServer);
            } else{
                linuxAtomaryCommands.scpCopyIndexFiles(fromIndexFileName, fromDir, toDir);
            }
        }
    }

    public void copyAndRenameFiles(String fromDir, String toDir, String oldProcessName, String newProcessName) throws IOException {
        linuxAtomaryCommands.copyAndRenameFiles(fromDir,
                                                toDir,
                                                oldProcessName + ".spa " + newProcessName + ".spa",
                                                oldProcessName + ".spd " + newProcessName + ".spd",
                                                oldProcessName + ".spe " + newProcessName + ".spe",
                                                oldProcessName + ".sph " + newProcessName + ".sph",
                                                oldProcessName + ".spi " + newProcessName + ".spi",
                                                oldProcessName + ".spk " + newProcessName + ".spk",
                                                oldProcessName + ".spm " + newProcessName + ".spm",
                                                oldProcessName + ".spp " + newProcessName + ".spp",
                                                oldProcessName + ".sps " + newProcessName + ".sps");
    }

    public void scpAndRenameFiles(String fromDir, String toDir, String processName) throws IOException {
        linuxAtomaryCommands.scpAndRenameFiles(fromDir,
                                               toDir,
                                                processName + ".spa ",
                                                processName + ".spd ",
                                                processName + ".spe ",
                                                processName + ".sph ",
                                                processName + ".spi ",
                                                processName + ".spk ",
                                                processName + ".spm ",
                                                processName + ".spp ",
                                                processName + ".sps ");
    }

   
}
