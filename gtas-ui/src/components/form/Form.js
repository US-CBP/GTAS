import React from "react";
import PropTypes from "prop-types";
import ErrorBoundary from "../errorBoundary/ErrorBoundary";
import { hasData, asArray } from "../../utils/text";
import {Badge, Button} from "react-bootstrap"

/**
 * **Generic form that can add a new record or fetch and edit an existing one.**
 */

// The quick solution is to require the child elements to include a "datafield" prop
// that we can use to identify the data-containing elements. There are probably other ways to
// separate the data elements from non-data elements automatically without the user having to mark the
// children, but using the "datafield" prop also lets the user define different names for the component
// and the data field it should save to ("name" vs "datafield" props).

class Form extends React.Component {
  constructor(props) {
    super(props);

    this.onFormSubmit = this.onFormSubmit.bind(this);
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
      kids: []
    };
  }

  componentDidMount() {
    this.fetchData();
  }

  fetchData() {
    let fields = [];
    if (this.isEdit()) {
      this.props.service.get(this.props.id).then(res => {
        if (hasData(res)) {
          console.log(res);
          let populatedFields = [];
          for (let field in this.state.fields) {
            populatedFields[field] = res[field];
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

    const service = this.props.service;

    let operation = null;
    switch (this.props.action) {
      case "":
      case "add":
        operation = service.post;
        break;
      case "auth":
        operation = service.authPost;
        break;
      case "edit":
        operation = service.put;
        break;
      default:
        throw new Error("Unsupported action on form. ");
    }
    let operationResult =  operation({ ...this.state.fields }, this.props.id);
    if (this.props.afterProcessed !== undefined) {
      this.props.afterProcessed(operationResult);
    }

    // handle confirmation here or pass through to parent??
    // could expose the method of confirmation in the page-container context so we can swap it out easily ???
    // then we can implement it as a page-level banner or popup, whatever. Try it?
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
        inputVal: populatedFields[this.state.fieldMap[child.props.name]] || "",
        ...cleanprops
      });


      return newchild;
    });

    this.setState({
      kids: boundChildren,
      fields: populatedFields,
      getSuccess: hasData(populatedFields)
    });
  }

  render() {
    const disabled = this.canSubmit() ? "" : "disabled";

    return (
      <div>
        <Badge>{this.props.title}</Badge>
        <form onSubmit={this.onFormSubmit}>
          <ErrorBoundary message="Form children could not be rendered">
            {this.state.kids}
          </ErrorBoundary>
          <div className="text-center pad-top-20" id="button-div">
            <Button
              className={`button block info fullwidth gradient-button ${disabled}`}
              type="submit"
            >
              {this.props.submitText || "Submit"}
            </Button>
          </div>
        </form>
      </div>
    );
  }
}

Form.propTypes = {
  title: PropTypes.string,
  submitText: PropTypes.string,
  service: PropTypes.any.isRequired,
  action: PropTypes.oneOf(["add", "edit", "auth", ""]),
  id: PropTypes.string,
  afterProcessed: PropTypes.func
};

export default Form;
