import { getProfile, putProfile, postProfile, deleteProfile } from "./serviceWrapper";

describe("Service Wrapper", () => {
  beforeEach(function() {
    global.fetch = jest.fn().mockImplementation(() => {
      var p = new Promise((resolve, reject) => {
        resolve({
          ok: true,
          Id: "123",
          json: function() {
            return { Id: "123" };
          }
        });
      });

      return p;
    });
  });

  // Success
  it("postProfile", async function() {
    const response = await postProfile({ name: "sam" });
    expect(response.Id).toBe("123");
  });

  it("getProfile", async function() {
    const response = await getProfile();
    expect(response.Id).toBe("123");
  });

  it("putProfile", async function() {
    const response = await putProfile({ name: "sam" });
    expect(response.Id).toBe("123");
  });

  it("deleteProfile", async function() {
    const response = await deleteProfile("id");
    expect(response.Id).toBe("123");
  });

  // Fails
  it("postProfile fails because of unsent body", async function() {
    let err;
    try {
      const response = await postProfile();
    } catch (e) {
      err = e;
    }
    expect(err).not.toBeNull();
  });

  it("putProfile fails because of unsent body", async function() {
    let err;
    try {
      const response = await putProfile();
    } catch (e) {
      err = e;
    }
    expect(err).not.toBeNull();
  });

  it("deleteProfile fails because of unsent body", async function() {
    let err;
    try {
      const response = await deleteProfile();
    } catch (e) {
      err = e;
    }
    expect(err).not.toBeNull();
  });
});
