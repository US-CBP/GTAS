import React from "react";
import ReactDOM from "react-dom";
// import {shallow} from 'enzyme';
import LabelledInput from "./LabelledInput";

const callback = () => {};
const fake = "faketext";
const div = document.createElement("div");

describe("LabelledInput", () => {
  it("renders without crashing", () => {
    const type = "text";

    ReactDOM.render(
      <LabelledInput
        id={fake}
        callback={callback}
        alt={fake}
        name={fake}
        inputType={type}
        lblText={fake}
      />,
      div
    );
    ReactDOM.unmountComponentAtNode(div);
  });

  it("renders a SELECT input", () => {
    const type = "text";
    //    const foo = shallow(<select id={fake} callback={callback} name={fake} inputType={type}></select>);

    // expect(ReactDOM.render(<LabelledInput id={fake} callback={callback} alt={fake} name={fake} inputType={type} lblText={fake}/>, div))
    // ReactDOM.unmountComponentAtNode(div);
  });

  it("renders a TEXT input", () => {
    const type = "text";

    ReactDOM.render(
      <LabelledInput
        id={fake}
        callback={callback}
        alt={fake}
        name={fake}
        inputType={type}
        lblText={fake}
      />,
      div
    );
    ReactDOM.unmountComponentAtNode(div);
  });

  it("renders a RADIO input", () => {
    const type = "radio";

    ReactDOM.render(
      <LabelledInput
        id={fake}
        callback={callback}
        alt={fake}
        name={fake}
        inputType={type}
        lblText={fake}
        options={["false", "true"]}
      />,
      div
    );
    ReactDOM.unmountComponentAtNode(div);
  });

  it("renders a FILE input", () => {
    const type = "file";

    ReactDOM.render(
      <LabelledInput
        id={fake}
        callback={callback}
        alt={fake}
        name={fake}
        inputType={type}
        lblText={fake}
        options={[".doc", ".pdf"]}
      />,
      div
    );
    ReactDOM.unmountComponentAtNode(div);
  });
});
