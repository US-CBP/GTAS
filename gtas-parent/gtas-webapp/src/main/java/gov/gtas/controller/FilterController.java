/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.services.Filter.FilterData;
import gov.gtas.services.Filter.FilterService;
import gov.gtas.validator.FilterDataValidator;

@RestController
public class FilterController {

    private static final Logger logger = LoggerFactory.getLogger(FilterController.class);

    @Autowired
    private FilterService filterService;

    @Autowired
    private FilterDataValidator filterDataValidator;

    @InitBinder("filterData")
    protected void intializeUserDataValidator(WebDataBinder binder) {
        binder.addValidators(filterDataValidator);
    }

    @RequestMapping( method = RequestMethod.GET,value = "/filter/{userId}"
            ,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody  FilterData getUserFilter(@PathVariable(value = "id") String userId) {
        return filterService.findById(userId);
    }
    
    @RequestMapping( method = RequestMethod.POST,value = "/filter/{userId}"
            ,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody  FilterData createUserFilter(@RequestBody FilterData filterData) {
        return filterService.create(filterData);
    }
    
    @RequestMapping( method = RequestMethod.PUT,value = "/filter/{userId}"
            ,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody  FilterData updateUserFilter(@RequestBody FilterData filterData) {
        return filterService.update(filterData);
    }

}
