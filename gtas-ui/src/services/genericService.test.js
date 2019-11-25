import GenericService from './genericService';

describe('GenericService works', () => {

  beforeEach(function () {
    global.fetch = jest.fn().mockImplementation(() => {
      var p = new Promise((resolve, reject) => {
        resolve({
          ok: true,
          Id: '123',
          json: function () {
            return { Id: '123' }
          }
        });
      });

      return p;
    });
  });

  it("get works", async function () {
    const response = await GenericService({ uri: 'http://localhost:3004/fakeendpoint', method: 'get' });
    expect(response.Id).toBe("123");
  });

  it("post works", async function () {
    const response = await GenericService({ uri: 'http://localhost:3004/fakeendpoint', method: 'post', body: { name: 'sam' } });
    expect(response.Id).toBe("123");
  });

})

describe('GenericService fails with status', () => {

  beforeEach(function () {
    global.fetch = jest.fn().mockImplementation(() => {
      var p = new Promise((resolve, reject) => {
        resolve({
          ok: false,
          status: '503'
        });
      });

      return p;
    });
  });

  it("get fails", async function () {
    let err;
    try {
      const response = await GenericService({ uri: 'http://localhost:3004/fakeendpoint', method: 'get' })
    } catch (e) {
      err = e;
    }
    expect(err).not.toBeNull();
  });

});

describe('GenericService fails with status', () => {

  beforeEach(function () {
    global.fetch = jest.fn().mockImplementation(() => {
      var p = new Promise((resolve, reject) => {
        resolve({
          ok: false
        });
      });

      return p;
    });
  });

  it("get fails", async function () {
    let err;
    try {
      const response = await GenericService({ uri: 'http://localhost:3004/fakeendpoint', method: 'get' })
    } catch (e) {
      err = e;
    }
    expect(err).not.toBeNull();
  });

});
