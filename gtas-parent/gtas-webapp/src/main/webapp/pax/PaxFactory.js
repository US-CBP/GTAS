/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.factory("sharedPaxData", function () {
    'use strict';
    var items = [], itemsService = {};

    itemsService.add = function (item, index) {
        items[index] = item;
    };

    itemsService.list = function (index) {
        return items[index];
    };

    itemsService.getAll = function () {
        return items;
    };

    return itemsService;
});
