/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.watchlist.json.validation;

import static org.junit.Assert.*;

import java.util.List;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.error.CommonValidationException;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.util.SampleDataGenerator;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.FieldError;

public class WatchlistValidationAdapterTest {
    private static final String WL_NAME1 = "Hello WL 1";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testValidateWatchlistSpec() {
        try{
        WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
        WatchlistValidationAdapter.validateWatchlistSpec(spec);
        } catch (Exception  ex){
            ex.printStackTrace();
            fail("Not expecting exception");
        }
    }
    @Test
    public void testMissingName() {
        try{
            WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
            spec.setName(StringUtils.EMPTY);
            WatchlistValidationAdapter.validateWatchlistSpec(spec);
            fail("Expecting exception");
        } catch (CommonValidationException  ex){
            assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, ex.getErrorCode());
            List<FieldError> fieldErrors = ex.getValidationErrors().getFieldErrors();
            assertEquals(1, fieldErrors.size());
            assertEquals(WatchlistConstants.WL_NAME_FIELD, fieldErrors.get(0).getField());
        }
    }

    @Test
    public void testMissingEntity() {
        try{
            WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
            spec.setEntity(StringUtils.EMPTY);
            WatchlistValidationAdapter.validateWatchlistSpec(spec);
            fail("Expecting exception");
        } catch (CommonValidationException  ex){
            assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, ex.getErrorCode());
            List<FieldError> fieldErrors = ex.getValidationErrors().getFieldErrors();
            assertEquals(1, fieldErrors.size());
            assertEquals(WatchlistConstants.WL_ENTITY_FIELD, fieldErrors.get(0).getField());
        }
    }
    @Test
    public void testInvalidEntity() {
        try{
            WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
            spec.setEntity("foobar");
            WatchlistValidationAdapter.validateWatchlistSpec(spec);
            fail("Expecting exception");
        } catch (CommonValidationException  ex){
            assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, ex.getErrorCode());
            List<FieldError> fieldErrors = ex.getValidationErrors().getFieldErrors();
            assertEquals(1, fieldErrors.size());
            assertEquals(WatchlistConstants.WL_ENTITY_FIELD, fieldErrors.get(0).getField());
        }
    }
    @Test
    public void testEmptyItemList() {
        try{
            WatchlistSpec spec = new WatchlistSpec(WL_NAME1, "passenger");
            WatchlistValidationAdapter.validateWatchlistSpec(spec);
            fail("Expecting exception");
        } catch (CommonValidationException  ex){
            assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, ex.getErrorCode());
            List<FieldError> fieldErrors = ex.getValidationErrors().getFieldErrors();
            assertEquals(1, fieldErrors.size());
            assertEquals(WatchlistConstants.WL_ITEMS_FIELD, fieldErrors.get(0).getField());
        }
    }
    public void testInvalidAction() {
        try{
            WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
            WatchlistItemSpec itm = spec.getWatchlistItems().get(0);
            itm.setAction("C");
            WatchlistValidationAdapter.validateWatchlistSpec(spec);
            fail("Expecting exception");
        } catch (CommonValidationException  ex){
            assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, ex.getErrorCode());
            List<FieldError> fieldErrors = ex.getValidationErrors().getFieldErrors();
            assertEquals(1, fieldErrors.size());
            assertEquals(WatchlistConstants.WL_ITEM_ACTION_FIELD, fieldErrors.get(0).getField());
        }
    }
    public void testInvalidFieldAndType() {
        try{
            WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
            WatchlistItemSpec itm = spec.getWatchlistItems().get(0);
            WatchlistTerm trm = itm.getTerms()[0];
            trm.setField("foo");
            trm.setType("bar");
            WatchlistValidationAdapter.validateWatchlistSpec(spec);
            fail("Expecting exception");
        } catch (CommonValidationException  ex){
            assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, ex.getErrorCode());
            List<FieldError> fieldErrors = ex.getValidationErrors().getFieldErrors();
            assertEquals(2, fieldErrors.size());
            boolean gotFieldError = false;
            boolean gotTypeError = false;
            for(FieldError err:fieldErrors){
                if(WatchlistConstants.WL_ITEM_FIELD_FIELD.equals(err.getField())){
                    gotFieldError = true;
                }
                if(WatchlistConstants.WL_ITEM_TYPE_FIELD.equals(err.getField())){
                    gotTypeError = true;
                }
            }
            assertTrue(gotFieldError&gotTypeError);
        }
    }
}
