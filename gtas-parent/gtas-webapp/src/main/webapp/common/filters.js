/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
(function () {
    'use strict';
    app
        .filter('hitsConditionDisplayFilter', function () {
            return function (ruleDetail) {
                return ruleDetail.ruleConditions.replace(/[$]/g, '');
            };
        })
        .filter('roleDescriptionFilter', function () {
            return function (roles) {
                return roles != null ?
                        roles.map(function (role) {
                            return role.roleDescription;
                        }).join(', ')
                        :'';
            };
        })
        .filter('ruleHitButton', function () {
            return function (ruleHit) {
                return ruleHit ? "" : "disabled";
            };
        })
        .filter('ruleHitIcon', function () {
            return function (ruleHit) {
                return ruleHit ? "glyphicon glyphicon-flag" : "";
            };
        })
        .filter('userStatusFilter', function () {
            return function (value) {
                return !!value ? "Yes":"No";
            };
        })
        /* NOT USED */
        .filter('capitalize', function () {
            return function (input) {
                return (!!input) ? input.replace(/([^\W_]+[^\s-]*) */g, function (txt) {
                    return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
                }) : '';
            };
        })
        /* NOT USED */
        .filter('orderObjectBy', function () {
            return function (items, field, reverse) {
                var filtered = [];
                items.forEach(function (item) {
                    filtered.push(item);
                });
                filtered.sort(function (a, b) {
                    return (a[field] > b[field] ? 1 : -1);
                });
                if (reverse) {
                    filtered.reverse();
                }
                return filtered;
            };
        })
        .filter('watchListImageFilter', function () {
            return function (hits) {
                if (hits === 1) {
                    return 'icon-user-check glyphiconWLPax col-sm-4';
                } // glyphiconWLPax
                if (hits === 2) {
                    return 'icon-book glyphiconWLDocs col-sm-4';
                } // glyphiconWLDocs
                if (hits === 3) {
                    return 'icon-user-check glyphiconWLPaxDocs col-sm-4';
                } // glyphiconWLPaxDocs  glyphicon-user
                return '';
            };
        })
        .filter('watchListImageInsertFilter', function () {
            return function (hits) {
                if (hits === 1) {
                    return '';
                } // glyphiconWLPax
                if (hits === 2) {
                    return '';
                } // glyphiconWLDocs
                if (hits === 3) {
                    return 'icon-book';
                } // glyphiconWLPaxDocs
                return '';
            };

        })
        .filter('mapDocType', function() {
            var DOCUMENT_TYPES = {
                'P': 'PASSENGER',
                'V': 'VISA'
            };
            return function (input) {
                return !input ? '' : DOCUMENT_TYPES[input];
            };
        })
        .filter('watchListDocHit', function () {
            return function (hit) {
                return hit ? 'glyphicon glyphicon-file' : '';
            };
        })
        .filter('watchListHit', function () {
            return function (hit) {
                return hit ? 'glyphicon glyphicon-user' : '';
            };
        });
}());
