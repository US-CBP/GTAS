/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import gov.gtas.common.WebAppConfig;
import gov.gtas.controller.config.TestMvcRestServiceWebConfig;
import gov.gtas.enumtype.Status;
import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.svc.RuleManagementService;
import gov.gtas.svc.WatchlistService;
import gov.gtas.util.SampleDataGenerator;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * End to end Integration tests for Watch list.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMvcRestServiceWebConfig.class,
        WebAppConfig.class })
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
public class WatchlistManagementControllerIT {
    private static final String TEST_USER = "adelorie";
    private static final String WL_NAME = "TestWL123";
    private MockMvc mockMvc;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private RuleManagementService ruleManagementService;

    private WatchlistManagementController watchlistController;

    @Before
    public void setUp() {
        watchlistController = new WatchlistManagementController();
        ReflectionTestUtils.setField(watchlistController,
                "ruleManagementService", ruleManagementService);
        ReflectionTestUtils.setField(watchlistController, "watchlistService",
                watchlistService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(watchlistController)
                .defaultRequest(
                        get("/").contextPath("/gtas").accept(
                                MediaType.APPLICATION_JSON)).build();
    }

    @Test
    @Transactional
    public void testGetWl() throws Exception {
        watchlistService.createUpdateDeleteWatchlistItems("adelorie",
                SampleDataGenerator.newWlWith2Items(WL_NAME));

        mockMvc.perform(get("/gtas/wl/PASSENGER/" + WL_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$result.name", is(WL_NAME)))
                .andExpect(jsonPath("$result.entity", is("Passenger")))
                .andExpect(jsonPath("$result.watchlistItems", hasSize(2)))
                .andExpect(
                        jsonPath("$result.watchlistItems[0].action",
                                is((String) null)))
                .andExpect(
                        jsonPath("$result.watchlistItems[1].action",
                                is((String) null)));
    }

    @Test
    @Transactional
    public void testGetEmptyWl() throws Exception {
        /*
         * Get on any watch list name will return an empty watch list if it does
         * not exist in the DB.
         */
        mockMvc.perform(get("/gtas/wl/PASSENGER/foobar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$result.name", is("foobar")))
                .andExpect(jsonPath("$result.entity", is("PASSENGER")))
                .andExpect(jsonPath("$result.watchlistItems", hasSize(0)));
    }

    @Test
    @Transactional
    @WithUserDetails(TEST_USER)
    public void testCreateWl() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(
                post("/gtas/wl/adelorie").contentType(
                        MediaType.APPLICATION_JSON).content(
                        mapper.writeValueAsString(SampleDataGenerator
                                .newWlWith2Items(WL_NAME))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.status",
                                is(Status.SUCCESS.toString())))
                .andExpect(jsonPath("$.responseDetails", hasSize(3)))
                .andExpect(
                        jsonPath("$.responseDetails[0].attributeName", is("id")))
                .andExpect(
                        jsonPath("$.responseDetails[1].attributeName",
                                is("title")));

    }

    @Test
    @Transactional
    @WithUserDetails(TEST_USER)
    public void testUpdateDeleteWl() throws Exception {
        watchlistService.createUpdateDeleteWatchlistItems(TEST_USER,
                SampleDataGenerator.newWlWith2Items(WL_NAME));
        WatchlistSpec wlSpec = watchlistService.fetchWatchlist(WL_NAME);
        assertNotNull(wlSpec);
        // the watch list has two items
        assertEquals(2, wlSpec.getWatchlistItems().size());

        wlSpec.getWatchlistItems().get(0)
                .setAction(WatchlistEditEnum.U.getOperationName());// update one
                                                                    // item
        wlSpec.getWatchlistItems().get(1)
                .setAction(WatchlistEditEnum.D.getOperationName());// delete one
                                                                    // item
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(
                post("/gtas/wl/passenger").contentType(
                        MediaType.APPLICATION_JSON).content(
                        mapper.writeValueAsString(wlSpec)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.status",
                                is(Status.SUCCESS.toString())))
                .andExpect(
                        jsonPath("$.responseDetails[0].attributeName", is("id")))
                .andExpect(
                        jsonPath("$.responseDetails[1].attributeName",
                                is("title")));

        wlSpec = watchlistService.fetchWatchlist(WL_NAME);
        // the watch list now has one item
        assertEquals(1, wlSpec.getWatchlistItems().size());
    }

    @Test
    @Transactional
    @WithUserDetails(TEST_USER)
    public void testDeleteAllWl() throws Exception {
        watchlistService.createUpdateDeleteWatchlistItems(TEST_USER,
                SampleDataGenerator.newWlWith2Items(WL_NAME));
        WatchlistSpec wlSpec = watchlistService.fetchWatchlist(WL_NAME);
        assertNotNull(wlSpec);
        // the watch list has two items
        assertEquals(2, wlSpec.getWatchlistItems().size());

        mockMvc.perform(delete("/gtas/wl/passenger/"+WL_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.status",
                                is(Status.SUCCESS.toString())))
                .andExpect(
                        jsonPath("$.responseDetails[0].attributeName", is("id")))
                .andExpect(
                        jsonPath("$.responseDetails[1].attributeName",
                                is("title")));

        wlSpec = watchlistService.fetchWatchlist(WL_NAME);
        // the watch list has been deleted
        assertNull(wlSpec);
    }

}
