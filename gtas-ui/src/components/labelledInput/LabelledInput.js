import React, { Component } from "react";
import PropTypes from "prop-types";
import CheckboxInput from "../inputs/checkbox/Checkbox";
import TextInput from "../inputs/text/Text";
import TextareaInput from "../inputs/textarea/Textarea";
import FileInput from "../inputs/file/File";
import SelectInput from "../inputs/select/Select";
import { hasData } from "../../utils/utils";
import LabelledInputDisplayWrapper from "../inputs/LabelledInputDecorator";

const textTypes = ["text", "number", "password", "email", "search", "tel"];
const boolTypes = ["radio", "checkbox", "toggle"];
const selectType = "select";
const textareaType = "textarea";
const fileType = "file";
const REQUIRED = "required";

/**
 * **LabelledInput is contains elements and props required by filter form for non 3rd party inputs.**
 */
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

  componentDidMount() {}

  onChange(e) {
    const value = e.target.value;

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
    const inputStyle = `${type} ${this.props.className || ""}`;

    if (type === textareaType) {
      return (
        <TextareaInput
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
    if (boolTypes.includes(type)) {
      return (
        <React.Fragment>
          {this.props.labelText && <br />}
          <CheckboxInput
            className={inputStyle}
            label={this.props.label}
            name={this.props.name}
            inputType={this.props.inputType}
            inputVal={this.props.inputVal}
            callback={this.onChange}
            required={this.state.required}
            selected={this.props.selected}
            placeholder={this.state.placeholder}
            alt={this.props.alt}
          />
        </React.Fragment>
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
    return this.getInputByType();
  }
}

LabelledInput.propTypes = {
  name: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  labelText: PropTypes.string.isRequired,
  inputType: PropTypes.oneOf([
    "text",
    "textarea",
    "number",
    "password",
    "select",
    "radio",
    "checkbox",
    "toggle",
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

export default LabelledInputDisplayWrapper(LabelledInput);
