module('deepmerge');

test('add keys in target that do not exist at the root', function () {
    var src = { key1: 'value1', key2: 'value2' }
    target = {}

    var res = jQuery.extend(true, 'extend', target, src)

    deepEqual(res, src)
})

test('merge existing simple keys in target at the roots', function () {
    var src = { key1: 'changed', key2: 'value2' }
    var target = { key1: 'value1', key3: 'value3' }

    var expected = {
        key1: 'changed',
        key2: 'value2',
        key3: 'value3'
    }

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
})

test('merge nested objects into target', function () {
    var src = {
        key1: {
            subkey1: 'changed',
            subkey3: 'added'
        }
    }
    var target = {
        key1: {
            subkey1: 'value1',
            subkey2: 'value2'
        }
    }

    var expected = {
        key1: {
            subkey1: 'changed',
            subkey2: 'value2',
            subkey3: 'added'
        }
    }

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
})

test('replace simple key with nested object in target', function () {
    var src = {
        key1: {
            subkey1: 'subvalue1',
            subkey2: 'subvalue2'
        }
    }
    var target = {
        key1: 'value1',
        key2: 'value2'
    }

    var expected = {
        key1: {
            subkey1: 'subvalue1',
            subkey2: 'subvalue2'
        },
        key2: 'value2'
    }

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
})

test('should add nested object in target', function() {
    var src = {
        "b": {
            "c": {}
        }
    }

    var target = {
        "a": {}
    }

    var expected = {
        "a": {},
        "b": {
            "c": {}
        }
    }

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
})

test('should replace object with simple key in target', function () {
    var src = { key1: 'value1' }
    var target = {
        key1: {
            subkey1: 'subvalue1',
            subkey2: 'subvalue2'
        },
        key2: 'value2'
    }

    var expected = { key1: 'value1', key2: 'value2' }

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
})

test('should work on simple array', function () {
    var src = ['one', 'three']
    var target = ['one', 'two']

    var expected = ['one', 'two', 'three']

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
    ok(Array.isArray(jQuery.extend(true, 'extend', target, src)))
})

test('should work on another simple array', function() {
    var target = ["a1","a2","c1","f1","p1"];
    var src = ["t1","s1","c2","r1","p2","p3"];

    var expected = ["a1", "a2", "c1", "f1", "p1", "t1", "s1", "c2", "r1", "p2", "p3"]

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
    ok(Array.isArray(jQuery.extend(true, 'extend', target, src)))
})

test('should work on array properties', function () {
    var src = {
        key1: ['one', 'three'],
        key2: ['four']
    }
    var target = {
        key1: ['one', 'two']
    }

    var expected = {
        key1: ['one', 'two', 'three'],
        key2: ['four']
    }

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
    ok(Array.isArray(jQuery.extend(true, 'extend', target, src).key1))
    ok(Array.isArray(jQuery.extend(true, 'extend', target, src).key2))
})

test('should work on array of objects', function () {
    var src = [
        { key1: ['one', 'three'], key2: ['one'] },
        { key3: ['five'] }
    ]
    var target = [
        { key1: ['one', 'two'] },
        { key3: ['four'] }
    ]

    var expected = [
        { key1: ['one', 'two', 'three'], key2: ['one'] },
        { key3: ['four', 'five'] }
    ]

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
    ok(Array.isArray(jQuery.extend(true, 'extend', target, src)), 'result should be an array')
    ok(Array.isArray(jQuery.extend(true, 'extend', target, src)[0].key1), 'subkey should be an array too')
})

test('should work on arrays of nested objects', function() {
    var target = [
        { key1: { subkey: 'one' }}
    ]

    var src = [
        { key1: { subkey: 'two' }},
        { key2: { subkey: 'three' }}
    ]

    var expected = [
        { key1: { subkey: 'two' }},
        { key2: { subkey: 'three' }}
    ]

    deepEqual(jQuery.extend(true, 'extend', target, src), expected)
})
