import React from "react";
import ReactDOM from "react-dom";
import Table from "./Table";

const div = document.createElement("div");
const callback = () => {};
const fake = "fake";

describe("Table", () => {
  it("renders without crashing", () => {
    ReactDOM.render(
      <Table uri={fake} id={fake} service={callback} callback={callback} />,
      div
    );
    ReactDOM.unmountComponentAtNode(div);
  });

  it("accepts a data array without crashing", () => {
    const data = [
      { id: 2, code: "sjeu", userName: "Yogi" },
      { id: 10, code: "sjeu", userName: "Yogi" },
      { id: 1, code: "sjeu", userName: "Yogi" }
    ];

    ReactDOM.render(<Table data={data} id={"id"} callback={callback} />, div);
    ReactDOM.unmountComponentAtNode(div);
  });

  it("returns an error if data, uri, and service params are all blank", () => {
    ReactDOM.render(<Table id={fake} callback={callback} />, div);

    // .expect()
    ReactDOM.unmountComponentAtNode(div);
  });
});
