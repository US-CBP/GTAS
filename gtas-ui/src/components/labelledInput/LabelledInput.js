import React, { Component } from "react";
import PropTypes from "prop-types";
import CheckboxInput from "../Inputs/Checkbox";
import TextInput from "../Inputs/Text";
import FileInput from "../Inputs/File";
import SelectInput from "../Inputs/Select";
import { hasData } from "../../utils/text";
import {Row} from "react-bootstrap";

const textTypes = ["text", "number", "password", "email", "search", "tel"];
const optionTypes = ["radio", "checkbox"];
const selectType = "select";
const fileType = "file";
const REQUIRED = "required";

class LabelledInput extends Component {
  constructor(props) {
    super(props);
    this.onChange = this.onChange.bind(this);

    this.state = {
      isValid: true,
      labelText: props.labelText || "",
      inputVal: props.inputVal || "",
      options: props.options,
      placeholder: props.placeholder || "",
      required: props.required || "",
      visibleStyle: props.isVisible || ""
    };
  }

  componentDidMount() {
    //APB getInput here??
  }

  onChange(e) {
    const value = e.target.value;
    console.log(e.target);

    //update the local state
    this.setState({
      inputVal: value,
      selected: value,
      isValid: hasData(value) || this.props.required !== REQUIRED
    });
    //update the parent state
    this.props.callback(e.target);
  }

  //APB - REFACTOR
  getInputByType() {
    const type = this.props.inputType;

    // const inputStyle = this.state.isValid ? 'input' : 'input invalid';
    const inputStyle = "input";

    if (textTypes.includes(type)) {
      return (
        <TextInput
          className={inputStyle}
          alt={this.props.alt}
          name={this.props.name}
          inputType={this.props.inputType}
          inputVal={this.state.inputVal || ""}
          callback={this.onChange}
          required={this.state.required}
          placeholder={this.state.placeholder}
          readOnly={this.props.readOnly}
        />
      );
    }
    if (type === selectType) {
      console.log(this.props.inputVal);
      return (
        <SelectInput
          className={inputStyle}
          alt={this.props.alt}
          name={this.props.name}
          inputType={this.props.inputType}
          selected={this.props.inputVal || ""}
          inputVal={this.props.inputVal || ""}
          callback={this.onChange}
          required={this.state.required}
          placeholder={this.state.placeholder}
          options={this.state.options}
          readOnly={this.props.readOnly}
        />
      );
    }
    if (optionTypes.includes(type)) {
      return (
        <CheckboxInput
          className={inputStyle}
          name={this.props.name}
          inputType={this.props.inputType}
          selected={this.state.inputVal}
          options={this.state.options}
          callback={this.onChange}
          required={this.state.required}
          placeholder={this.state.placeholder}
          alt={this.props.alt}
        />
      );
    }
    if (type === fileType) {
      return (
        <FileInput
          className={inputStyle}
          name={this.props.name}
          inputType={this.props.inputType}
          selected={this.state.inputVal}
          options={this.state.options}
          callback={this.onChange}
          required={this.state.required}
          placeholder={this.state.placeholder}
          alt={this.props.alt}
        />
      );
    }

    return null;
  }

  render() {
    const input = this.getInputByType();

    return (
      <div className={`field ${this.state.visibleStyle}`}>
        <Row className="control">
          <label className="txtlabel">{this.state.labelText}</label>
        </Row>
          <Row>
          {input}
        </Row>
      </div>
    );
  }
}

LabelledInput.propTypes = {
  name: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  labelText: PropTypes.string.isRequired,
  inputType: PropTypes.oneOf([
    "text",
    "number",
    "password",
    "select",
    "radio",
    "checkbox",
    "email",
    "search",
    "tel",
    "file"
  ]).isRequired,
  callback: PropTypes.func.isRequired,
  inputVal: PropTypes.string,
  selected: PropTypes.array,
  options: PropTypes.array,
  placeholder: PropTypes.string,
  required: PropTypes.oneOf([REQUIRED, "", undefined]),
  isVisible: PropTypes.bool,
  readOnly: PropTypes.string
};

export default LabelledInput;
