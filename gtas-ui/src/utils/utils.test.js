import HttpStatus from './HttpStatus';
import {titleCase, hasData, isObject} from './text';

describe('HttpStatus', () => {
  const expected = {
    code: '404',
    phrase: expect.any(String),
    description: expect.any(String),
    spec_href: expect.any(String),
  };

  test('returns a status code object', () => {
    expect(HttpStatus('404')).toMatchObject(expected);
  });
})

describe('titleCase', () => {
  test('converts a camelCase string to Title Case', () => {
    expect(titleCase('camelCaseString')).toEqual('Camel Case String');
  });
})

describe('hasData', () => {
  test('returns false for empty strings', () => {
    expect(hasData('  ')).toEqual(false);
  });

  test('returns false for empty arrays', () => {
    expect(hasData([])).toEqual(false);
  });

  test('returns false for empty objects', () => {
    expect(hasData({})).toEqual(false);
  });

  test('returns false for undefined', () => {
    expect(hasData(undefined)).toEqual(false);
  });

  test('returns true for false boolean', () => {
    expect(hasData(false)).toEqual(true);
  });

  test('returns true for numbers', () => {
    expect(hasData(12323)).toEqual(true);
  });

  test('returns true for strings', () => {
    expect(hasData('123123sdfsdf sdf   ')).toEqual(true);
  });
})

describe('isObject', () => {
  test('returns false for null', () => {
    expect(isObject(null)).toBe(false);
  });

  test('returns false for undefined', () => {
    expect(isObject(undefined)).toBe(false);
  });

  test('returns false for empty string', () => {
    expect(isObject('')).toBe(false);
  });

  test('returns false for array', () => {
    expect(isObject([])).toBe(false);
  });

  test('returns true for an object', () => {
    expect(isObject({one: 1, two:2})).toBe(true);
  });
});
