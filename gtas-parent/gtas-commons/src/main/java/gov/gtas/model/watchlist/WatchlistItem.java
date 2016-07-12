/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.watchlist;

import gov.gtas.constant.DomainModelConstants;
import gov.gtas.model.BaseEntity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "wl_item")
public class WatchlistItem extends BaseEntity {
    private static final long serialVersionUID = 3593L;  
    
    public WatchlistItem() { }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ITM_WL_REF", referencedColumnName="ID", nullable = false)     
    private Watchlist watchlist;
    
    @Column(name = "ITM_DATA", nullable=false, length = DomainModelConstants.WL_ITEM_DATA_COLUMN_SIZE)
    private String itemData;
    
    @Column(name = "ITM_RL_DATA", nullable=true, length = DomainModelConstants.WL_RULE_DATA_COLUMN_SIZE)
    private String itemRuleData;
    
    
    /**
     * @return the watch list
     */
    public Watchlist getWatchlist() {
        return watchlist;
    }

    /**
     * @param watchlist the watchlist to set
     */
    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    /**
     * @return the itemData
     */
    public String getItemData() {
        return itemData;
    }

    /**
     * @param itemData the itemData to set
     */
    public void setItemData(String itemData) {
        this.itemData = itemData;
    }

    /**
     * @return the itemRuleData
     */
    public String getItemRuleData() {
        return itemRuleData;
    }

    /**
     * @param itemRuleData the itemRuleData to set
     */
    public void setItemRuleData(String itemRuleData) {
        this.itemRuleData = itemRuleData;
    }

    @Override
    public int hashCode() {
       return Objects.hash(this.itemData);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WatchlistItem other = (WatchlistItem)obj;
        return Objects.equals(this.itemData, other.itemData);
    }    
}
