module('jQuery core');

test("jQuery.extend(Object, Object)", function() {
  expect(28);

  var empty, optionsWithLength, optionsWithDate, myKlass,
    customObject, optionsWithCustomObject, MyNumber, ret,
    nullUndef, target, recursive, obj,
    defaults, defaultsCopy, options1, options1Copy, options2, options2Copy, merged2,
    settings = { "xnumber1": 5, "xnumber2": 7, "xstring1": "peter", "xstring2": "pan" },
    options = { "xnumber2": 1, "xstring2": "x", "xxx": "newstring" },
    optionsCopy = { "xnumber2": 1, "xstring2": "x", "xxx": "newstring" },
    merged = { "xnumber1": 5, "xnumber2": 1, "xstring1": "peter", "xstring2": "x", "xxx": "newstring" },
    deep1 = { "foo": { "bar": true } },
    deep2 = { "foo": { "baz": true }, "foo2": document },
    deep2copy = { "foo": { "baz": true }, "foo2": document },
    deepmerged = { "foo": { "bar": true, "baz": true }, "foo2": document },
    arr = [1, 2, 3],
    nestedarray = { "arr": arr };

  jQuery.extend(settings, options);
  deepEqual( settings, merged, "Check if extended: settings must be extended" );
  deepEqual( options, optionsCopy, "Check if not modified: options must not be modified" );

  jQuery.extend(settings, null, options);
  deepEqual( settings, merged, "Check if extended: settings must be extended" );
  deepEqual( options, optionsCopy, "Check if not modified: options must not be modified" );

  jQuery.extend(true, deep1, deep2);
  deepEqual( deep1["foo"], deepmerged["foo"], "Check if foo: settings must be extended" );
  deepEqual( deep2["foo"], deep2copy["foo"], "Check if not deep2: options must not be modified" );
  equal( deep1["foo2"], document, "Make sure that a deep clone was not attempted on the document" );

  ok( jQuery.extend(true, {}, nestedarray)["arr"] !== arr, "Deep extend of object must clone child array" );

  // #5991
  ok( jQuery.isArray( jQuery.extend(true, { "arr": {} }, nestedarray)["arr"] ), "Cloned array have to be an Array" );
  ok( jQuery.isPlainObject( jQuery.extend(true, { "arr": arr }, { "arr": {} })["arr"] ), "Cloned object have to be an plain object" );

  empty = {};
  optionsWithLength = { "foo": { "length": -1 } };
  jQuery.extend(true, empty, optionsWithLength);
  deepEqual( empty["foo"], optionsWithLength["foo"], "The length property must copy correctly" );

  empty = {};
  optionsWithDate = { "foo": { "date": new Date() } };
  jQuery.extend(true, empty, optionsWithDate);
  deepEqual( empty["foo"], optionsWithDate["foo"], "Dates copy correctly" );

  /** @constructor */
  myKlass = function() {};
  customObject = new myKlass();
  optionsWithCustomObject = { "foo": { "date": customObject } };
  empty = {};
  jQuery.extend(true, empty, optionsWithCustomObject);
  ok( empty["foo"] && empty["foo"]["date"] === customObject, "Custom objects copy correctly (no methods)" );

  // Makes the class a little more realistic
  myKlass.prototype = { "someMethod": function(){} };
  empty = {};
  jQuery.extend(true, empty, optionsWithCustomObject);
  ok( empty["foo"] && empty["foo"]["date"] === customObject, "Custom objects copy correctly" );

  MyNumber = Number;

  ret = jQuery.extend(true, { "foo": 4 }, { "foo": new MyNumber(5) } );
  ok( parseInt(ret.foo, 10) === 5, "Wrapped numbers copy correctly" );

  nullUndef;
  nullUndef = jQuery.extend({}, options, { "xnumber2": null });
  ok( nullUndef["xnumber2"] === null, "Check to make sure null values are copied");

  nullUndef = jQuery.extend({}, options, { "xnumber2": undefined });
  ok( nullUndef["xnumber2"] === options["xnumber2"], "Check to make sure undefined values are not copied");

  nullUndef = jQuery.extend({}, options, { "xnumber0": null });
  ok( nullUndef["xnumber0"] === null, "Check to make sure null values are inserted");

  target = {};
  recursive = { foo:target, bar:5 };
  jQuery.extend(true, target, recursive);
  deepEqual( target, { bar:5 }, "Check to make sure a recursive obj doesn't go never-ending loop by not copying it over" );

  ret = jQuery.extend(true, { foo: [] }, { foo: [0] } ); // 1907
  equal( ret.foo.length, 1, "Check to make sure a value with coercion 'false' copies over when necessary to fix #1907" );

  ret = jQuery.extend(true, { foo: "1,2,3" }, { foo: [1, 2, 3] } );
  ok( typeof ret.foo !== "string", "Check to make sure values equal with coercion (but not actually equal) overwrite correctly" );

  ret = jQuery.extend(true, { foo:"bar" }, { foo:null } );
  ok( typeof ret.foo !== "undefined", "Make sure a null value doesn't crash with deep extend, for #1908" );

  obj = { foo:null };
  jQuery.extend(true, obj, { foo:"notnull" } );
  equal( obj.foo, "notnull", "Make sure a null value can be overwritten" );

  function func() {}
  jQuery.extend(func, { key: "value" } );
  equal( func.key, "value", "Verify a function can be extended" );

  defaults = { xnumber1: 5, xnumber2: 7, xstring1: "peter", xstring2: "pan" };
  defaultsCopy = { xnumber1: 5, xnumber2: 7, xstring1: "peter", xstring2: "pan" };
  options1 = { xnumber2: 1, xstring2: "x" };
  options1Copy = { xnumber2: 1, xstring2: "x" };
  options2 = { xstring2: "xx", xxx: "newstringx" };
  options2Copy = { xstring2: "xx", xxx: "newstringx" };
  merged2 = { xnumber1: 5, xnumber2: 1, xstring1: "peter", xstring2: "xx", xxx: "newstringx" };

  settings = jQuery.extend({}, defaults, options1, options2);
  deepEqual( settings, merged2, "Check if extended: settings must be extended" );
  deepEqual( defaults, defaultsCopy, "Check if not modified: options1 must not be modified" );
  deepEqual( options1, options1Copy, "Check if not modified: options1 must not be modified" );
  deepEqual( options2, options2Copy, "Check if not modified: options2 must not be modified" );
});

test("jQuery.extend(true,{},{a:[], o:{}}); deep copy with array, followed by object", function() {
  expect(2);

  var result, initial = {
    // This will make "copyIsArray" true
    array: [ 1, 2, 3, 4 ],
    // If "copyIsArray" doesn't get reset to false, the check
    // will evaluate true and enter the array copy block
    // instead of the object copy block. Since the ternary in the
    // "copyIsArray" block will will evaluate to false
    // (check if operating on an array with ), this will be
    // replaced by an empty array.
    object: {}
  };

  result = jQuery.extend( true, {}, initial );

  deepEqual( result, initial, "The [result] and [initial] have equal shape and values" );
  ok( !jQuery.isArray( result.object ), "result.object wasn't paved with an empty array" );
});
