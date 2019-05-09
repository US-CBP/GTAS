/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.vo.MessageVo;

@Service
public abstract class MessageLoaderService {

    @Autowired
    protected GtasLoader loaderRepo;

    @Autowired
    protected LoaderUtils utils;

    public abstract List<String> preprocess(String message);

    public abstract MessageVo parse(String message);

    public abstract boolean load(MessageVo parsedMessage);

    protected String filePath = null;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    protected boolean useIndexer;

    public void setUseIndexer(boolean useIndexer) {
        this.useIndexer = useIndexer;
    }

    public abstract MessageDto parse(MessageDto msgDto);

    public abstract MessageStatus load(MessageDto msgDto);
}
