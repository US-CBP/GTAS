# jQuery.extendext

[![Bower version](https://badge.fury.io/bo/jquery-extendext.svg)](http://badge.fury.io/bo/jquery-extendext)
[![Build Status](https://travis-ci.org/mistic100/jQuery.extendext.svg?branch=master)](https://travis-ci.org/mistic100/jQuery.extendext)

jQuery.extend with configurable behaviour for arrays.

## Isn't $.extend good enough ?

Well, it's actually pretty good, and is generally sufficient, but it merges arrays in a strange way depending of what you want. Example:

```js
var DEFAULTS = {
  operators: ['AND', 'OR', 'XOR']
};

var config = {
  operators: ['OR', 'XOR']
};

config = $.extend(true, {}, DEFAULTS, config);
```

When executing this code, one will expects to get `config.operators = ['OR', 'XOR']`, but instead you get `['OR', 'XOR', 'XOR]`, because `$.extend` merges arrays like objects as per spec.

Other deep merging utilities I found either have the same behaviour or perform both merge and append on array values ([nrf110/deepmerge](https://github.com/nrf110/deepmerge) for example).

## Usage

**jQuery.extendext.js** contains a new `$.extendext` function with the exact same behaviour as `$.extend` if not additional config is provided.

The difference is that it accepts a optional second string argument to specify how arrays should be merged.

```js
jQuery.extendext([deep ,][arrayMode ,] target, object1 [, objectN ] )
```

* **deep** _boolean_ — If true, the merge becomes recursive (aka. deep copy).
* **arrayMode** _string_ — Specify the arrays merge operation, either `replace`, `concat`, `extend` or `default`
* **target** _object_ — The object to extend. It will receive the new properties.
* **object1** _object_ — An object containing additional properties to merge in.
* **objectN** _object_ — Additional objects containing properties to merge in.

### "replace" mode

In this mode, every Array values in `target` is replaced by a copy of the same value found in `objectN`. The copy is recursive if `deep` is true.

```js
var DEFAULTS = {
  operators: ['AND', 'OR', 'XOR']
};

var config = {
  operators: ['OR', 'XOR']
};

config = $.extendext(true, 'replace', {}, DEFAULTS, config);

assert.deepEqual(config, {
  operators: ['OR', 'XOR']
}) // true;
```

### "concat" mode

In this mode, Arrays found in both `target` and `objectN` are always concatenated. If `deep` is true, a recursive copy of each value if concatenated instead of the value itself.

```js
var DEFAULTS = {
  operators: ['AND', 'OR', 'XOR']
};

var config = {
  operators: ['OR', 'XOR']
};

config = $.extendext(true, 'concat', {}, DEFAULTS, config);

assert.deepEqual(config, {
  operators: ['AND', 'OR', 'XOR', 'OR', 'XOR']
}) // true;
```

### "extend" mode

This is how [nrf110/deepmerge](https://github.com/nrf110/deepmerge) works. In this mode, Arrays values are treated a bit differently:

* If plain objects are found at the same position in both `target` and `objectN` they are merged recursively or not (depending on `deep` option).
* Otherwise, if the value in `objectN` is not found in `target`, it is pushed at the end of the array.

```js
var DEFAULTS = {
  operators: ['AND', 'OR', 'XOR']
};

var config = {
  operators: ['XOR', 'NAND']
};

config = $.extendext(true, 'extend', {}, DEFAULTS, config);

assert.deepEqual(config, {
  operators: ['AND', 'OR', 'XOR', 'NAND']
}) // true;
```

### "default" mode

Same as `$.extend`.

```js
var DEFAULTS = {
  operators: ['AND', 'OR', 'XOR']
};

var config = {
  operators: ['OR', 'XOR']
};

config = $.extendext(true, 'default', {}, DEFAULTS, config);

assert.deepEqual(config, {
  operators: ['OR', 'XOR', 'XOR']
}) // true;
```

## Tests

A QUnit test suite is provided in `tests` directory.

`$.extendext` is tested against core jQuery tests for `$.extend` and `nrf110/deepmerge` tests (with the difference that extendext, like extend, modifies the first argument where deepmerge does not touch it).