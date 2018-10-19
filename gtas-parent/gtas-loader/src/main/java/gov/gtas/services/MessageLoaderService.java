/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.List;

import gov.gtas.parsers.exception.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.services.search.ElasticHelper;

@Service
public abstract class MessageLoaderService {
    @Autowired
    protected LoaderRepository loaderRepo;

    @Autowired
    protected LoaderUtils utils;

    public abstract List<String> preprocess(String message) throws ParseException;
    public abstract MessageVo parse(String message);
    public abstract boolean load(MessageVo parsedMessage);

    protected String filePath = null;
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // indexer

    @Autowired
    protected ElasticHelper indexer;

    protected boolean useIndexer;

	public void setUseIndexer(boolean useIndexer) {
		this.useIndexer = useIndexer;
	}
	public abstract MessageDto parse(MessageDto msgDto);
	public abstract boolean load(MessageDto msgDto);
}
