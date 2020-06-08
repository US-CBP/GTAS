import React from "react";
import PropTypes from "prop-types";
import ErrorBoundary from "../errorBoundary/ErrorBoundary";
import { hasData, asArray, isObject, alt } from "../../utils/utils";
import Title from "../title/Title";
import { Button, Form as RBForm, ButtonToolbar } from "react-bootstrap";
import { navigate } from "@reach/router";

import "./Form.css";

/**
 * **Generic form that can add a new record or fetch and edit an existing one.**
 */

class Form extends React.Component {
  constructor(props) {
    super(props);

    this.onFormSubmit = this.onFormSubmit.bind(this);
    this.onFormCancel = this.onFormCancel.bind(this);
    this.onChange = this.onChange.bind(this);

    let fields = [];
    let fieldMap = [];

    asArray(this.props.children).forEach((child, idx) => {
      const datafield = child.props.datafield;

      if (datafield) {
        const noname = `unnamedfield${idx}`;
        const componentname = child.props.name || noname;
        const fieldname = datafield === true ? componentname : datafield;

        // Either the name or datafield prop must contain a string
        if (fieldname === noname) {
          throw new Error(`The child collection contains a "datafield" element whose name is not defined in the 
          "name" or "datafield" props. Remove the "datafield" prop or define a name for the element.`);
        }

        fieldMap[componentname] = fieldname;
        fields[fieldMap[componentname]] = "";
      }
    });

    this.state = {
      fields: fields, // array of data fields and their current vals. This gets saved to the DB.
      fieldMap: fieldMap, // maps the names of the child components to the data fields.
      getSuccess: "",
      kids: [],
      formkey: 1
    };
  }

  componentDidMount() {
    this.fetchData();
  }

  fetchData() {
    let fields = [];
    if (this.isEdit()) {
      this.props.getService(this.props.recordId).then(res => {
        if (hasData(res)) {
          // If res is an object, let it pass
          // If its an array with 1 object item, use that item
          // otherwise, assume it's an invalid result. Use an empty object to set the form values.
          let singleRecord = isObject(res)
            ? res
            : Array.isArray(res) && res.length === 1 && isObject(res[0])
            ? res[0]
            : {};

          let populatedFields = [];
          for (let field in this.state.fields) {
            populatedFields[field] = singleRecord[field];
          }
          fields = populatedFields;
        }
        this.bindChildren(fields);
      });
      // .catch, throw error
    } else {
      this.bindChildren(fields);
    }
  }

  canSubmit() {
    const isedit = this.isEdit();
    return !isedit || (isedit && this.state.getSuccess === true);
  }

  isEdit() {
    return this.props.action === "edit";
  }

  onChange(ev) {
    const componentname = ev.name;
    const value = ev.value;

    let newfields = this.state.fields;
    let datafieldname = this.state.fieldMap[componentname];

    newfields[datafieldname] = value;

    this.setState({
      fields: newfields
    });
  }

  onFormSubmit(e) {
    e.preventDefault();

    let operation = this.props.submitService;

    if (operation === undefined) {
      return this.fetchData();
    }

    const params = this.props.recordId
      ? [this.props.recordId, this.state.fields]
      : [this.state.fields];

    operation(...params).then(res => {
      if (hasData(this.props.callback)) this.props.callback(alt(res));
    });
  }

  onFormCancel() {
    if (this.props.redirectTo !== undefined) navigate(this.props.redirectTo);
    else window.history.back();
  }

  // bind children containing form data to the ev handler and state
  bindChildren(populatedFields) {
    let boundChildren = asArray(this.props.children).map((child, idx) => {
      if (!child.props.datafield) return child;

      let cleanprops = Object.assign({}, child.props);
      delete cleanprops.callback;

      let newchild = React.cloneElement(child, {
        key: idx,
        callback: this.onChange,
        inputVal: populatedFields[this.state.fieldMap[child.props.name]],
        ...cleanprops
      });

      return newchild;
    });

    const newkey = this.state.formkey + 1;

    this.setState({
      kids: boundChildren,
      fields: populatedFields,
      formkey: newkey,
      getSuccess: hasData(populatedFields)
    });
  }

  render() {
    const showSubmit = this.props.action !== "readonly";
    const disabled = this.canSubmit() ? "" : "disabled";

    return (
      <div>
        {this.props.title && <Title title={this.props.title}></Title>}
        <RBForm onSubmit={this.onFormSubmit} key={this.state.formkey}>
          <ErrorBoundary message="Form children could not be rendered">
            {this.state.kids}
          </ErrorBoundary>
          <ButtonToolbar className="container">
            {this.props.cancellable && (
              <Button
                type="button"
                className="m-2 text-white outline-dark-outline"
                variant="outline-dark"
                onClick={this.onFormCancel}
              >
                {this.props.cancelText || "Cancel"}
              </Button>
            )}
            {showSubmit && (
              <Button
                className={`m-2 button block info fullwidth gradient-button ${disabled}`}
                type="submit"
              >
                {this.props.submitText || "Submit"}
              </Button>
            )}
          </ButtonToolbar>
        </RBForm>
      </div>
    );
  }
}

Form.propTypes = {
  title: PropTypes.string,
  submitText: PropTypes.string,
  cancellable: PropTypes.oneOf(["true", true, ""]),
  cancelText: PropTypes.string,
  redirectTo: PropTypes.string,
  getService: PropTypes.func,
  submitService: PropTypes.func,
  action: PropTypes.oneOf(["add", "edit", "readonly", ""]),
  recordId: PropTypes.string,
  callback: PropTypes.func.isRequired,
  paramCallback: PropTypes.func
};

export default Form;
