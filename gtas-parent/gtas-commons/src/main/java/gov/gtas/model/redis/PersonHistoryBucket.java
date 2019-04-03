/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.model.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author gbays
 */
public class PersonHistoryBucket 
{
 
    private Map<String, Integer> inboundMap = new HashMap<>();
  
    private Map<String, Integer> outboundMap = new HashMap<>();


    public Map<String, Integer> getInboundMap() {
        return inboundMap;
    }

    public void setInboundMap(Map<String, Integer> inboundMap) {
        this.inboundMap = inboundMap;
    }

    public Map<String, Integer> getOutboundMap() {
        return outboundMap;
    }

    public void setOutboundMap(Map<String, Integer> outboundMap) {
        this.outboundMap = outboundMap;
    }
    
    
  
  
}
