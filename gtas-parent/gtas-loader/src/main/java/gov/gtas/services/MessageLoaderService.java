/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.List;

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

    @Autowired
    protected ElasticHelper indexer;

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
}
