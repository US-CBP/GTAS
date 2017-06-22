/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

public enum HitTypeEnum {
    R,   // Rule Hit
    P,   // Watchlist Passenger Hit
    D,   // Watchlist Document Hit
    PD,  // Watchlist Passenger and Document Hit
    RPD,  // UDR rule, Watchlist Passenger and Document Hit
    RP,  // UDR rule, Watchlist Passenger Hit
    RD; // UDR rule, Watchlist Document Hit
    
    public HitTypeEnum addHitType(HitTypeEnum hitTypeToAdd){
        HitTypeEnum ret = this;
        switch(hitTypeToAdd){
            case R:
                if(this == P){
                    ret = RP;
                } else if( this == D){
                    ret = RD;
                } else if (this == PD){
                    ret = RPD;
                }
                break;
            case P:
                if(this == R){
                    ret = RP;
                } else if( this == D){
                    ret = PD;
                } else if (this == RD){
                    ret = RPD;
                }
                break;
            case D:
                if(this == P){
                    ret = PD;
                } else if( this == R){
                    ret = RD;
                } else if (this == RP){
                    ret = RPD;
                }
                break;
            case PD:
                if(this == R){
                    ret = RPD;
                }
                break;
            case RP:
                if(this == D){
                    ret = RPD;
                }
                break;
            case RD:
                if(this == P){
                    ret = RPD;
                }
                break;
            default:
                break;
            
        }
        return ret;
    }
}
