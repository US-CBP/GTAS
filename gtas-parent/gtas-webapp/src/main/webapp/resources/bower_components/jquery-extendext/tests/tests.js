module('$.extend-enhanced');

test('New modes', function() {
  var a, b, o;

  a = {
    foo: { bar: 3 },
    array: [ { does: 'work', too: [ 1, 2, 3 ] } ]
  };
  b = {
    foo: { baz: 4 },
    quux: 5,
    array: [ { does: 'work', too: [ 4, 5, 6 ] }, { really: 'yes' } ]
  };
  o = {
    foo: { bar: 3, baz: 4 },
    array: [ { does: 'work', too: [ 1, 2, 3, 4, 5, 6 ] }, { really: 'yes' } ],
    quux: 5
  };

  deepEqual($.extendext(true, 'extend', a, b), o, 'extend');

  a = {
    foo: { bar: 3 },
    array: [1, 2, 4]
  };
  b = {
    foo: { baz: 4 },
    array: [1, 4, 5]
  };
  o = {
    foo: { bar: 3, baz: 4 },
    array: [1, 2, 4, 1, 4, 5]
  };

  deepEqual($.extendext(true, 'concat', a, b), o, 'concat');

  a = {
    foo: { bar: 3 },
    array: [1, 2, 4]
  };
  b = {
    foo: { baz: 4 },
    array: [4, 5]
  };
  o = {
    foo: { bar: 3, baz: 4 },
    array: [4, 5]
  };

  deepEqual($.extendext(true, 'replace', a, b), o, 'replace');
});
