/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.controller('jqbController', function ($scope, $timeout) {
  'use strict';
  /* Note any controller pulling in must have a view template with one element with an id of builder */
  /* this only handles pulling jQueryBuilder into angular */
  var setSelectizeValue = function ($selectize, value) {
          $selectize[0].selectize.setValue(value);
          $timeout(function () {
              if ($selectize[0].selectize.getValue().length === 0) setSelectizeValue($selectize, value);
          },50);
      },
      selectizeValueSetter = function (rule, value) {
          rule.$el.find(".rule-value-container select").val(value);
          var $selectize = rule.$el.find(".rule-value-container .selectized");

          if ($selectize.length) setSelectizeValue($selectize, value);
      },
      getOptionsFromJSONArray = function (that, property) {
        $.getJSON('./data/' + property + '.json', function (data) {
              //localStorage[property] = JSON.stringify(data);
              try {
                  data.forEach(function (item) {
                      that.addOption(item);
                  });
              } catch (exception) {
                  throw exception;
              }
          });
          //} else {
          //    try {
          //    JSON.parse(localStorage[property]).forEach(function (item) {
          //    that.addOption(item);
          //    });
          //    } catch (exception) {
          //       throw exception;
          //    }
          //}
      },
      defaults = {
          allow_empty: true,
          service: "DROOLS",
          plugins: {
              'bt-tooltip-errors': {delay: 100},
              'sortable': null,
              'filter-description': {mode: 'bootbox'},
              'bt-selectpicker': null,
              'unique-filter': null,
              'bt-checkbox': {color: 'primary'}
          },
          filters: []
      };

  $scope.buildAfterEntitiesLoaded = function (options) {
      var property = 'entities',
          $builder = $('#builder'),
          supplement = {
              selectize: function (obj) {
                  obj.plugin_config = {
                      "valueField": "id",
                      "labelField": "name",
                      "searchField": "name",
                      "sortField": "name",
                      "create": false,
                      "plugins": ["remove_button"],
                      "onInitialize": function () {
                          getOptionsFromJSONArray(this, obj.dataSource);
                      }
                  };
                  obj.valueSetter = selectizeValueSetter;
              },
              datepicker: function (obj) {
                  obj.validation = { "format": "YYYY-MM-DD" };
                  obj.plugin_config = {
                      "format": "yyyy-mm-dd",
                      "autoClose": true
                  };
              }
          };
      if ($builder.length === 0) {
          alert('#builder not found in the DOM!');
      }
      // init
      $builder
          .on('afterCreateRuleInput.queryBuilder', function (e, rule) {
              if (rule.filter !== undefined && rule.filter.plugin === 'selectize') {
                  rule.$el.find('.rule-value-container').css('min-width', '200px')
                      .find('.selectize-control').removeClass('form-control');
              }
          });

      try {
          $.getJSON('./data/' + property + '.json', function (data) {
              if (options && options.deleteEntity) {
                  data[options.deleteEntity] = null;
                  delete data[options.deleteEntity];
              }
              defaultOptions.entities = data;
              defaultOptions.filters = [];
              Object.keys(defaultOptions.entities).forEach(function (key){
                  defaultOptions.entities[key].columns.forEach(function (column){
                      switch (column.plugin) {
                          case 'selectize':
                          case 'datepicker':
                              supplement[column.plugin](column);
                              break;
                          default:
                              break;
                      }
                      defaultOptions.filters.push(column);
                  });
              });
              $builder.queryBuilder(defaultOptions);

              $scope.$builder = $builder;
          });
          //} else {
          //    defaultOptions.entities = JSON.parse(localStorage[property]);
          //    $builder.queryBuilder(defaultOptions);
          //    $scope.$builder = $builder;
          //}
      } catch (exception) {
          throw exception;
      }
  };
  $scope.buildAfterEntitiesLoaded();
});
