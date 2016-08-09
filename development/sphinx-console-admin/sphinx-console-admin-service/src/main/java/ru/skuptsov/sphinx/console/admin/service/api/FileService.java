package ru.skuptsov.sphinx.console.admin.service.api;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public interface FileService {
	public Reader getReader(String prefixChangesetPath, String path) throws FileNotFoundException, UnsupportedEncodingException;

	public String getFileName(String path);

    public String getFileDirPath(String path);

    public String readFile(String prefixChangesetPath, String path);

    public boolean isFileValid(String path);
}
