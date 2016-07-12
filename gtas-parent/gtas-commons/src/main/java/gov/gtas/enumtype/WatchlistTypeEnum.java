/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

public enum WatchlistTypeEnum {
   P("Passenger"),
   D("Document");
   
   private String watchlistEntityName;
   private WatchlistTypeEnum(String entityName){
       this.watchlistEntityName = entityName;
   }
    /**
     * @return the watchlistEntityName
     */
    public String getWatchlistEntityName() {
        return watchlistEntityName;
    }
}
