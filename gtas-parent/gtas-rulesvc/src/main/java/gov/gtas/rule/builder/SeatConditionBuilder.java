/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;


public class SeatConditionBuilder extends EntityConditionBuilder {
    
    private boolean apis;
    public SeatConditionBuilder(final String drlVariableName, final boolean apis){
        super(drlVariableName, RuleTemplateConstants.SEAT_ENTITY_NAME);
        this.apis = apis;
    }
    public void addApisCondition(){
        if(isApis()){
            super.addConditionAsString("apis == true");
        } else {
            super.addConditionAsString("apis == false");            
        }
    }
    @Override
    protected void addSpecialConditions(StringBuilder bldr) {
    }
    /**
     * @return the apis
     */
    public boolean isApis() {
        return apis;
    }
    
}
