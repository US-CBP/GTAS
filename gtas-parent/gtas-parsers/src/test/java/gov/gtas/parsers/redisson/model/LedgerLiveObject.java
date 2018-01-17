/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.redisson.model;

import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

import java.util.Date;

@REntity
public class LedgerLiveObject {
    @RId
    private String name;
    private String pnrTag;
    private String messagePayload;
    private Date messageTimeStamp;
    private Date processedTimeStamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPnrTag() {
        return pnrTag;
    }

    public void setPnrTag(String pnrTag) {
        this.pnrTag = pnrTag;
    }

    public String getMessagePayload() {
        return messagePayload;
    }

    public void setMessagePayload(String messagePayload) {
        this.messagePayload = messagePayload;
    }

    public Date getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public void setMessageTimeStamp(Date messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }

    public Date getProcessedTimeStamp() {
        return processedTimeStamp;
    }

    public void setProcessedTimeStamp(Date processedTimeStamp) {
        this.processedTimeStamp = processedTimeStamp;
    }
}
