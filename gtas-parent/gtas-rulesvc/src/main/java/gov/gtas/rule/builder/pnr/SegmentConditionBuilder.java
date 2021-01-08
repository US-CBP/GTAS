package gov.gtas.rule.builder.pnr;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.rule.builder.EntityConditionBuilder;

import java.text.ParseException;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;

public class SegmentConditionBuilder  extends EntityConditionBuilder {

    public SegmentConditionBuilder(final String drlVariableName) {
        super(drlVariableName, EntityEnum.SAVED_SEGMENT.getEntityName());
    }

    @Override
    protected void addSpecialConditions(StringBuilder bldr) {
    }

    public String getLinkVariableName() {
        return getDrlVariableName() + LINK_VARIABLE_SUFFIX;
    }

    @Override
    public void addCondition(final CriteriaOperatorEnum opCode, final String attributeName,
                             final TypeEnum attributeType, String[] values) throws ParseException {
        if (this.isEmpty()) {
            this.addConditionAsString("id == " + this.getLinkVariableName() + ".linkAttributeId");
        }
        super.addCondition(opCode, attributeName, attributeType, values);
    }
}
