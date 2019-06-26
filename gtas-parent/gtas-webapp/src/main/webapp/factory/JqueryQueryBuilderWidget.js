/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
app.factory('jqueryQueryBuilderWidget', function () {
  'use strict';
  /* Note any controller pulling in must have a view template with one element with an id of builder */
  /* this only handles pulling jQueryBuilder into angular */


  return function ($scope, $timeout, codeService) {
      var setSelectizeValue = function ($selectize, value) {
              if($scope.isLoadedSelectize && !$scope.hasDataSource){
                  $scope.isLoadedSelectize = false;
                  // to get the rules with IN cluses to run, fixed the non-sourced multiple inputs to be separate array elements.No need to split.
                  //value = value[0].split(',');
                  $.each(value, function(index,val){
                      $selectize[0].selectize.addOption({'id':val,'name':val});
                  });
              }
              
              $selectize[0].selectize.setValue(value);
              $timeout(function () {
                  if ($selectize[0].selectize.getValue().length === 0) {setSelectizeValue($selectize, value);}else{$scope.isLoadedSelectize = false;}
       
              },50);
          },
          selectizeValueSetter = function (rule, value) {
              rule.$el.find(".rule-value-container select").val(value);
              var $selectize = rule.$el.find(".rule-value-container .selectized");

              if ($selectize.length) setSelectizeValue($selectize, value);
          },
          getOptions = function(that, prop) {
            if (!prop) {
              $scope.hasDataSource = false;
              $scope.isLoadedSelectize = true;
              return;
            }
            $scope.hasDataSource = true;

            if(prop === 'airports') {
              codeService.getAirportsWithCode().then(result => {
                  result.forEach(function (item) {that.addOption(item) });
                  $scope.isLoadedSelectize = true;
                })
            }
            else if(prop === 'countries') {
              codeService.getCountryTooltips().then(result => {
                  result.forEach(function (item) {that.addOption(item) });
                  $scope.isLoadedSelectize = true;
                })
            }
            else {
              getOptionsFromJSONArray(that, prop); 
            }

          },
          getOptionsFromJSONArray = function (that, property) {
              if(typeof property !== 'undefined' && property !== ''){
                  $.getJSON('./data/' + property + '.json', function (data) {

                    try {
                          data.forEach(function (item) {
                              that.addOption(item);
                          });
                      } catch (exception) {
                          throw exception;
                      }
                      $scope.isLoadedSelectize = true;
                  });
              }else{
                  $scope.hasDataSource = false;
                  $scope.isLoadedSelectize = true;
              }
          };
      $scope.options = {
          allow_empty: true,
          service: "DROOLS",
          plugins: {
              'bt-tooltip-errors': {delay: 100},
              'sortable': {icon:'fa fa-arrows'},
              'filter-description': {mode: 'bootbox'},
              'bt-selectpicker': null,
              'unique-filter': null,
              'bt-checkbox': {color: 'primary'}
          },
          filters: []
      };
      
      $scope.getApisOnlyFlagAndVersion = function () {

          var apisOnlyFlagAndVersion = "FALSE";

          $.ajax({
              async: false,
              url: '/gtas/query/apisOnlyFlag',
              success: function (data, status, jqXHR) {
                 apisOnlyFlagAndVersion = data;
               }
           });

         return apisOnlyFlagAndVersion;
      };

      $scope.buildAfterEntitiesLoaded = function (options) {
          
          var apisOnlyFlagAndVersion = $scope.getApisOnlyFlagAndVersion();
          var apisVersion = (apisOnlyFlagAndVersion.startsWith("TRUE")) ? apisOnlyFlagAndVersion.split(";")[1] : null;
          // Could later add the apisVersion to end of property var and reference multiple json files for different versions of APIS.
          var property = (apisOnlyFlagAndVersion == "FALSE") ? 'entities' : 'entitiesApisOnly',
              $builder = $('#builder'),
              supplement = {
                  selectize: function (obj) {
                      var maxItems = 1;
                      var isCreate = true;
                      if(obj.operatorVal === "IN" || obj.operatorVal === "NOT_IN"){
                          maxItems = 10;
                      }
                      if(obj.dataSource){
                          isCreate = false;
                      }
                      obj.plugin_config = {
                          "valueField": "id",
                          "labelField": "name",
                          "searchField": "name",
                          "sortField": "name",
                          "create":isCreate,
                          "persist": false,
                          "maxItems":maxItems,
                          "plugins": ["restore_on_backspace", "remove_button"],
                          "onInitialize": function () {
                              getOptions(this, obj.dataSource);
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
              
          $builder.on('keyup', function (e) {
              var upper = e.target.value.toUpperCase();
              e.target.value = upper;
          });
          
          $builder
              .on('afterUpdateRuleOperator.queryBuilder', function(e, rule){
              if(!$scope.ruleOpIsTriggered){
                  var op = rule.$el.find('.rule-operator-container').find('select').val();
                  rule.filter.operatorVal = op;
                  $scope.tempRuleFilter = rule.filter;
                  rule.$el.find('.rule-filter-container').find('select').trigger('change'); //allows rebuild of input field plugin
                  $scope.resetOperatorValue(op, rule);
                  $('.bs-container').remove();
                  $scope.tempRuleFilter = false;
                  $scope.ruleOpIsTriggered = false;
              }
          });
          
          $builder
              .on('afterCreateRuleOperators.queryBuilder', function(e, rule, op){ 
                  //prevent multiple triggers and insure is only active after our custom operator event listener above
                  if($scope.tempRuleFilter && !$scope.ruleOpIsTriggered){
                      $scope.ruleOpIsTriggered = true;
                      if($scope.tempRuleFilter){
                          rule.filter = $.extend({}, $scope.tempRuleFilter);
                      }
                      if(rule.filter.needRevert){
                          var originalSettings = $scope.options.filters.filter(function(obj){ return obj.id === rule.filter.id})[0];
                          var tmpOp = rule.filter.operatorVal;
                          rule.filter = $.extend({},originalSettings);
                          rule.filter.operatorVal = tmpOp;
                      }
                      if(rule.filter && rule.filter.plugin !== 'datepicker' &&
                              (rule.filter.operatorVal === "IN" || rule.filter.operatorVal === "NOT_IN")){
                          rule.filter.plugin = 'selectize';
                          supplement[rule.filter.plugin](rule.filter);
                          rule.filter.needRevert = true;
                      }
                  }
              });

          try {
              $.getJSON('./data/' + property + '.json', function (data) {

                  if (options && options.deleteEntity) {
                      data[options.deleteEntity] = null;
                      delete data[options.deleteEntity];
                  }
                  $scope.options.entities = data;
                  $scope.options.filters = [];
                  Object.keys($scope.options.entities).forEach(function (key){
                      $scope.options.entities[key].columns.forEach(function (column){
                          switch (column.plugin) {
                              case 'selectize':
                              case 'datepicker':
                                  supplement[column.plugin](column);
                                  break;
                              default:
                                  break;
                          }
                          $scope.options.filters.push(column);
                      });
                  });
                  $builder.queryBuilder($scope.options);

                  $scope.$builder = $builder;
              });
          } catch (exception) {
              throw exception;
          }
      };
      $scope.resetQueryBuilder = function () {
          $scope.$builder.queryBuilder('reset');
      };
      
      $scope.resetOperatorValue = function(op, rule){
          var operatorVal = '';
          $.each($scope.$builder.queryBuilder.defaults().operators, function(index,value){
              if(value.type === op){
                  operatorVal = value;
                  return false;
              }
          });
          rule.operator = operatorVal;
      };
  };
});
