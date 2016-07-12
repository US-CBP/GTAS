/*!
 * jQuery QueryBuilder 2.2.0
 * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
 * Licensed under MIT (http://opensource.org/licenses/MIT)
 */

// Languages: en
// Plugins: bt-checkbox, bt-selectpicker, bt-tooltip-errors, filter-description, invert, mongodb-support, sortable, sql-support, unique-filter
(function(root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(['jquery', 'jQuery.extendext'], factory);
    }
    else {
        factory(root.jQuery);
    }
}(this, function($) {
    "use strict";

// CLASS DEFINITION
// ===============================
    var QueryBuilder = function($el, options) {
        this.init($el, options);
    };


// EVENTS SYSTEM
// ===============================
    var aps = Array.prototype.slice;

    $.extend(QueryBuilder.prototype, {
        change: function(type, value) {
            var event = new $.Event(type + '.queryBuilder.filter', {
                builder: this,
                value: value
            });

            this.$el.triggerHandler(event, aps.call(arguments, 2));

            return event.value;
        },

        trigger: function(type) {
            var event = new $.Event(type + '.queryBuilder', {
                builder: this
            });

            this.$el.triggerHandler(event, aps.call(arguments, 1));

            return event;
        },

        on: function(type, cb) {
            this.$el.on(type + '.queryBuilder', cb);
            return this;
        },

        off: function(type, cb) {
            this.$el.off(type + '.queryBuilder', cb);
            return this;
        },

        once: function(type, cb) {
            this.$el.one(type + '.queryBuilder', cb);
            return this;
        }
    });


// PLUGINS SYSTEM
// ===============================
    QueryBuilder.plugins = {};

    /**
     * Get or extend the default configuration
     * @param options {object,optional} new configuration, leave undefined to get the default config
     * @return {undefined|object} nothing or configuration object (copy)
     */
    QueryBuilder.defaults = function(options) {
        if (typeof options == 'object') {
            $.extendext(true, 'replace', QueryBuilder.DEFAULTS, options);
        }
        else if (typeof options == 'string') {
            if (typeof QueryBuilder.DEFAULTS[options] == 'object') {
                return $.extend(true, {}, QueryBuilder.DEFAULTS[options]);
            }
            else {
                return QueryBuilder.DEFAULTS[options];
            }
        }
        else {
            return $.extend(true, {}, QueryBuilder.DEFAULTS);
        }
    };

    /**
     * Define a new plugin
     * @param {string}
     * @param {function}
     * @param {object,optional} default configuration
     */
    QueryBuilder.define = function(name, fct, def) {
        QueryBuilder.plugins[name] = {
            fct: fct,
            def: def || {}
        };
    };

    /**
     * Add new methods
     * @param {object}
     */
    QueryBuilder.extend = function(methods) {
        $.extend(QueryBuilder.prototype, methods);
    };

    /**
     * Init plugins for an instance
     */
    QueryBuilder.prototype.initPlugins = function() {
        if (!this.plugins) {
            return;
        }

        if ($.isArray(this.plugins)) {
            var tmp = {};
            this.plugins.forEach(function(plugin) {
                tmp[plugin] = null;
            });
            this.plugins = tmp;
        }

        Object.keys(this.plugins).forEach(function(plugin) {
            if (plugin in QueryBuilder.plugins) {
                this.plugins[plugin] = $.extend(true, {},
                    QueryBuilder.plugins[plugin].def,
                    this.plugins[plugin] || {}
                );

                QueryBuilder.plugins[plugin].fct.call(this, this.plugins[plugin]);
            }
            else {
                error('Unable to find plugin "{0}"', plugin);
            }
        }, this);
    };

    /**
     * Allowed types and their internal representation
     */
    QueryBuilder.types = {
        'string': 'string',
        'integer': 'number',
        'double': 'number',
        'date': 'datetime',
        'time': 'datetime',
        'datetime': 'datetime',
        'boolean': 'boolean'
    };

    /**
     * Allowed inputs
     */
    QueryBuilder.inputs = [
        'text',
        'textarea',
        'radio',
        'checkbox',
        'select'
    ];

    /**
     * Runtime modifiable options with `setOptions` method
     */
    QueryBuilder.modifiable_options = [
        'display_errors',
        'allow_groups',
        'allow_empty'
    ];

    /**
     * Localized strings (populated by `i18n` files)
     */
    QueryBuilder.regional = {};

    /**
     * Default configuration
     */
    QueryBuilder.DEFAULTS = {
        filters: [],
        plugins: [],

        display_errors: true,
        allow_groups: -1,
        allow_empty: false,
        conditions: ['AND', 'OR'],
        default_condition: 'AND',
        inputs_separator: ' , ',
        select_placeholder: '------',

        default_rule_flags: {
            filter_readonly: false,
            operator_readonly: false,
            value_readonly: false,
            no_delete: false
        },

        template: {
            group: null,
            rule: null
        },

        lang_code: 'en',
        lang: {},

        operators: [
            {type: 'equal',            nb_inputs: 1, multiple: false, apply_to: ['string', 'number', 'datetime', 'boolean']},
            {type: 'not_equal',        nb_inputs: 1, multiple: false, apply_to: ['string', 'number', 'datetime', 'boolean']},
            {type: 'in',               nb_inputs: 1, multiple: true,  apply_to: ['string', 'number', 'datetime']},
            {type: 'not_in',           nb_inputs: 1, multiple: true,  apply_to: ['string', 'number', 'datetime']},
            {type: 'less',             nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime']},
            {type: 'less_or_equal',    nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime']},
            {type: 'greater',          nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime']},
            {type: 'greater_or_equal', nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime']},
            {type: 'between',          nb_inputs: 2, multiple: false, apply_to: ['number', 'datetime']},
            {type: 'not_between',      nb_inputs: 2, multiple: false, apply_to: ['number', 'datetime']},
            {type: 'begins_with',      nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'not_begins_with',  nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'contains',         nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'not_contains',     nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'ends_with',        nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'not_ends_with',    nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'is_empty',         nb_inputs: 0, multiple: false, apply_to: ['string']},
            {type: 'is_not_empty',     nb_inputs: 0, multiple: false, apply_to: ['string']},
            {type: 'is_null',          nb_inputs: 0, multiple: false, apply_to: ['string', 'number', 'datetime', 'boolean']},
            {type: 'is_not_null',      nb_inputs: 0, multiple: false, apply_to: ['string', 'number', 'datetime', 'boolean']},
            /* extending operators for DROOLS */
            {type: 'EQUAL',            nb_inputs: 1, multiple: false, apply_to: ['string', 'number', 'datetime', 'date', 'boolean']},
            {type: 'NOT_EQUAL',        nb_inputs: 1, multiple: false, apply_to: ['string', 'number', 'datetime', 'date', 'boolean']},
            {type: 'IN',               nb_inputs: 1, multiple: true,  apply_to: ['string', 'number', 'datetime', 'date']},
            {type: 'NOT_IN',           nb_inputs: 1, multiple: true,  apply_to: ['string', 'number', 'datetime', 'date']},
            {type: 'LESS',             nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime', 'date']},
            {type: 'LESS_OR_EQUAL',    nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime', 'date']},
            {type: 'GREATER',          nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime', 'date']},
            {type: 'GREATER_OR_EQUAL', nb_inputs: 1, multiple: false, apply_to: ['number', 'datetime', 'date']},
            {type: 'BETWEEN',          nb_inputs: 2, multiple: false, apply_to: ['number', 'datetime', 'date']},
            {type: 'NOT_BETWEEN',      nb_inputs: 2, multiple: false, apply_to: ['number', 'datetime', 'date']},
            {type: 'BEGINS_WITH',      nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'NOT_BEGINS_WITH',  nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'CONTAINS',         nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'NOT_CONTAINS',     nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'ENDS_WITH',        nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'NOT_ENDS_WITH',    nb_inputs: 1, multiple: false, apply_to: ['string']},
            {type: 'IS_EMPTY',         nb_inputs: 0, multiple: false, apply_to: ['string']},
            {type: 'IS_NOT_EMPTY',     nb_inputs: 0, multiple: false, apply_to: ['string']},
            {type: 'IS_NULL',          nb_inputs: 0, multiple: false, apply_to: ['string', 'number', 'datetime', 'date', 'boolean']},
            {type: 'IS_NOT_NULL',      nb_inputs: 0, multiple: false, apply_to: ['string', 'number', 'datetime', 'date', 'boolean']}

            /* end extending operators */
        ],

        icons: {
            add_group: 'glyphicon glyphicon-plus-sign',
            add_rule: 'glyphicon glyphicon-plus',
            remove_group: 'glyphicon glyphicon-remove',
            remove_rule: 'glyphicon glyphicon-remove',
            error: 'glyphicon glyphicon-warning-sign'
        }
    };

    /**
     * Init the builder
     */
    QueryBuilder.prototype.init = function($el, options) {
        $el[0].queryBuilder = this;
        this.$el = $el;

        // PROPERTIES
        this.settings = $.extendext(true, 'replace', {}, QueryBuilder.DEFAULTS, options);
        this.model = new Model();
        this.queryName = null;
        this.savedQueryNames = [];
        this.status = {
            group_id: 0,
            rule_id: 0,
            generated_id: false,
            has_optgroup: false,
            id: null,
            updating_value: false
        };

        // "allow_groups" can be boolean or int
        if (this.settings.allow_groups === false) {
            this.settings.allow_groups = 0;
        }
        else if (this.settings.allow_groups === true) {
            this.settings.allow_groups = -1;
        }

        // SETTINGS SHORTCUTS
        this.entities = this.settings.entities;
        this.filters = this.settings.filters;
        this.icons = this.settings.icons;
        this.operators = this.settings.operators;
        this.template = this.settings.template;
        this.plugins = this.settings.plugins;

        // translations : english << 'lang_code' << custom
        if (QueryBuilder.regional['en'] === undefined) {
            error('"i18n/en.js" not loaded.');
        }
        this.lang = $.extendext(true, 'replace', {}, QueryBuilder.regional['en'], QueryBuilder.regional[this.settings.lang_code], this.settings.lang);

        if (this.template.group === null) {
            this.template.group = this.getGroupTemplate;
        }
        if (this.template.rule === null) {
            this.template.rule = this.getRuleTemplate;
        }

        // ensure we have a container id
        if (!this.$el.attr('id')) {
            this.$el.attr('id', 'qb_'+Math.floor(Math.random()*99999));
            this.status.generated_id = true;
        }
        this.status.id = this.$el.attr('id');

        // INIT
        this.$el.addClass('query-builder form-inline');

        this.checkFilters();
        this.bindEvents();
        this.initPlugins();

        this.trigger('afterInit');

        if (options.rules) {
            this.setRules(options.rules);
            delete this.settings.rules;
        }
        else {
            this.setRoot(true);
        }
    };

    /**
     * Checks the configuration of each filter
     */
    QueryBuilder.prototype.checkFilters = function() {
        var definedFilters = [],
            that = this;

        if (!this.filters || this.filters.length === 0) {
            error('Missing filters list');
        }

        this.filters.forEach(function(filter, i) {
            if (!filter.id) {
                error('Missing filter {0} id', i);
            }
            if (definedFilters.indexOf(filter.id) != -1) {
                error('Filter "{0}" already defined', filter.id);
            }
            definedFilters.push(filter.id);

            if (!filter.type) {
                filter.type = 'string';
            }
            else if (!QueryBuilder.types[filter.type]) {
                error('Invalid type "{0}"', filter.type);
            }

            if (!filter.input) {
                filter.input = 'text';
            }
            else if (typeof filter.input != 'function' && QueryBuilder.inputs.indexOf(filter.input) == -1) {
                error('Invalid input "{0}"', filter.input);
            }

            if (!filter.field) {
                filter.field = filter.id;
            }
            if (!filter.label) {
                filter.label = filter.field;
            }

            if (!filter.optgroup) {
                filter.optgroup = null;
            }
            else {
                that.status.has_optgroup = true;
            }

            switch (filter.input) {
                case 'radio': case 'checkbox':
                if (!filter.values || filter.values.length < 1) {
                    error('Missing filter "{0}" values', filter.id);
                }
                break;
            }
        });

        // group filters with same optgroup, preserving declaration order when possible
        if (this.status.has_optgroup) {
            var optgroups = [],
                filters = [];

            this.filters.forEach(function(filter, i) {
                var idx;

                if (filter.optgroup) {
                    idx = optgroups.lastIndexOf(filter.optgroup);

                    if (idx == -1) {
                        idx = optgroups.length;
                    }
                    else {
                        idx++;
                    }
                }
                else {
                    idx = optgroups.length;
                }

                optgroups.splice(idx, 0, filter.optgroup);
                filters.splice(idx, 0, filter);
            });

            this.filters = filters;
        }
    };

    /**
     * Add all events listeners
     */
    QueryBuilder.prototype.bindEvents = function() {
        var that = this;

        // group condition change
        this.$el.on('change.queryBuilder', '.group-conditions :checked', function() {
            var $group = $(this).closest('.rules-group-container');
            Model($group).condition = this.value;
        });

        // rule entity change
        this.$el.on('change.queryBuilder', '.rule-entity-container select', function() {
            var $this = $(this);
            var $rule = $this.closest('.rule-container');
            Model($rule).entity = that.getEntityByName($this.val());
        });

        // rule filter change
        this.$el.on('change.queryBuilder', '.rule-filter-container select', function() {
            var $this = $(this);
            var $rule = $this.closest('.rule-container');

            Model($rule).filter = that.getFilterById($this.val());
        });

        // rule operator change
        this.$el.on('change.queryBuilder', '.rule-operator-container select', function() {
            var $this = $(this);
            var $rule = $this.closest('.rule-container');
            Model($rule).operator = that.getOperatorByType($this.val());
        });

        // add rule button
        this.$el.on('click.queryBuilder', '[data-add=rule]', function() {
            var $group = $(this).closest('.rules-group-container');
            that.addRule(Model($group));
        });

        // delete rule button
        this.$el.on('click.queryBuilder', '[data-delete=rule]', function() {
            var $rule = $(this).closest('.rule-container');
            that.deleteRule(Model($rule));
        });

        if (this.settings.allow_groups !== 0) {
            // add group button
            this.$el.on('click.queryBuilder', '[data-add=group]', function() {
                var $group = $(this).closest('.rules-group-container');
                that.addGroup(Model($group));
            });

            // delete group button
            this.$el.on('click.queryBuilder', '[data-delete=group]', function() {
                var $group = $(this).closest('.rules-group-container');
                that.deleteGroup(Model($group));
            });
        }

        // model events
        this.model.on({
            'drop': function(e, node) {
                node.$el.remove();
            },
            'add': function(e, node, index) {
                node.$el.detach();

                if (index === 0) {
                    node.$el.prependTo(node.parent.$el.find('>.rules-group-body>.rules-list'));
                }
                else {
                    node.$el.insertAfter(node.parent.rules[index-1].$el);
                }
            },
            'update': function(e, node, field, value, oldValue) {
                switch (field) {
                    case 'error':
                        that.displayError(node);
                        return;

                    case 'entity':
                        that.updateRuleEntity(node);
                        return;

                    case 'condition':
                        that.updateGroupCondition(node);
                        return;

                    case 'filter':
                        that.updateRuleFilter(node);
                        return;

                    case 'operator':
                        that.updateRuleOperator(node, oldValue);
                        return;

                    case 'flags':
                        that.applyRuleFlags(node);
                        return;

                    case 'value':
                        that.updateRuleValue(node);
                        return;
                }
            }
        });
    };

    /**
     * Create the root group
     * @param addRule {bool,optional} add a default empty rule
     * @param data {mixed,optional} group custom data
     * @return group {Root}
     */
    QueryBuilder.prototype.setRoot = function(addRule, data) {
        addRule = (addRule === undefined || addRule === true);

        var group_id = this.nextGroupId(),
            $group = $(this.template.group.call(this, group_id, 1));

        this.$el.append($group);
        this.model.root = new Group(null, $group);
        this.model.root.model = this.model;
        this.model.root.condition = this.settings.default_condition;

        if (data !== undefined) {
            this.model.root.data = data;
        }

        if (addRule) {
            this.addRule(this.model.root);
        }

        return this.model.root;
    };

    /**
     * Add a new group
     * @param parent {Group}
     * @param addRule {bool,optional} add a default empty rule
     * @param data {mixed,optional} group custom data
     * @return group {Group}
     */
    QueryBuilder.prototype.addGroup = function(parent, addRule, data) {
        addRule = (addRule === undefined || addRule === true);

        var level = parent.level + 1;

        var e = this.trigger('beforeAddGroup', parent, addRule, level);
        if (e.isDefaultPrevented()) {
            return null;
        }

        var group_id = this.nextGroupId(),
            $group = $(this.template.group.call(this, group_id, level)),
            model = parent.addGroup($group);

        if (data !== undefined) {
            model.data = data;
        }

        this.trigger('afterAddGroup', model);

        model.condition = this.settings.default_condition;

        if (addRule) {
            this.addRule(model);
        }

        return model;
    };

    /**
     * Tries to delete a group. The group is not deleted if at least one rule is no_delete.
     * @param group {Group}
     * @return {boolean} true if the group has been deleted
     */
    QueryBuilder.prototype.deleteGroup = function(group) {
        if (group.isRoot()) {
            return false;
        }

        var e = this.trigger('beforeDeleteGroup', group);
        if (e.isDefaultPrevented()) {
            return false;
        }

        var del = true;

        group.each('reverse', function(rule) {
            del&= this.deleteRule(rule);
        }, function(group) {
            del&= this.deleteGroup(group);
        }, this);

        if (del) {
            group.drop();
            this.trigger('afterDeleteGroup');
        }

        return del;
    };

    /**
     * Changes the condition of a group
     * @param group {Group}
     */
    QueryBuilder.prototype.updateGroupCondition = function(group) {
        group.$el.find('>.rules-group-header [name$=_cond]').each(function() {
            var $this = $(this);
            $this.prop('checked', $this.val() === group.condition);
            $this.parent().toggleClass('active', $this.val() === group.condition);
        });

        this.trigger('afterUpdateGroupCondition', group);
    };

    /**
     * Add a new rule
     * @param parent {Group}
     * @param data {mixed,optional} rule custom data
     * @return rule {Rule}
     */
    QueryBuilder.prototype.addRule = function(parent, data) {
        var e = this.trigger('beforeAddRule', parent);
        if (e.isDefaultPrevented()) {
            return null;
        }

        var rule_id = this.nextRuleId(),
            $rule = $(this.template.rule.call(this, rule_id)),
            model = parent.addRule($rule);

        if (data !== undefined) {
            model.data = data;
        }

        this.trigger('afterAddRule', model);

        this.createRuleEntities(model);

        return model;
    };

    /**
     * Delete a rule.
     * @param rule {Rule}
     * @return {boolean} true if the rule has been deleted
     */
    QueryBuilder.prototype.deleteRule = function(rule) {
        if (rule.flags.no_delete) {
            return false;
        }

        var e = this.trigger('beforeDeleteRule', rule);
        if (e.isDefaultPrevented()) {
            return false;
        }

        rule.drop();

        this.trigger('afterDeleteRule');

        return true;
    };

    /**
     * Create the entities <select> for a rule
     * @param rule {Rule}
     */
    QueryBuilder.prototype.createRuleEntities = function(rule) {
        var $entitiesSelect = $(this.getRuleEntitySelect(rule));

        rule.$el.find('.rule-entity-container').append($entitiesSelect);
        this.trigger('afterCreateEntities', rule);
    };

    /**
     * Create the filters <select> for a rule
     * @param rule {Rule}
     */
    QueryBuilder.prototype.createRuleFilters = function(rule) {
        this.filters = this.entities[rule.entity].columns;
        var filters = this.change('getRuleFilters', this.filters, rule);

        var $filterSelect = $(this.getRuleFilterSelect(rule, filters));

        rule.$el.find('.rule-filter-container').empty().append($filterSelect);

        this.trigger('afterCreateRuleFilters', rule);
    };

    /**
     * Create the operators <select> for a rule and init the rule operator
     * @param rule {Rule}
     */
    QueryBuilder.prototype.createRuleOperators = function(rule) {
        var $operatorContainer = rule.$el.find('.rule-operator-container').empty();

        if (!rule.filter) {
            return;
        }

        var operators = this.getOperators(rule.filter),
            $operatorSelect = $(this.getRuleOperatorSelect(rule, operators));

        $operatorContainer.html($operatorSelect);

        // set the operator without triggering update event
        rule.__.operator = operators[0];

        this.trigger('afterCreateRuleOperators', rule, operators);
    };

    /**
     * Create the main input for a rule
     * @param rule {Rule}
     */
    QueryBuilder.prototype.createRuleInput = function(rule) {
        var $valueContainer = rule.$el.find('.rule-value-container').empty();

        rule.__.value = undefined;

        if (!rule.filter || !rule.operator || rule.operator.nb_inputs === 0) {
            return;
        }

        var that = this,
            $inputs = $(),
            filter = rule.filter,
            method, args;

        for (var i=0; i<rule.operator.nb_inputs; i++) {
            var $ruleInput = $(this.getRuleInput(rule, i));
            if (i > 0) $valueContainer.append(this.settings.inputs_separator);
            $valueContainer.append($ruleInput);
            $inputs = $inputs.add($ruleInput);
        }

        $valueContainer.show();

        $inputs.on('change', function() {
            that.status.updating_value = true;
            rule.value = that.getRuleValue(rule);
            that.status.updating_value = false;
        });

        if (filter.plugin !== undefined) {
            method = filter.plugin;
            args = filter.plugin_config !== undefined ? filter.plugin_config : {};
            $inputs[method](args);
        }

        this.trigger('afterCreateRuleInput', rule);

        if (filter.default_value !== undefined) {
            rule.value = filter.default_value;
        }
        else {
            that.status.updating_value = true;
            rule.value = that.getRuleValue(rule);
            that.status.updating_value = false;
        }
    };

    /**
     * Perform action when rule's entity is changed
     * @param rule {Rule}
     */
    QueryBuilder.prototype.updateRuleEntity = function(rule) {
        rule.$el.find('.rule-entity-container select').val(rule.entity ? rule.entity : '-1');
        this.createRuleFilters(rule);
        this.trigger('afterUpdateRuleEntity', rule);
    };

    /**
     * Perform action when rule's filter is changed
     * @param rule {Rule}
     */
    QueryBuilder.prototype.updateRuleFilter = function(rule) {
        this.createRuleOperators(rule);
        this.createRuleInput(rule);

        rule.$el.find('.rule-filter-container [name$=_filter]').val(rule.filter ? rule.filter.id : '-1');

        this.trigger('afterUpdateRuleFilter', rule);
    };

    /**
     * Update main <input> visibility when rule operator changes
     * @param rule {Rule}
     * @param previousOperator {object}
     */
    QueryBuilder.prototype.updateRuleOperator = function(rule, previousOperator) {
        var $valueContainer = rule.$el.find('.rule-value-container');

        if (!rule.operator || rule.operator.nb_inputs === 0) {
            $valueContainer.hide();

            rule.__.value = undefined;
        }
        else {
            $valueContainer.show();

            if ($valueContainer.is(':empty') || rule.operator.nb_inputs !== previousOperator.nb_inputs) {
                this.createRuleInput(rule);
            }
        }

        if (rule.operator) {
            rule.$el.find('.rule-operator-container [name$=_operator]').val(rule.operator.type);
        }

        this.trigger('afterUpdateRuleOperator', rule);
    };

    /**
     * Perform action when rule's value is changed
     * @param rule {Rule}
     */
    QueryBuilder.prototype.updateRuleValue = function(rule) {
        if (!this.status.updating_value) {
            this.setRuleValue(rule, rule.value);
        }

        this.trigger('afterUpdateRuleValue', rule);
    };

    /**
     * Change rules properties depending on flags.
     * @param rule {Rule}
     * @param readonly {boolean}
     */
    QueryBuilder.prototype.applyRuleFlags = function(rule, readonly) {
        var flags = rule.flags;

        if (flags.filter_readonly) {
            rule.$el.find('[name$=_filter]').prop('disabled', true);
        }
        if (flags.operator_readonly) {
            rule.$el.find('[name$=_operator]').prop('disabled', true);
        }
        if (flags.value_readonly) {
            rule.$el.find('[name*=_value_]').prop('disabled', true);
        }
        if (flags.no_delete) {
            rule.$el.find('[data-delete=rule]').remove();
        }

        this.trigger('afterApplyRuleFlags', rule);
    };

    /**
     * Clear all errors markers
     * @param node {Node,optional} default is root Group
     */
    QueryBuilder.prototype.clearErrors = function(node) {
        node = node || this.model.root;

        if (!node) {
            return;
        }

        node.error = null;

        if (node instanceof Group) {
            node.each(function(rule) {
                rule.error = null;
            }, function(group) {
                this.clearErrors(group);
            }, this);
        }
    };

    /**
     * Add/Remove class .has-error and update error title
     * @param node {Node}
     */
    QueryBuilder.prototype.displayError = function(node) {
        if (this.settings.display_errors) {
            if (node.error === null) {
                node.$el.removeClass('has-error');
            }
            else {
                // translate the text without modifying event array
                var error = $.extend([], node.error, [
                    this.lang.errors[node.error[0]] || node.error[0]
                ]);

                node.$el.addClass('has-error')
                    .find('.error-container').eq(0).attr('title', fmt.apply(null, error));
            }
        }
    };

    /**
     * Trigger a validation error event
     * @param node {Node}
     * @param error {array}
     * @param value {mixed}
     */
    QueryBuilder.prototype.triggerValidationError = function(node, error, value) {
        if (!$.isArray(error)) {
            error = [error];
        }

        var e = this.trigger('validationError', node, error, value);
        if (!e.isDefaultPrevented()) {
            node.error = error;
        }
    };

    /**
     * Destroy the plugin
     */
    QueryBuilder.prototype.destroy = function() {
        this.trigger('beforeDestroy');

        if (this.status.generated_id) {
            this.$el.removeAttr('id');
        }

        this.clear();
        this.model = null;

        this.$el
            .off('.queryBuilder')
            .removeClass('query-builder')
            .removeData('queryBuilder');

        delete this.$el[0].queryBuilder;
    };

    /**
     * Reset the plugin
     */
    QueryBuilder.prototype.reset = function() {
        this.status.group_id = 1;
        this.status.rule_id = 0;

        this.model.root.empty();

        this.addRule(this.model.root);

        this.trigger('afterReset');
    };

    /**
     * Clear the plugin
     */
    QueryBuilder.prototype.clear = function() {
        this.status.group_id = 0;
        this.status.rule_id = 0;

        if (this.model.root) {
            this.model.root.drop();
            this.model.root = null;
        }

        this.trigger('afterClear');
    };

    /**
     * Modify the builder configuration
     * Only options defined in QueryBuilder.modifiable_options are modifiable
     * @param {object}
     */
    QueryBuilder.prototype.setOptions = function(options) {
        // use jQuery utils to filter options keys
        $.makeArray($(Object.keys(options)).filter(QueryBuilder.modifiable_options))
            .forEach(function(opt) {
                this.settings[opt] = options[opt];
            }, this);
    };

    /**
     * Validate the whole builder
     * @return {boolean}
     */
    QueryBuilder.prototype.validate = function() {
        this.clearErrors();

        var that = this;

        var valid = (function parse(group) {
            var done = 0, errors = 0;

            group.each(function(rule) {
                if (!rule.filter) {
                    that.triggerValidationError(rule, 'no_filter', null);
                    errors++;
                    return;
                }

                if (that.mode === "rule" && rule.entity === 'HitsSummary') {
                    that.triggerValidationError(rule, 'hits_summary', null);
                    errors++;
                    return;
                }

                if (rule.operator.nb_inputs !== 0) {
                    var valid = that.validateValue(rule, rule.value);

                    if (valid !== true) {
                        that.triggerValidationError(rule, valid, rule.value);
                        errors++;
                        return;
                    }
                }

                done++;

            }, function(group) {
                if (parse(group)) {
                    done++;
                }
                else {
                    errors++;
                }
            });

            if (errors > 0) {
                return false;
            }
            else if (done === 0 && (!that.settings.allow_empty || !group.isRoot())) {
                that.triggerValidationError(group, 'empty_group', null);
                return false;
            }

            return true;

        }(this.model.root));

        return this.change('validate', valid);
    };

    /**
     * Get an object representing current rules
     * @return {object}
     */
    QueryBuilder.prototype.getRules = function() {
        if (!this.validate()) {
            return {};
        }

        var out = (function parse(group) {
            var data = {
                condition: group.condition,
                rules: []
            };

            if (group.data) {
                data.data = $.extendext(true, 'replace', {}, group.data);
            }

            group.each(function(model) {
                var value = null;
                if (model.operator.nb_inputs !== 0) {
                    value = model.value;
                }

                var rule = {
                    id: model.filter.id,
                    field: model.filter.field,
                    entity: model.entity,
                    'type': model.filter.type,
                    input: model.filter.input,
                    operator: $('#'+model.id).find('.rule-operator-container').find('select').val(),
                    value: value
                };

                if (model.filter.data || model.data) {
                    rule.data = $.extendext(true, 'replace', {}, model.filter.data, model.data);
                }

                data.rules.push(rule);

            }, function(model) {
                data.rules.push(parse(model));
            });

            //return optimizeRules(data);

            return data;

        }(this.model.root));

        return this.change('getRules', out);
    };

    var prepareDroolsJSON = function (data, notRoot) {
        var properties;
        if (notRoot === undefined || notRoot === false ) {
            if (typeof data === 'string') {
                data = JSON.parse(data);
            }
            data['@class'] = 'gov.gtas.model.udr.json.QueryObject';
        }
        data.rules.forEach(function(rule){
            if (rule.id !== undefined) {
                properties = rule.id.split('.');
                rule.entity = properties.shift();
                rule["@class"] = "QueryTerm";
                if (!Array.isArray(rule.value)){
                    rule.value = [rule.value];
                }

                rule.field = properties.join('.');
                delete rule.id;
            } else if (rule.rules !== undefined) {
                rule["@class"] = "QueryObject";
                prepareDroolsJSON(rule, true);
            }
        });
        /*
         // DELETE when Amit gives ok
         if (notRoot === undefined && data.rules.length === 1) {
         data.rules.push(data.rules[0]);
         }
         // END DELETE */

        return data;
    };

    var interpretDroolsJSON = function (data, notRoot) {
        if (notRoot === undefined || notRoot === false) {
            if (typeof data === 'string') {
                data = JSON.parse(data);
            }
            delete data['@class'];
            delete data.summary;
        }
        data.rules.forEach(function(rule) {
            if (rule["@class"] === "QueryTerm") {
                rule.id = [rule.entity, rule.field].join('.');

                // CONVERT rule from array to string if an array of one and not expected to be an array based on operator
                if (['IN', 'NOT IN', 'BETWEEN'].indexOf(rule.operator.toUpperCase()) < 0 && Array.isArray(rule.value) && rule.length === 1) {
                    rule.value = rule.value[0];
                }

                rule.field = [rule.entity, rule.field].join('.');
                // remove when Amit gives go ahead..
                console.log(rule.type);
                rule.type = rule.type.toLowerCase();
                // end remove
            } else if (rule["@class"] === "QueryObject") {
                interpretDroolsJSON(rule, true);
            }

            delete rule['@class'];
        });

        return data;
    };

    var adapterLookup = {
        'DROOLS.IN': interpretDroolsJSON,
        'DROOLS.OUT': prepareDroolsJSON,
        'QueryBuilder.IN': interpretDroolsJSON,
        'QueryBuilder.OUT': prepareDroolsJSON
    };

    /** RULES Wrapper */
    QueryBuilder.prototype.adapter  = function(rules, direction) {
        var key = [this.settings.service, direction.toUpperCase()].join('.');
        console.log(key + ' ' + (adapterLookup[key] ? 'exists' : 'is undefined'));
        return adapterLookup[key](rules);
    };

    /**
     * LOAD rules from object
     * excepts JSON Obj or string representation
     */
    QueryBuilder.prototype.loadRules = function(rules) {
        if (!rules) {
            console.log('requires rules');
            return;
        }
        if (typeof rules === 'string'){
            rules = JSON.parse(rules);
        }

        if( this.settings.service !== undefined ) {
            rules = this.adapter( rules, 'in' );
        }

        this.setRules(rules);
    };

    QueryBuilder.prototype.readOnlyRules = function (rules) {
        var qb = this,
            makeReadOnly = function () {
                qb.$el.find('a.remove, .selectize-dropdown-content').remove();
                qb.$el.find('select.bs-select-hidden').prop('disabled', true);
                qb.$el.find('input').prop('disabled', true);
            };
        qb.loadRules(rules);
        qb.$el.find('.rule-header, .group-actions').remove();
        setTimeout(makeReadOnly, 1000);
    };

    var combinedOperator = {
        'equal': 'in',
        'not equal': 'not in'
    };

    function optimizeRules(data) {
        var lookup = {};
        var key, operator;
        data.rules.forEach(function(rule, index){
            key = rule.field;
            operator = rule.operator;
            if (key !== undefined && rule['type'] === 'string') {
                if (lookup[key] === undefined) {
                    lookup[key] = { };
                }
                if (lookup[key][operator] === undefined) {
                    lookup[key][operator] = {};
                    lookup[key][operator].value = [];
                    lookup[key][operator].indexes = [];
                    lookup[key][operator]['type'] = rule['type'];
                }
                lookup[key][operator].value.push(rule.value);
                lookup[key][operator].indexes.push(index);
            } else if (rule.rules !== undefined) {
                optimizeRules(rule);
            }
        });
        Object.keys(lookup).forEach(function(key) {
            Object.keys(lookup[key]).forEach(function(operator){
                var op = combinedOperator[operator];
                if (lookup[key][operator].value.length > 1 && op !== undefined) {
                    while(lookup[key][operator].indexes.length){
                        data.rules.splice(lookup[key][operator].indexes.pop(), 1);
                    }
                    data.rules.push({
                        id: key,
                        field: key,
                        'type': lookup[key][operator]['type'],
                        input: 'select',
                        multiple: true,
                        plugin: 'selectize',
                        operator: op,
                        value: lookup[key][operator].value
                    });
                }
            });
        });
//    debugger;
        return data;
    }
    /* Set Mode */
    QueryBuilder.prototype.setMode = function (mode) {
        this.mode = mode;
    };
    /**
     * SAVE rules from object
     */
    QueryBuilder.prototype.getDrools = function () {
        var rules = JSON.stringify(this.getRules());
        if (rules === "{}") {
            console.log('rules can not be empty');
            return false;
        }

        if( this.settings.service !== undefined ) {
            rules = this.adapter(rules, 'out');
        }

        return rules;
    };


    /**
     * DELETE rules from object
     */
    QueryBuilder.prototype.deleteRules = function(queryName, savedQueryNames) {
        if (!queryName) {
            console.log('requires name');
            return;
        }

        localStorage.removeItem(queryName);
        localStorage.setItem('savedQueryNames', JSON.stringify(savedQueryNames));
    };

    /**
     * Set rules from object
     * @param data {object}
     */
    QueryBuilder.prototype.setRules = function(data) {
        if (!data || !data.rules || (data.rules.length===0 && !this.settings.allow_empty)) {
            error('Incorrect data object passed');
        }

        this.clear();
        this.setRoot(false, data.data);

        data = this.change('setRules', data);

        var that = this;

        (function add(data, group){
            if (group === null) {
                return;
            }

            if (data.condition === undefined) {
                data.condition = that.settings.default_condition;
            }
            else if (that.settings.conditions.indexOf(data.condition) == -1) {
                error('Invalid condition "{0}"', data.condition);
            }

            group.condition = data.condition;

            data.rules.forEach(function(item) {
                var model, periods;
                if (item.rules && item.rules.length>0) {
                    if (that.settings.allow_groups != -1 && that.settings.allow_groups < group.level) {
                        that.reset();
                        error('No more than {0} groups are allowed', that.settings.allow_groups);
                    }
                    else {
                        model = that.addGroup(group, false, item.data);
                        add(item, model);
                    }
                }
                else {
                    if (item.id === undefined) {
                        error('Missing rule field id');
                    }
                    if (item.operator === undefined) {
                        item.operator = 'equal';
                    }

                    model = that.addRule(group, item.data);
                    if (model === null) {
                        return;
                    }

                    periods = item.id.split('.');

                    model.entity = periods.shift();
                    model.filter = that.getFilterById(item.id);
                    model.operator = that.getOperatorByType(item.operator);
                    model.flags = that.parseRuleFlags(item);

                    if (model.operator.nb_inputs !== 0 && item.value !== undefined) {
                        model.value = item.value;
                    }
                }
            });

        }(data, this.model.root));
    };

    /**
     * Check if a value is correct for a filter
     * @param rule {Rule}
     * @param value {string|string[]|undefined}
     * @return {array|true}
     */
    QueryBuilder.prototype.validateValue = function(rule, value) {
        var validation = rule.filter.validation || {},
            result = true;

        if (validation.callback) {
            result = validation.callback.call(this, value, rule);
        }
        else {
            result = this.validateValueInternal(rule, value);
        }

        return this.change('validateValue', result, value, rule);
    };

    /**
     * Default validation function
     * @param rule {Rule}
     * @param value {string|string[]|undefined}
     * @return {array|true}
     */
    QueryBuilder.prototype.validateValueInternal = function(rule, value) {
        var filter = rule.filter,
            operator = rule.operator,
            validation = filter.validation || {},
            result = true,
            tmp;

        if (rule.operator.nb_inputs === 1) {
            value = [value];
        }
        else {
            value = value;
        }

        for (var i=0; i<operator.nb_inputs; i++) {

            switch (filter.input) {
                case 'radio':
                    if (value[i] === undefined) {
                        result = ['radio_empty'];
                        break;
                    }
                    break;

                case 'checkbox':
                    if (value[i] === undefined || value[i].length === 0) {
                        result = ['checkbox_empty'];
                        break;
                    }
                    else if (!operator.multiple && value[i].length > 1) {
                        result = ['operator_not_multiple', this.lang[operator.type] || operator.type];
                        break;
                    }
                    break;

                case 'select':
                    if (filter.multiple) {
                        if (value[i] === undefined || value[i].length === 0) {
                            result = ['select_empty'];
                            break;
                        }
                        else if (!operator.multiple && value[i].length > 1) {
                            result = ['operator_not_multiple', this.lang[operator.type] || operator.type];
                            break;
                        }
                    }
                    else {
                        if (value[i] === undefined) {
                            result = ['select_empty'];
                            break;
                        }
                    }
                    break;

                default:
                    switch (QueryBuilder.types[filter.type]) {
                        case 'string':
                            if (value[i] === undefined || value[i].length === 0) {
                                result = ['string_empty'];
                                break;
                            }
                            if (validation.min !== undefined) {
                                if (value[i].length < parseInt(validation.min)) {
                                    result = ['string_exceed_min_length', validation.min];
                                    break;
                                }
                            }
                            if (validation.max !== undefined) {
                                if (value[i].length > parseInt(validation.max)) {
                                    result = ['string_exceed_max_length', validation.max];
                                    break;
                                }
                            }
                            if (validation.format) {
                                if (typeof validation.format === 'string') {
                                    validation.format = new RegExp(validation.format);
                                }
                                if (!validation.format.test(value[i])) {
                                    result = ['string_invalid_format', validation.format];
                                    break;
                                }
                            }
                            break;

                        case 'number':
                            if (value[i] === undefined || isNaN(value[i])) {
                                result = ['number_nan'];
                                break;
                            }
                            if (filter.type == 'integer') {
                                if (parseInt(value[i]) != value[i]) {
                                    result = ['number_not_integer'];
                                    break;
                                }
                            }
                            else {
                                if (parseFloat(value[i]) != value[i]) {
                                    result = ['number_not_double'];
                                    break;
                                }
                            }
                            if (validation.min !== undefined) {
                                if (value[i] < parseFloat(validation.min)) {
                                    result = ['number_exceed_min', validation.min];
                                    break;
                                }
                            }
                            if (validation.max !== undefined) {
                                if (value[i] > parseFloat(validation.max)) {
                                    result = ['number_exceed_max', validation.max];
                                    break;
                                }
                            }
                            if (validation.step !== undefined) {
                                var v = value[i]/validation.step;
                                if (parseInt(v) != v) {
                                    result = ['number_wrong_step', validation.step];
                                    break;
                                }
                            }
                            break;

                        case 'datetime':
                            if (value[i] === undefined || value[i].length === 0) {
                                result = ['datetime_empty'];
                                break;
                            }

                            // we need MomentJS
                            if (validation.format) {
                                if (!('moment' in window)) {
                                    error('MomentJS is required for Date/Time validation. Get it here http://momentjs.com');
                                }

                                var datetime = moment(value[i], validation.format);
                                if (!datetime.isValid()) {
                                    result = ['datetime_invalid'];
                                    break;
                                }
                                else {
                                    if (validation.min) {
                                        if (datetime < moment(validation.min, validation.format)) {
                                            result = ['datetime_exceed_min', validation.min];
                                            break;
                                        }
                                    }
                                    if (validation.max) {
                                        if (datetime > moment(validation.max, validation.format)) {
                                            result = ['datetime_exceed_max', validation.max];
                                            break;
                                        }
                                    }
                                }
                            }
                            break;

                        case 'boolean':
                            tmp = value[i].trim().toLowerCase();
                            if (tmp !== 'true' && tmp !== 'false' && tmp !== '1' && tmp !== '0' && value[i] !== 1 && value[i] !== 0) {
                                result = ['boolean_not_valid'];
                                break;
                            }
                    }
            }

            if (result !== true) {
                break;
            }
        }

        return result;
    };

    /**
     * Returns an incremented group ID
     * @return {string}
     */
    QueryBuilder.prototype.nextGroupId = function() {
        return this.status.id + '_group_' + (this.status.group_id++);
    };

    /**
     * Returns an incremented rule ID
     * @return {string}
     */
    QueryBuilder.prototype.nextRuleId = function() {
        return this.status.id + '_rule_' + (this.status.rule_id++);
    };

    /**
     * Returns the operators for a filter
     * @param filter {string|object} (filter id name or filter object)
     * @return {object[]}
     */
    QueryBuilder.prototype.getOperators = function(filter) {
        if (typeof filter === 'string') {
            filter = this.getFilterById(filter);
        }

        var result = [];

        for (var i=0, l=this.operators.length; i<l; i++) {
            // filter operators check
            if (filter.operators) {
                if (filter.operators.indexOf(this.operators[i].type) == -1) {
                    continue;
                }
            }
            // type check
            else if (this.operators[i].apply_to.indexOf(QueryBuilder.types[filter.type]) == -1) {
                continue;
            }

            result.push(this.operators[i]);
        }

        // keep sort order defined for the filter
        if (filter.operators) {
            result.sort(function(a, b) {
                return filter.operators.indexOf(a.type) - filter.operators.indexOf(b.type);
            });
        }

        return this.change('getOperators', result, filter);
    };

    /**
     * Returns a particular filter by its id
     * @param filterId {string}
     * @return {object|null}
     */
    QueryBuilder.prototype.getFilterById = function(id) {
        if (id == '-1') {
            return null;
        }

        //Custom fix to check against and load from the full filter list rather than the filters for "Last entity loaded"
        var totalFilterList = this.entities[id.split('.')[0]].columns;
        
        if(totalFilterList.length > 0){
    	    for (var i=0, l=totalFilterList.length; i<l; i++) {
    	        if (totalFilterList[i].id == id) {
    	            return totalFilterList[i];
    	        }
    	    }
        } else{  
    	    for (var i=0, l=this.filters.length; i<l; i++) {
    	        if (this.filters[i].id == id) {
    	            return this.filters[i];
    	        }
    	    }
        }

        error('Undefined filter "{0}"', id);
    };

    /**
     * Return a particular operator by its type
     * @param type {string}
     * @return {object|null}
     */
    QueryBuilder.prototype.getOperatorByType = function(type) {
        if (type == '-1') {
            return null;
        }

        for (var i=0, l=this.operators.length; i<l; i++) {
            if (this.operators[i].type == type) {
                return this.operators[i];
            }
        }

        error('Undefined operator  "{0}"', type);
    };

    /**
     * Return a particular entity by its entity's name
     * @param type {string}
     * @return {object|null}
     */
    QueryBuilder.prototype.getEntityByName = function(entityName) {
        if (entityName == '-1') {
            return null;
        }
        var entityKeys = Object.keys(this.entities);
        for (var i=0, l=entityKeys.length; i<l; i++) {
            if (entityKeys[i] === entityName) {
                return entityKeys[i];
            }
        }

        error('Undefined entity name  "{0}"', entityName);
    };

    /**
     * Returns rule value
     * @param rule {Rule}
     * @return {mixed}
     */
    QueryBuilder.prototype.getRuleValue = function(rule) {
        var filter = rule.filter,
            operator = rule.operator,
            value = [];

        if (filter.valueGetter) {
            value = filter.valueGetter.call(this, rule);
        }
        else {
            var $value = rule.$el.find('.rule-value-container'),
                tmp;

            for (var i=0; i<operator.nb_inputs; i++) {
                var name = rule.id + '_value_' + i;

                switch (filter.input) {
                    case 'radio':
                        value.push($value.find('[name='+ name +']:checked').val());
                        break;

                    case 'checkbox':
                        tmp = [];
                        $value.find('[name='+ name +']:checked').each(function() {
                            tmp.push($(this).val());
                        });
                        value.push(tmp);
                        break;

                    case 'select':
                        if (filter.multiple) {
                            tmp = [];
                            $value.find('[name='+ name +'] option:selected').each(function() {
                                tmp.push($(this).val());
                            });
                            value.push(tmp);
                        }
                        else {
                            value.push($value.find('[name='+ name +'] option:selected').val());
                        }
                        break;

                    default:
                        value.push($value.find('[name='+ name +']').val());
                }
            }

            if (operator.nb_inputs === 1) {
                value = value[0];
            }

            // @deprecated
            if (filter.valueParser) {
                value = filter.valueParser.call(this, rule, value);
            }
        }

        return this.change('getRuleValue', value, rule);
    };

    /**
     * Sets the value of a rule.
     * @param rule {Rule}
     * @param value {mixed}
     */
    QueryBuilder.prototype.setRuleValue = function(rule, value) {
        var filter = rule.filter,
            operator = rule.operator;

        if (filter.valueSetter) {
            filter.valueSetter.call(this, rule, value);
        }
        else {
            var $value = rule.$el.find('.rule-value-container');

            if (operator.nb_inputs == 1) {
                value = [value];
            }
            else {
                value = value;
            }

            for (var i=0; i<operator.nb_inputs; i++) {
                var name = rule.id +'_value_'+ i;

                switch (filter.input) {
                    case 'radio':
                        $value.find('[name='+ name +'][value="'+ value[i] +'"]').prop('checked', true).trigger('change');
                        break;

                    case 'checkbox':
                        if (!$.isArray(value[i])) {
                            value[i] = [value[i]];
                        }
                        value[i].forEach(function(value) {
                            $value.find('[name='+ name +'][value="'+ value +'"]').prop('checked', true).trigger('change');
                        });
                        break;

                    default:
                        $value.find('[name='+ name +']').val(value[i]).trigger('change');
                        break;
                }
            }
        }
    };

    /**
     * Clean rule flags.
     * @param rule {object}
     * @return {object}
     */
    QueryBuilder.prototype.parseRuleFlags = function(rule) {
        var flags = $.extend({}, this.settings.default_rule_flags);

        if (rule.readonly) {
            $.extend(flags, {
                filter_readonly: true,
                operator_readonly: true,
                value_readonly: true,
                no_delete: true
            });
        }

        if (rule.flags) {
            $.extend(flags, rule.flags);
        }

        return this.change('parseRuleFlags', flags, rule);
    };

    /**
     * Returns group HTML
     * @param group_id {string}
     * @param level {int}
     * @return {string}
     */
    QueryBuilder.prototype.getGroupTemplate = function(group_id, level) {
        var h = '\
<dl id="'+ group_id +'" class="rules-group-container"> \
  <dt class="rules-group-header"> \
    <div class="btn-group pull-right group-actions"> \
      <button type="button" class="btn btn-xs btn-primary" data-add="rule"> \
        <i class="' + this.icons.add_rule + '"></i> '+ this.lang.add_rule +' \
      </button> \
      '+ (this.settings.allow_groups===-1 || this.settings.allow_groups>=level ?
            '<button type="button" class="btn btn-xs btn-primary" data-add="group"> \
          <i class="' + this.icons.add_group + '"></i> '+ this.lang.add_group +' \
        </button>'
                :'') +' \
      '+ (level>1 ?
            '<button type="button" class="btn btn-xs btn-primary" data-delete="group"> \
          <i class="' + this.icons.remove_group + '"></i> '+ this.lang.delete_group +' \
        </button>'
                : '') +' \
    </div> \
    <div class="btn-group group-conditions"> \
      '+ this.getGroupConditions(group_id, level) +' \
    </div> \
    '+ (this.settings.display_errors ?
            '<div class="error-container"><i class="' + this.icons.error + '"></i></div>'
                :'') +'\
  </dt> \
  <dd class=rules-group-body> \
    <ul class=rules-list></ul> \
  </dd> \
</dl>';

        return this.change('getGroupTemplate', h, level);
    };

    /**
     * Returns group conditions HTML
     * @param group_id {string}
     * @param level {int}
     * @return {string}
     */
    QueryBuilder.prototype.getGroupConditions = function(group_id, level) {
        var h = '';

        for (var i=0, l=this.settings.conditions.length; i<l; i++) {
            var cond = this.settings.conditions[i],
                label = this.lang.conditions[cond] || cond;

            h+= '\
        <label class="btn btn-xs btn-primary"> \
          <input type="radio" name="'+ group_id +'_cond" value="'+ cond +'"> '+ label +' \
        </label>';
        }

        return this.change('getGroupConditions', h, level);
    };

    /**
     * Returns rule HTML
     * @param rule_id {string}
     * @return {string}
     */
    QueryBuilder.prototype.getRuleTemplate = function(rule_id) {
        var h = '\
<li id="'+ rule_id +'" class="rule-container"> \
  <div class="rule-header"> \
  <div class="btn-group pull-right rule-actions"> \
    <button type="button" class="btn btn-xs btn-primary" data-delete="rule"> \
      <i class="' + this.icons.remove_rule + '"></i> '+ this.lang.delete_rule +' \
    </button> \
  </div> \
  </div> \
  '+ (this.settings.display_errors ?
            '<div class="error-container"><i class="' + this.icons.error + '"></i></div>'
                :'') +'\
  <div class="rule-entity-container"></div> \
  <div class="rule-field-container"></div> \
  <div class="rule-filter-container"></div> \
  <div class="rule-operator-container"></div> \
  <div class="rule-value-container"></div> \
</li>';

        return this.change('getRuleTemplate', h);
    };

    /**
     * Returns rule filter <select> HTML
     * @param rule {Rule}
     * @param filters {array}
     * @return {string}
     */
    QueryBuilder.prototype.getRuleFilterSelect = function(rule, filters) {
        var optgroup = null;

        var h = '<select class="form-control" name="'+ rule.id +'_filter">';
        h+= '<option value="-1">'+ this.settings.select_placeholder +'</option>';

        filters.forEach(function(filter) {
            if (optgroup != filter.optgroup) {
                if (optgroup !== null) h+= '</optgroup>';
                optgroup = filter.optgroup;
                if (optgroup !== null) h+= '<optgroup label="'+ optgroup +'">';
            }

            h+= '<option value="'+ filter.id +'">'+ filter.label +'</option>';
        });

        if (optgroup !== null) h+= '</optgroup>';
        h+= '</select>';

        return this.change('getRuleFilterSelect', h, rule);
    };

    /**
     * Returns rule operator <select> HTML
     * @param rule {Rule}
     * @param operators {object}
     * @return {string}
     */
    QueryBuilder.prototype.getRuleOperatorSelect = function(rule, operators) {
        var h = '<select class="form-control" name="'+ rule.id +'_operator">';

        for (var i=0, l=operators.length; i<l; i++) {
            var label = this.lang.operators[operators[i].type] || operators[i].type;
            h+= '<option value="'+ operators[i].type +'">'+ label +'</option>';
        }

        h+= '</select>';

        return this.change('getRuleOperatorSelect', h, rule);
    };

    /**
     * Returns rule entity <select> HTML
     * @param rule {Rule}
     * @param entities {object}
     * @return {string}
     */
    QueryBuilder.prototype.getRuleEntitySelect = function(rule) {
        var entities = Object.keys(this.entities);
        var h = '<label for="'+ rule.id +'_entity">Entity:</label> \
            <select class="form-control entity-name" name="'+ rule.id +'_entity" id="'+ rule.id +'_entity">';

        h+= '<option value="-1"> - </option>';

        for (var i=0, l=entities.length, id, label, selected; i<l; i++) {
            id = entities[i];
            label = this.entities[entities[i]].label;
            selected = rule.filter && rule.filter.id.split('.')[0] === id ? 'selected' : '';
            h+= '<option value="'+ id +'" '+ selected +'>'+ label +'</option>';
        }

        h+= '</select>';

        return this.change('getRuleEntitySelect', h, rule);
    };

    /**
     * Return the rule value HTML
     * @param rule {Rule}
     * @param filter {object}
     * @param value_id {int}
     * @return {string}
     */
    QueryBuilder.prototype.getRuleInput = function(rule, value_id) {
        var filter = rule.filter,
            validation = rule.filter.validation || {},
            name = rule.id +'_value_'+ value_id,
            c = filter.vertical ? ' class=block' : '',
            h = '';

        if (typeof filter.input === 'function') {
            h = filter.input.call(this, rule, name);
        }
        else {
            switch (filter.input) {
                case 'radio':
                    iterateOptions(filter.values, function(key, val) {
                        h+= '<label'+ c +'><input type="radio" name="'+ name +'" value="'+ key +'"> '+ val +'</label> ';
                    });
                    break;

                case 'checkbox':
                    iterateOptions(filter.values, function(key, val) {
                        h+= '<label'+ c +'><input type="checkbox" name="'+ name +'" value="'+ key +'"> '+ val +'</label> ';
                    });
                    break;

                case 'select':
                    h+= '<select class="form-control" name="'+ name +'"'+ (filter.multiple ? ' multiple' : '') +'>';
                    iterateOptions(filter.values, function(key, val) {
                        h+= '<option value="'+ key +'"> '+ val +'</option> ';
                    });
                    h+= '</select>';
                    break;

                case 'textarea':
                    h+= '<textarea class="form-control" name="'+ name +'"';
                    if (filter.size) h+= ' cols="'+ filter.size +'"';
                    if (filter.rows) h+= ' rows="'+ filter.rows +'"';
                    if (validation.min !== undefined) h+= ' minlength="'+ validation.min +'"';
                    if (validation.max !== undefined) h+= ' maxlength="'+ validation.max +'"';
                    if (filter.placeholder) h+= ' placeholder="'+ filter.placeholder +'"';
                    h+= '></textarea>';
                    break;

                default:
                    switch (QueryBuilder.types[filter.type]) {
                        case 'number':
                            h+= '<input class="form-control" type="number" name="'+ name +'"';
                            if (validation.step !== undefined) h+= ' step="'+ validation.step +'"';
                            if (validation.min !== undefined) h+= ' min="'+ validation.min +'"';
                            if (validation.max !== undefined) h+= ' max="'+ validation.max +'"';
                            if (filter.placeholder) h+= ' placeholder="'+ filter.placeholder +'"';
                            if (filter.size) h+= ' size="'+ filter.size +'"';
                            h+= '>';
                            break;

                        default:
                            h+= '<input class="form-control" type="text" name="'+ name +'"';
                            if (filter.placeholder) h+= ' placeholder="'+ filter.placeholder +'"';
                            if (filter.type === 'string' && validation.min !== undefined) h+= ' minlength="'+ validation.min +'"';
                            if (filter.type === 'string' && validation.max !== undefined) h+= ' maxlength="'+ validation.max +'"';
                            if (filter.size) h+= ' size="'+ filter.size +'"';
                            h+= '>';
                    }
            }
        }

        return this.change('getRuleInput', h, rule, name);
    };

// Model CLASS
// ===============================
    /**
     * Main object storing data model and emitting events
     * ---------
     * Access Node object stored in jQuery objects
     * @param el {jQuery|Node}
     * @return {Node}
     */
    function Model(el) {
        if (!(this instanceof Model)) {
            return Model.getModel(el);
        }

        this.root = null;
        this.$ = $(this);
    }

    $.extend(Model.prototype, {
        trigger: function(type) {
            this.$.triggerHandler(type, aps.call(arguments, 1));
            return this;
        },

        on: function() {
            this.$.on.apply(this.$, aps.call(arguments));
            return this;
        },

        off: function() {
            this.$.off.apply(this.$, aps.call(arguments));
            return this;
        },

        once: function() {
            this.$.one.apply(this.$, aps.call(arguments));
            return this;
        }
    });

    /**
     * Access Node object stored in jQuery objects
     * @param el {jQuery|Node}
     * @return {Node}
     */
    Model.getModel = function(el) {
        if (!el) {
            return null;
        }
        else if (el instanceof Node) {
            return el;
        }
        else {
            return $(el).data('queryBuilderModel');
        }
    };

    /*
     * Define Node properties with getter and setter
     * Update events are emitted in the setter through root Model (if any)
     */
    function defineModelProperties(obj, fields) {
        fields.forEach(function(field) {
            Object.defineProperty(obj.prototype, field, {
                enumerable: true,
                get: function() {
                    return this.__[field];
                },
                set: function(value) {
                    var oldValue = (this.__[field] !== null && typeof this.__[field] == 'object') ?
                        $.extend({}, this.__[field]) :
                        this.__[field];

                    this.__[field] = value;

                    if (this.model !== null) {
                        this.model.trigger('update', this, field, value, oldValue);
                    }
                }
            });
        });
    }


// Node abstract CLASS
// ===============================
    /**
     * @param {Node}
     * @param {jQuery}
     */
    var Node = function(parent, $el) {
        if (!(this instanceof Node)) {
            return new Node();
        }

        Object.defineProperty(this, '__', { value: {}});

        $el.data('queryBuilderModel', this);

        this.__.level = 0;
        this.__.error = null;
        this.__.data = undefined;
        this.$el = $el;
        this.id = $el[0].id;
        this.model = null;
        this.parent = parent;
    };

    defineModelProperties(Node, ['level', 'error', 'data']);

    Object.defineProperty(Node.prototype, 'parent', {
        enumerable: true,
        get: function() {
            return this.__.parent;
        },
        set: function(value) {
            this.__.parent = value;
            this.level = value === null ? 1 : value.level+1;
            this.model = value === null ? null : value.model;
        }
    });

    /**
     * Check if this Node is the root
     * @return {boolean}
     */
    Node.prototype.isRoot = function() {
        return (this.level === 1);
    };

    /**
     * Return node position inside parent
     * @return {int}
     */
    Node.prototype.getPos = function() {
        if (this.isRoot()) {
            return -1;
        }
        else {
            return this.parent.getNodePos(this);
        }
    };

    /**
     * Delete self
     */
    Node.prototype.drop = function() {
        if (this.model !== null) {
            this.model.trigger('drop', this);
        }

        if (!this.isRoot()) {
            this.parent._dropNode(this);
            this.parent = null;
        }
    };

    /**
     * Move itself after another Node
     * @param {Node}
     * @return {Node} self
     */
    Node.prototype.moveAfter = function(node) {
        if (this.isRoot()) return;

        this.parent._dropNode(this);
        node.parent._addNode(this, node.getPos()+1);
        return this;
    };

    /**
     * Move itself at the beginning of parent or another Group
     * @param {Group,optional}
     * @return {Node} self
     */
    Node.prototype.moveAtBegin = function(target) {
        if (this.isRoot()) return;

        if (target === undefined) {
            target = this.parent;
        }

        this.parent._dropNode(this);
        target._addNode(this, 0);
        return this;
    };

    /**
     * Move itself at the end of parent or another Group
     * @param {Group,optional}
     * @return {Node} self
     */
    Node.prototype.moveAtEnd = function(target) {
        if (this.isRoot()) return;

        if (target === undefined) {
            target = this.parent;
        }

        this.parent._dropNode(this);
        target._addNode(this, target.length());
        return this;
    };


// GROUP CLASS
// ===============================
    /**
     * @param {Group}
     * @param {jQuery}
     */
    var Group = function(parent, $el) {
        if (!(this instanceof Group)) {
            return new Group(parent, $el);
        }

        Node.call(this, parent, $el);

        this.rules = [];
        this.__.condition = null;
    };

    Group.prototype = Object.create(Node.prototype);
    Group.prototype.constructor = Group;

    defineModelProperties(Group, ['condition']);

    /**
     * Empty the Group
     */
    Group.prototype.empty = function() {
        this.each('reverse', function(rule) {
            rule.drop();
        }, function(group) {
            group.drop();
        });
    };

    /**
     * Delete self
     */
    Group.prototype.drop = function() {
        this.empty();
        Node.prototype.drop.call(this);
    };

    /**
     * Return the number of children
     * @return {int}
     */
    Group.prototype.length = function() {
        return this.rules.length;
    };

    /**
     * Add a Node at specified index
     * @param {Node}
     * @param {int,optional}
     * @return {Node} the inserted node
     */
    Group.prototype._addNode = function(node, index) {
        if (index === undefined) {
            index = this.length();
        }

        this.rules.splice(index, 0, node);
        node.parent = this;

        if (this.model !== null) {
            this.model.trigger('add', node, index);
        }

        return node;
    };

    /**
     * Add a Group by jQuery element at specified index
     * @param {jQuery}
     * @param {int,optional}
     * @return {Group} the inserted group
     */
    Group.prototype.addGroup = function($el, index) {
        return this._addNode(new Group(this, $el), index);
    };

    /**
     * Add a Rule by jQuery element at specified index
     * @param {jQuery}
     * @param {int,optional}
     * @return {Rule} the inserted rule
     */
    Group.prototype.addRule = function($el, index) {
        return this._addNode(new Rule(this, $el), index);
    };

    /**
     * Delete a specific Node
     * @param {Node}
     * @return {Group} self
     */
    Group.prototype._dropNode = function(node) {
        var index = this.getNodePos(node);
        if (index !== -1) {
            node.parent = null;
            this.rules.splice(index, 1);
        }

        return this;
    };

    /**
     * Return position of a child Node
     * @param {Node}
     * @return {int}
     */
    Group.prototype.getNodePos = function(node) {
        return this.rules.indexOf(node);
    };

    /**
     * Iterate over all Nodes
     * @param {boolean,optional} iterate in reverse order, required if you delete nodes
     * @param {function} callback for Rules
     * @param {function,optional} callback for Groups
     * @return {boolean}
     */
    Group.prototype.each = function(reverse, cbRule, cbGroup, context) {
        if (typeof reverse === 'function') {
            context = cbGroup;
            cbGroup = cbRule;
            cbRule = reverse;
            reverse = false;
        }
        context = context === undefined ? null : context;

        var i = reverse ? this.rules.length-1 : 0,
            l = reverse ? 0 : this.rules.length-1,
            c = reverse ? -1 : 1,
            next = function(){ return reverse ? i>=l : i<=l; },
            stop = false;

        for (; next(); i+=c) {
            if (this.rules[i] instanceof Group) {
                if (cbGroup !== undefined) {
                    stop = cbGroup.call(context, this.rules[i]) === false;
                }
            }
            else {
                stop = cbRule.call(context, this.rules[i]) === false;
            }

            if (stop) {
                break;
            }
        }

        return !stop;
    };

    /**
     * Return true if the group contains a particular Node
     * @param {Node}
     * @param {boolean,optional} recursive search
     * @return {boolean}
     */
    Group.prototype.contains = function(node, deep) {
        if (this.getNodePos(node) !== -1) {
            return true;
        }
        else if (!deep) {
            return false;
        }
        else {
            // the loop will return with false as soon as the Node is found
            return !this.each(function(rule) {
                return true;
            }, function(group) {
                return !group.contains(node, true);
            });
        }
    };


// RULE CLASS
// ===============================
    /**
     * @param {Group}
     * @param {jQuery}
     */
    var Rule = function(parent, $el) {
        if (!(this instanceof Rule)) {
            return new Rule(parent, $el);
        }

        Node.call(this, parent, $el);

        this.__.entity = null;
        this.__.filter = null;
        this.__.operator = null;
        this.__.flags = {};
        this.__.value = undefined;
    };

    Rule.prototype = Object.create(Node.prototype);
    Rule.prototype.constructor = Rule;

    defineModelProperties(Rule, ['entity', 'filter', 'operator', 'flags', 'value']);


    QueryBuilder.Group = Group;
    QueryBuilder.Rule = Rule;

    /**
     * Utility to iterate over radio/checkbox/selection options.
     * it accept three formats: array of values, map, array of 1-element maps
     *
     * @param options {object|array}
     * @param tpl {callable} (takes key and text)
     */
    function iterateOptions(options, tpl) {
        if (options) {
            if ($.isArray(options)) {
                options.forEach(function(entry) {
                    // array of one-element maps
                    if ($.isPlainObject(entry)) {
                        $.each(entry, function(key, val) {
                            tpl(key, val);
                            return false; // break after first entry
                        });
                    }
                    // array of values
                    else {
                        tpl(entry, entry);
                    }
                });
            }
            // unordered map
            else {
                $.each(options, function(key, val) {
                    tpl(key, val);
                });
            }
        }
    }

    /**
     * Replaces {0}, {1}, ... in a string
     * @param str {string}
     * @param args,... {string|int|float}
     * @return {string}
     */
    function fmt(str, args) {
        args = Array.prototype.slice.call(arguments);

        return str.replace(/{([0-9]+)}/g, function(m, i) {
            return args[parseInt(i)+1];
        });
    }

    /**
     * Output internal error with jQuery.error
     * @see fmt
     */
    function error() {
        $.error(fmt.apply(null, arguments));
    }

    /**
     * Change type of a value to int or float
     * @param value {mixed}
     * @param type {string} 'integer', 'double' or anything else
     * @param boolAsInt {boolean} return 0 or 1 for booleans
     * @return {mixed}
     */
    function changeType(value, type, boolAsInt) {
        switch (type) {
            case 'integer': return parseInt(value);
            case 'double': return parseFloat(value);
            case 'boolean':
                var bool = value.trim().toLowerCase() === "true" || value.trim() === '1' || value === 1;
                return  boolAsInt ? (bool ? 1 : 0) : bool;
            default: return value;
        }
    }

    /**
     * Escape string like mysql_real_escape_string
     * @param value {string}
     * @return {string}
     */
    function escapeString(value) {
        if (typeof value !== 'string') {
            return value;
        }

        return value
            .replace(/[\0\n\r\b\\\'\"]/g, function(s) {
                switch(s) {
                    case '\0': return '\\0';
                    case '\n': return '\\n';
                    case '\r': return '\\r';
                    case '\b': return '\\b';
                    default:   return '\\' + s;
                }
            })
            // uglify compliant
            .replace(/\t/g, '\\t')
            .replace(/\x1a/g, '\\Z');
    }

    /**
     * Escape value for use in regex
     * @param value {string}
     * @return {string}
     */
    function escapeRegExp(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
    }

    $.fn.queryBuilder = function(option) {
        if (this.length > 1) {
            error('Unable to initialize on multiple target');
        }

        var data = this.data('queryBuilder'),
            options = (typeof option == 'object' && option) || {};

        if (!data && option == 'destroy') {
            return this;
        }
        if (!data) {
            this.data('queryBuilder', new QueryBuilder(this, options));
        }
        if (typeof option == 'string') {
            return data[option].apply(data, Array.prototype.slice.call(arguments, 1));
        }

        return this;
    };

    $.fn.queryBuilder.constructor = QueryBuilder;
    $.fn.queryBuilder.defaults = QueryBuilder.defaults;
    $.fn.queryBuilder.extend = QueryBuilder.extend;
    $.fn.queryBuilder.define = QueryBuilder.define;
    $.fn.queryBuilder.regional = QueryBuilder.regional;

    /*!
     * jQuery QueryBuilder Awesome Bootstrap Checkbox
     * Applies Awesome Bootstrap Checkbox for checkbox and radio inputs.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

    QueryBuilder.define('bt-checkbox', function(options) {
        if (options.font == 'glyphicons') {
            var injectCSS = document.createElement('style');
            injectCSS.innerHTML = '\
.checkbox input[type=checkbox]:checked + label:after { \
    font-family: "Glyphicons Halflings"; \
    content: "\\e013"; \
} \
.checkbox label:after { \
    padding-left: 4px; \
    padding-top: 2px; \
    font-size: 9px; \
}';
            document.body.appendChild(injectCSS);
        }

        this.on('getRuleInput.filter', function(h, rule, name) {
            var filter = rule.filter;

            if ((filter.input === 'radio' || filter.input === 'checkbox') && !filter.plugin) {
                h.value = '';

                if (!filter.colors) {
                    filter.colors = {};
                }
                if (filter.color) {
                    filter.colors._def_ = filter.color;
                }

                var style = filter.vertical ? ' style="display:block"' : '',
                    i = 0, color, id;

                iterateOptions(filter.values, function(key, val) {
                    color = filter.colors[key] || filter.colors._def_ || options.color;
                    id = name +'_'+ (i++);

                    h.value+= '\
<div'+ style +' class="'+ filter.input +' '+ filter.input +'-'+ color +'"> \
  <input type="'+ filter.input +'" name="'+ name +'" id="'+ id +'" value="'+ key +'"> \
  <label for="'+ id +'">'+ val +'</label> \
</div>';
                });
            }
        });
    }, {
        font: 'glyphicons',
        color: 'default'
    });

    /*!
     * jQuery QueryBuilder Bootstrap Selectpicker
     * Applies Bootstrap Select on filters and operators combo-boxes.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

    QueryBuilder.define('bt-selectpicker', function(options) {
        if (!$.fn.selectpicker || !$.fn.selectpicker.Constructor) {
            error('Bootstrap Select is required to use "bt-selectpicker" plugin. Get it here: http://silviomoreto.github.io/bootstrap-select');
        }

        // init selectpicker
        this.on('afterCreateEntities', function(e, rule) {
            rule.$el.find('.rule-entity-container select').removeClass('form-control').selectpicker(options);
        });

        this.on('afterCreateEntityFields', function(e, rule) {
            rule.$el.find('.rule-field-container select').removeClass('form-control').selectpicker(options);
        });

        //this.on('afterUpdateQueryNamesList', function(e, queryName) {
        //    if (this.savedQueryNames === undefined) {
        //        this.savedQueryNames = $('#saved-query-names');
        //    }
        //    this.savedQueryNames.selectpicker('refresh');
        //    if (queryName) {
        //        this.savedQueryNames.selectpicker('val', queryName);
        //    }
        //});

        this.on('afterCreateRuleFilters', function(e, rule) {
            rule.$el.find('.rule-filter-container select').removeClass('form-control').selectpicker(options);
            rule.$el.find('.rule-operator-container, .rule-value-container').empty();
        });

        this.on('afterCreateRuleOperators', function(e, rule) {
            if (rule.filter.operators && rule.filter.operators === "EQUALS") {
                rule.$el.find('.rule-operator-container').hide();
                return;
            }
            rule.$el.find('.rule-operator-container').show().find('select').removeClass('form-control').selectpicker(options);
        });

        // update selectpicker on change
        this.on('afterUpdateRuleEntity', function(e, rule) {
            var $filter = rule.$el.find('.rule-filter-container select');
            rule.$el.find('.rule-entity-container select').selectpicker('render');
            // Auto select if only one option and trigger change / update
            if ($filter.children().length === 2) {
                $filter.children().last().prop('selected', true);
                $filter.selectpicker('render');
                $filter.trigger('change');
            }
        });

        this.on('afterUpdateRuleFilter', function(e, rule) {
            rule.$el.find('.rule-filter-container select').selectpicker('render');
        });

        this.on('afterUpdateRuleOperator', function(e, rule) {
            rule.$el.find('.rule-operator-container select').selectpicker('render');
        });
    }, {
        container: 'body',
        style: 'btn-inverse btn-xs',
        width: 'auto',
        showIcon: false
    });

    /*!
     * jQuery QueryBuilder Bootstrap Tooltip errors
     * Applies Bootstrap Tooltips on validation error messages.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

    QueryBuilder.define('bt-tooltip-errors', function(options) {
        if (!$.fn.tooltip || !$.fn.tooltip.Constructor || !$.fn.tooltip.Constructor.prototype.fixTitle) {
            error('Bootstrap Tooltip is required to use "bt-tooltip-errors" plugin. Get it here: http://getbootstrap.com');
        }

        var self = this;

        // add BT Tooltip data
        this.on('getRuleTemplate.filter', function(h) {
            h.value = h.value.replace('class="error-container"', 'class="error-container" data-toggle="tooltip"');
        });

        this.on('getGroupTemplate.filter', function(h) {
            h.value = h.value.replace('class="error-container"', 'class="error-container" data-toggle="tooltip"');
        });

        // init/refresh tooltip when title changes
        this.model.on('update', function(e, node, field) {
            if (field == 'error' && self.settings.display_errors) {
                node.$el.find('.error-container').eq(0)
                    .tooltip(options)
                    .tooltip('hide')
                    .tooltip('fixTitle');
            }
        });
    }, {
        placement: 'right'
    });

    /*!
     * jQuery QueryBuilder Filter Description
     * Provides three ways to display a description about a filter: inline, Bootsrap Popover or Bootbox.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

    QueryBuilder.define('filter-description', function(options) {
        /**
         * INLINE
         */
        if (options.mode === 'inline') {
            this.on('afterUpdateRuleFilter', function(e, rule) {
                var $p = rule.$el.find('p.filter-description');

                if (!rule.filter || !rule.filter.description) {
                    $p.hide();
                }
                else {
                    if ($p.length === 0) {
                        $p = $('<p class="filter-description"></p>');
                        $p.appendTo(rule.$el);
                    }
                    else {
                        $p.show();
                    }

                    $p.html('<i class="' + options.icon + '"></i> ' + rule.filter.description);
                }
            });
        }
        /**
         * POPOVER
         */
        else if (options.mode === 'popover') {
            if (!$.fn.popover || !$.fn.popover.Constructor || !$.fn.popover.Constructor.prototype.fixTitle) {
                error('Bootstrap Popover is required to use "filter-description" plugin. Get it here: http://getbootstrap.com');
            }

            this.on('afterUpdateRuleFilter', function(e, rule) {
                var $b = rule.$el.find('button.filter-description');

                if (!rule.filter || !rule.filter.description) {
                    $b.hide();

                    if ($b.data('bs.popover')) {
                        $b.popover('hide');
                    }
                }
                else {
                    if ($b.length === 0) {
                        $b = $('<button type="button" class="btn btn-xs btn-info filter-description" data-toggle="popover"><i class="' + options.icon + '"></i></button>');
                        $b.prependTo(rule.$el.find('.rule-actions'));

                        $b.popover({
                            placement: 'left',
                            container: 'body',
                            html: true
                        });

                        $b.on('mouseout', function() {
                            $b.popover('hide');
                        });
                    }
                    else {
                        $b.show();
                    }

                    $b.data('bs.popover').options.content = rule.filter.description;

                    if ($b.attr('aria-describedby')) {
                        $b.popover('show');
                    }
                }
            });
        }
        /**
         * BOOTBOX
         */
        else if (options.mode === 'bootbox') {
            if (!('bootbox' in window)) {
                error('Bootbox is required to use "filter-description" plugin. Get it here: http://bootboxjs.com');
            }

            this.on('afterUpdateRuleFilter', function(e, rule) {
                var $b = rule.$el.find('button.filter-description');

                if (!rule.filter || !rule.filter.description) {
                    $b.hide();
                }
                else {
                    if ($b.length === 0) {
                        $b = $('<button type="button" class="btn btn-xs btn-info filter-description" data-toggle="bootbox"><i class="' + options.icon + '"></i></button>');
                        $b.prependTo(rule.$el.find('.rule-actions'));

                        $b.on('click', function() {
                            bootbox.alert($b.data('description'));
                        });
                    }

                    $b.data('description', rule.filter.description);
                }
            });
        }
    }, {
        icon: 'glyphicon glyphicon-info-sign',
        mode: 'popover'
    });

    /*!
     * jQuery QueryBuilder Invert
     * Allows to invert a rule operator, a group condition or the entire builder.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

    QueryBuilder.defaults({
        operatorOpposites: {
            'equal':            'not_equal',
            'not_equal':        'equal',
            'in':               'not_in',
            'not_in':           'in',
            'less':             'greater_or_equal',
            'less_or_equal':    'greater',
            'greater':          'less_or_equal',
            'greater_or_equal': 'less',
            'between':          'not_between',
            'not_between':      'between',
            'begins_with':      'not_begins_with',
            'not_begins_with':  'begins_with',
            'contains':         'not_contains',
            'not_contains':     'contains',
            'ends_with':        'not_ends_with',
            'not_ends_with':    'ends_with',
            'is_empty':         'is_not_empty',
            'is_not_empty':     'is_empty',
            'is_null':          'is_not_null',
            'is_not_null':      'is_null'
        },

        conditionOpposites: {
            'AND': 'OR',
            'OR': 'AND'
        }
    });

    QueryBuilder.define('invert', function(options) {
        var that = this;

        /**
         * Bind events
         */
        this.on('afterInit', function() {
            that.$el.on('click.queryBuilder', '[data-invert=group]', function() {
                var $group = $(this).closest('.rules-group-container');
                that.invert(Model($group), options);
            });

            if (options.display_rules_button && options.invert_rules) {
                that.$el.on('click.queryBuilder', '[data-invert=rule]', function() {
                    var $rule = $(this).closest('.rule-container');
                    that.invert(Model($rule), options);
                });
            }
        });

        /**
         * Modify templates
         */
        this.on('getGroupTemplate.filter', function(h, level) {
            var $h = $(h.value);
            $h.find('.group-conditions').after('<button type="button" class="btn btn-xs btn-default" data-invert="group"><i class="' + options.icon + '"></i> '+ that.lang.invert +'</button>');
            h.value = $h.prop('outerHTML');
        });

        if (options.display_rules_button && options.invert_rules) {
            this.on('getRuleTemplate.filter', function(h) {
                var $h = $(h.value);
                $h.find('.rule-actions').prepend('<button type="button" class="btn btn-xs btn-default" data-invert="rule"><i class="' + options.icon + '"></i> '+ that.lang.invert +'</button>');
                h.value = $h.prop('outerHTML');
            });
        }
    }, {
        icon: 'glyphicon glyphicon-random',
        recursive: true,
        invert_rules: true,
        display_rules_button: false,
        silent_fail: false
    });

    QueryBuilder.extend({
        /**
         * Invert a Group, a Rule or the whole builder
         * @param {Node,optional}
         * @param {object,optional}
         */
        invert: function(node, options) {
            if (!(node instanceof Node)) {
                if (!this.model.root) return;
                options = node;
                node = this.model.root;
            }

            if (typeof options != 'object') options = {};
            if (options.recursive === undefined) options.recursive = true;
            if (options.invert_rules === undefined) options.invert_rules = true;
            if (options.silent_fail === undefined) options.silent_fail = false;

            if (node instanceof Group) {
                if (this.settings.conditionOpposites[node.condition]) {
                    node.condition = this.settings.conditionOpposites[node.condition];
                }
                else if (!options.silent_fail) {
                    error('Unknown inverse of condition "{0}"', node.condition);
                }

                if (options.recursive) {
                    node.each(function(rule) {
                        if (options.invert_rules) {
                            this.invert(rule, options);
                        }
                    }, function(group) {
                        this.invert(group, options);
                    }, this);
                }
            }
            else if (node instanceof Rule) {
                if (node.operator) {
                    if (this.settings.operatorOpposites[node.operator.type]) {
                        node.operator = this.getOperatorByType(this.settings.operatorOpposites[node.operator.type]);
                    }
                    else  if (!options.silent_fail){
                        error('Unknown inverse of operator "{0}"', node.operator.type);
                    }
                }
            }
        }
    });

    /*!
     * jQuery QueryBuilder MongoDB Support
     * Allows to export rules as a MongoDB find object as well as populating the builder from a MongoDB object.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

// DEFAULT CONFIG
// ===============================
    QueryBuilder.defaults({
        mongoOperators: {
            equal:            function(v){ return v[0]; },
            not_equal:        function(v){ return {'$ne': v[0]}; },
            in:               function(v){ return {'$in': v}; },
            not_in:           function(v){ return {'$nin': v}; },
            less:             function(v){ return {'$lt': v[0]}; },
            less_or_equal:    function(v){ return {'$lte': v[0]}; },
            greater:          function(v){ return {'$gt': v[0]}; },
            greater_or_equal: function(v){ return {'$gte': v[0]}; },
            between:          function(v){ return {'$gte': v[0], '$lte': v[1]}; },
            not_between:      function(v){ return {'$lt': v[0], '$gt': v[1]}; },
            begins_with:      function(v){ return {'$regex': '^' + escapeRegExp(v[0])}; },
            not_begins_with:  function(v){ return {'$regex': '^(?!' + escapeRegExp(v[0]) + ')'}; },
            contains:         function(v){ return {'$regex': escapeRegExp(v[0])}; },
            not_contains:     function(v){ return {'$regex': '^((?!' + escapeRegExp(v[0]) + ').)*$', '$options': 's'}; },
            ends_with:        function(v){ return {'$regex': escapeRegExp(v[0]) + '$'}; },
            not_ends_with:    function(v){ return {'$regex': '(?<!' + escapeRegExp(v[0]) + ')$'}; },
            is_empty:         function(v){ return ''; },
            is_not_empty:     function(v){ return {'$ne': ''}; },
            is_null:          function(v){ return null; },
            is_not_null:      function(v){ return {'$ne': null}; }
        },

        mongoRuleOperators: {
            $ne: function(v) {
                v = v.$ne;
                return {
                    'val': v,
                    'op': v === null ? 'is_not_null' : (v === '' ? 'is_not_empty' : 'not_equal')
                };
            },
            eq: function(v) {
                return {
                    'val': v,
                    'op': v === null ? 'is_null' : (v === '' ? 'is_empty' : 'equal')
                };
            },
            $regex: function(v) {
                v = v.$regex;
                if (v.slice(0,4) == '^(?!' && v.slice(-1) == ')') {
                    return { 'val': v.slice(4,-1), 'op': 'not_begins_with' };
                }
                else if (v.slice(0,5) == '^((?!' && v.slice(-5) == ').)*$') {
                    return { 'val': v.slice(5,-5), 'op': 'not_contains' };
                }
                else if (v.slice(0,4) == '(?<!' && v.slice(-2) == ')$') {
                    return { 'val': v.slice(4,-2), 'op': 'not_ends_with' };
                }
                else if (v.slice(-1) == '$') {
                    return { 'val': v.slice(0,-1), 'op': 'ends_with' };
                }
                else if (v.slice(0,1) == '^') {
                    return { 'val': v.slice(1), 'op': 'begins_with' };
                }
                else {
                    return { 'val': v, 'op': 'contains' };
                }
            },
            between : function(v) { return {'val': [v.$gte, v.$lte], 'op': 'between'}; },
            not_between : function(v) { return {'val': [v.$lt, v.$gt], 'op': 'not_between'}; },
            $in :     function(v) { return {'val': v.$in, 'op': 'in'}; },
            $nin :    function(v) { return {'val': v.$nin, 'op': 'not_in'}; },
            $lt :     function(v) { return {'val': v.$lt, 'op': 'less'}; },
            $lte :    function(v) { return {'val': v.$lte, 'op': 'less_or_equal'}; },
            $gt :     function(v) { return {'val': v.$gt, 'op': 'greater'}; },
            $gte :    function(v) { return {'val': v.$gte, 'op': 'greater_or_equal'}; }
        }
    });


// PUBLIC METHODS
// ===============================
    /*!
     * jQuery QueryBuilder Sortable
     * Enables drag & drop sort of rules.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

    QueryBuilder.define('sortable', function(options) {
        /**
         * Init HTML5 drag and drop
         */
        this.on('afterInit', function(e) {
            // configure jQuery to use dataTransfer
            $.event.props.push('dataTransfer');

            var placeholder, src,
                self = e.builder;

            // only add "draggable" attribute when hovering drag handle
            // preventing text select bug in Firefox
            self.$el.on('mouseover', '.drag-handle', function() {
                self.$el.find('.rule-container, .rules-group-container').attr('draggable', true);
            });
            self.$el.on('mouseout', '.drag-handle', function() {
                self.$el.find('.rule-container, .rules-group-container').removeAttr('draggable');
            });

            // dragstart: create placeholder and hide current element
            self.$el.on('dragstart', '[draggable]', function(e) {
                e.stopPropagation();

                // notify drag and drop (only dummy text)
                e.dataTransfer.setData('text', 'drag');

                src = Model(e.target);

                // Chrome glitchs
                // - helper invisible if hidden immediately
                // - "dragend" is called immediately if we modify the DOM directly
                setTimeout(function() {
                    var ph = $('<div class="rule-placeholder">&nbsp;</div>');
                    ph.css('min-height', src.$el.height());

                    placeholder = src.parent.addRule(ph, src.getPos());

                    src.$el.hide();
                }, 0);
            });

            // dragenter: move the placeholder
            self.$el.on('dragenter', '[draggable]', function(e) {
                e.preventDefault();
                e.stopPropagation();

                if (placeholder) {
                    moveSortableToTarget(placeholder, $(e.target));
                }
            });

            // dragover: prevent glitches
            self.$el.on('dragover', '[draggable]', function(e) {
                e.preventDefault();
                e.stopPropagation();
            });

            // drop: move current element
            self.$el.on('drop', function(e) {
                e.preventDefault();
                e.stopPropagation();

                moveSortableToTarget(src, $(e.target));
            });

            // dragend: show current element and delete placeholder
            self.$el.on('dragend', '[draggable]', function(e) {
                e.preventDefault();
                e.stopPropagation();

                src.$el.show();
                placeholder.drop();

                src = placeholder = null;

                self.$el.find('.rule-container, .rules-group-container').removeAttr('draggable');
            });
        });

        /**
         * Remove drag handle from non-sortable rules
         */
        this.on('parseRuleFlags.filter', function(flags) {
            if (flags.value.no_sortable === undefined) {
                flags.value.no_sortable = options.default_no_sortable;
            }
        });

        this.on('afterApplyRuleFlags', function(e, rule) {
            if (rule.flags.no_sortable) {
                rule.$el.find('.drag-handle').remove();
            }
        });

        /**
         * Modify templates
         */
        this.on('getGroupTemplate.filter', function(h, level) {
            if (level>1) {
                var $h = $(h.value);
                $h.find('.group-conditions').after('<div class="drag-handle"><i class="' + options.icon + '"></i></div>');
                h.value = $h.prop('outerHTML');
            }
        });

        this.on('getRuleTemplate.filter', function(h) {
            var $h = $(h.value);
            $h.find('.rule-header').after('<div class="drag-handle"><i class="' + options.icon + '"></i></div>');
            h.value = $h.prop('outerHTML');
        });
    }, {
        default_no_sortable: false,
        icon: 'glyphicon glyphicon-sort'
    });

    /**
     * Move an element (placeholder or actual object) depending on active target
     * @param {Node}
     * @param {jQuery}
     */
    function moveSortableToTarget(element, target) {
        var parent;

        // on rule
        parent = target.closest('.rule-container');
        if (parent.length) {
            element.moveAfter(Model(parent));
            return;
        }

        // on group header
        parent = target.closest('.rules-group-header');
        if (parent.length) {
            parent = target.closest('.rules-group-container');
            element.moveAtBegin(Model(parent));
            return;
        }

        // on group
        parent = target.closest('.rules-group-container');
        if (parent.length) {
            element.moveAtEnd(Model(parent));
            return;
        }
    }

    /*!
     * jQuery QueryBuilder SQL Support
     * Allows to export rules as a SQL WHERE statement as well as populating the builder from an SQL query.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

// DEFAULT CONFIG
// ===============================
    QueryBuilder.defaults({
        /* operators for internal -> SQL conversion */
        sqlOperators: {
            equal:            { op: '= ?' },
            not_equal:        { op: '!= ?' },
            in:               { op: 'IN(?)',     sep: ', ' },
            not_in:           { op: 'NOT IN(?)', sep: ', ' },
            less:             { op: '< ?' },
            less_or_equal:    { op: '<= ?' },
            greater:          { op: '> ?' },
            greater_or_equal: { op: '>= ?' },
            between:          { op: 'BETWEEN ?',     sep: ' AND ' },
            not_between:      { op: 'NOT BETWEEN ?', sep: ' AND ' },
            begins_with:      { op: 'LIKE(?)',     fn: function(v){ return v+'%'; } },
            not_begins_with:  { op: 'NOT LIKE(?)', fn: function(v){ return v+'%'; } },
            contains:         { op: 'LIKE(?)',     fn: function(v){ return '%'+v+'%'; } },
            not_contains:     { op: 'NOT LIKE(?)', fn: function(v){ return '%'+v+'%'; } },
            ends_with:        { op: 'LIKE(?)',     fn: function(v){ return '%'+v; } },
            not_ends_with:    { op: 'NOT LIKE(?)', fn: function(v){ return '%'+v; } },
            is_empty:         { op: '= \'\'' },
            is_not_empty:     { op: '!= \'\'' },
            is_null:          { op: 'IS NULL' },
            is_not_null:      { op: 'IS NOT NULL' }
        },

        /* operators for SQL -> internal conversion */
        sqlRuleOperator: {
            '=': function(v) {
                return {
                    val: v,
                    op: v === '' ? 'is_empty' : 'equal'
                };
            },
            '!=': function(v) {
                return {
                    val: v,
                    op: v === '' ? 'is_not_empty' : 'not_equal'
                };
            },
            'LIKE': function(v) {
                if (v.slice(0,1)=='%' && v.slice(-1)=='%') {
                    return {
                        val: v.slice(1,-1),
                        op: 'contains'
                    };
                }
                else if (v.slice(0,1)=='%') {
                    return {
                        val: v.slice(1),
                        op: 'ends_with'
                    };
                }
                else if (v.slice(-1)=='%') {
                    return {
                        val: v.slice(0,-1),
                        op: 'begins_with'
                    };
                }
                else {
                    error('Invalid value for LIKE operator');
                }
            },
            'IN':       function(v) { return { val: v, op: 'in' }; },
            'NOT IN':   function(v) { return { val: v, op: 'not_in' }; },
            '<':        function(v) { return { val: v, op: 'less' }; },
            '<=':       function(v) { return { val: v, op: 'less_or_equal' }; },
            '>':        function(v) { return { val: v, op: 'greater' }; },
            '>=':       function(v) { return { val: v, op: 'greater_or_equal' }; },
            'BETWEEN':  function(v) { return { val: v, op: 'between' }; },
            'NOT BETWEEN': function(v) { return { val: v, op: 'not_between' }; },
            'IS':       function(v) {
                if (v !== null) {
                    error('Invalid value for IS operator');
                }
                return { val: null, op: 'is_null' };
            },
            'IS NOT':   function(v) {
                if (v !== null) {
                    error('Invalid value for IS operator');
                }
                return { val: null, op: 'is_not_null' };
            }
        },

        /* statements for internal -> SQL conversion */
        sqlStatements: {
            'question_mark': function() {
                var bind_params = [];
                return {
                    add: function(rule, value) {
                        bind_params.push(value);
                        return '?';
                    },
                    run: function() {
                        return bind_params;
                    }
                };
            },

            'numbered': function() {
                var bind_index = 0;
                var bind_params = [];
                return {
                    add: function(rule, value) {
                        bind_params.push(value);
                        bind_index++;
                        return '$' + bind_index;
                    },
                    run: function() {
                        return bind_params;
                    }
                };
            },

            'named': function() {
                var bind_index = {};
                var bind_params = {};
                return {
                    add: function(rule, value) {
                        if (!bind_index[rule.field]) bind_index[rule.field] = 0;
                        bind_index[rule.field]++;
                        var key = rule.field + '_' + bind_index[rule.field];
                        bind_params[key] = value;
                        return ':' + key;
                    },
                    run: function() {
                        return bind_params;
                    }
                };
            }
        },

        /* statements for SQL -> internal conversion */
        sqlRuleStatement: {
            'question_mark': function(values) {
                var i = 0;
                return {
                    get: function(v) {
                        if ($.isArray(v)) {
                            return v.map(function(v) {
                                return v=='?' ? values[i++] : v;
                            });
                        }
                        else {
                            return v=='?' ? values[i++] : v;
                        }
                    },
                    esc: function(sql) {
                        return sql.replace(/\?/g, '\'?\'');
                    }
                };
            },

            'numbered': function(values) {
                return {
                    get: function(v) {
                        if ($.isArray(v)) {
                            return v.map(function(v) {
                                return /^\$[0-9]+$/.test(v) ? values[v.slice(1)-1] : v;
                            });
                        }
                        else {
                            return /^\$[0-9]+$/.test(v) ? values[v.slice(1)-1] : v;
                        }
                    },
                    esc: function(sql) {
                        return sql.replace(/\$([0-9]+)/g, '\'$$$1\'');
                    }
                };
            },

            'named': function(values) {
                return {
                    get: function(v) {
                        if ($.isArray(v)) {
                            return v.map(function(v) {
                                return /^:/.test(v) ? values[v.slice(1)] : v;
                            });
                        }
                        else {
                            return /^:/.test(v) ? values[v.slice(1)] : v;
                        }
                    },
                    esc: function(sql) {
                        return sql.replace(new RegExp(':(' + Object.keys(values).join('|') + ')', 'g'), '\':$1\'');
                    }
                };
            }
        }
    });


// PUBLIC METHODS
// ===============================

    /*!
     * jQuery QueryBuilder Unique Filter
     * Allows to define some filters as "unique": ie which can be used for only one rule, globally or in the same group.
     * Copyright 2014-2015 Damien "Mistic" Sorel (http://www.strangeplanet.fr)
     */

    QueryBuilder.define('unique-filter', function() {
        this.status.used_filters = {};

        this.on('afterUpdateRuleFilter', this.updateDisabledFilters);
        this.on('afterDeleteRule', this.updateDisabledFilters);
        this.on('afterCreateRuleFilters', this.applyDisabledFilters);
    });

    QueryBuilder.extend({
        updateDisabledFilters: function(e) {
            var self = e.builder;
            self.status.used_filters = {};

            if (!self.model) {
                return;
            }

            // get used filters
            (function walk(group) {
                group.each(function(rule) {
                    if (rule.filter && rule.filter.unique) {
                        if (!self.status.used_filters[rule.filter.id]) {
                            self.status.used_filters[rule.filter.id] = [];
                        }
                        if (rule.filter.unique == 'group') {
                            self.status.used_filters[rule.filter.id].push(rule.parent);
                        }
                    }
                }, function(group) {
                    walk(group);
                });
            }(self.model.root));

            self.applyDisabledFilters(e);
        },

        applyDisabledFilters: function(e) {
            var self = e.builder;

            // re-enable everything
            self.$el.find('.rule-filter-container option').prop('disabled', false);

            // disable some
            $.each(self.status.used_filters, function(filterId, groups) {
                if (groups.length === 0) {
                    self.$el.find('.rule-filter-container option[value="' + filterId + '"]:not(:selected)').prop('disabled', true);
                }
                else {
                    groups.forEach(function(group) {
                        group.each(function(rule) {
                            rule.$el.find('.rule-filter-container option[value="' + filterId + '"]:not(:selected)').prop('disabled', true);
                        });
                    });
                }
            });

            // update Selectpicker
            if (self.settings.plugins && self.settings.plugins['bt-selectpicker']) {
                self.$el.find('.rule-filter-container select').selectpicker('render');
            }
        }
    });

    /*!
     * jQuery QueryBuilder 2.2.0
     * Locale: English (en)
     * Author: Damien "Mistic" Sorel, http://www.strangeplanet.fr
     * Licensed under MIT (http://opensource.org/licenses/MIT)
     */

    QueryBuilder.regional['en'] = {
        "__locale": "English (en)",
        "__author": "Damien \"Mistic\" Sorel, http://www.strangeplanet.fr",
        "add_rule": "Add condition",
        "add_group": "Add group",
        "delete_rule": "Delete",
        "delete_group": "Delete",
        "conditions": {
            "AND": "AND",
            "OR": "OR"
        },
        "operators": {
            "EQUAL": "equal",
            "NOT_EQUAL": "not equal",
            "IN": "in",
            "NOT_IN": "not in",
            "LESS": "less",
            "LESS_OR_EQUAL": "less or equal",
            "GREATER": "greater",
            "GREATER_OR_EQUAL": "greater or equal",
            "BETWEEN": "between",
            "NOT_BETWEEN": "not between",
            "BEGINS_WITH": "begins with",
            "NOT_BEGINS_WITH": "doesn't begin with",
            "CONTAINS": "contains",
            "NOT_CONTAINS": "doesn't contain",
            "ENDS_WITH": "ends with",
            "NOT_ENDS_WITH": "doesn't end with",
            "IS_EMPTY": "is empty",
            "IS_NOT_EMPTY": "is not empty",
            "IS_NULL": "is null",
            "IS_NOT_NULL": "is not null"
            /* end DROOLS extension */
        },
        "errors": {
            "hits_summary": "Only a query can reference Hits",
            "no_filter": "No filter selected",
            "empty_group": "The group is empty",
            "radio_empty": "No value selected",
            "checkbox_empty": "No value selected",
            "select_empty": "No value selected",
            "string_empty": "Empty value",
            "string_exceed_min_length": "Must contain at least {0} characters",
            "string_exceed_max_length": "Must not contain more than {0} characters",
            "string_invalid_format": "Invalid format ({0})",
            "number_nan": "Not a number",
            "number_not_integer": "Not an integer",
            "number_not_double": "Not a real number",
            "number_exceed_min": "Must be greater than {0}",
            "number_exceed_max": "Must be lower than {0}",
            "number_wrong_step": "Must be a multiple of {0}",
            "datetime_empty": "Empty value",
            "datetime_invalid": "Invalid date format ({0})",
            "datetime_exceed_min": "Must be after {0}",
            "datetime_exceed_max": "Must be before {0}",
            "boolean_not_valid": "Not a boolean",
            "operator_not_multiple": "Operator {0} cannot accept multiple values"
        },
        "invert": "Invert"
    };

    QueryBuilder.defaults({ lang_code: 'en' });
}));
