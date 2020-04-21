import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import ErrorBoundary from "../errorBoundary/ErrorBoundary";
import { asArray, hasData, getParamList } from "../../../utils/utils";
import { Button, ButtonToolbar, Form as RBForm } from "react-bootstrap";
import Title from "../title/Title";
import { useQuery } from "react-query";

import "./FilterForm.css";

/**
 * **Generic filter form used to fetch data for use in another component.**
 */

const FilterForm = props => {
  const [formkey, setFormKey] = useState(1);
  const [fields, setFields] = useState([]);
  const [fieldMap, setFieldMap] = useState([]);
  const [datafieldNames, setDatafieldNames] = useState([]);
  const [getSuccess, setGetSuccess] = useState("");
  const [kids, setKids] = useState([]);
  const [params, setParams] = useState("");
  const [isPolling, setIsPolling] = useState("");
  let latestParams = "";

  const onReset = () => {
    let key = formkey;
    let fields = [];

    datafieldNames.forEach(function(name) {
      fields[name] = "";
    });

    setFormKey(key + 1);
    setFields(fields);
  };

  const fetchData = (newParams, retainState = true) => {
    const p = newParams || params;

    const controller = new AbortController();
    const signal = controller.signal;

    const promise = props.service(p).then(res => {
      props.callback(res, retainState);
    });

    promise.cancel = controller.abort;
    return promise;
  };

  // TODO - default to off and pull interval from props
  const { data } = useQuery(isPolling, fetchData, {
    refetchInterval: 60000
    // manual: true,
    // initialData: []
  });

  useEffect(() => {
    setIsPolling(true);
    return () => {
      setIsPolling(false);
    };
  }, []);

  const onChange = ev => {
    const componentname = ev.name;
    const value = ev.value;

    let newfields = fields;
    let datafieldname = fieldMap[componentname];
    newfields[datafieldname] = value;

    setFields(newfields);
  };

  const onFormSubmit = e => {
    e.preventDefault();
    // pass the new val directly as they won't be updated in state
    // before fetchData references it. Shd replace this with useeffect prob.
    const newParams = calcParams();
    fetchData(newParams, false);
  };

  const calcParams = () => {
    const cb = props.paramCallback;
    latestParams = getParamList(fields);

    if (hasData(cb)) {
      latestParams = cb(latestParams);
    }
    setParams(latestParams);

    return latestParams;
  };

  // bind children containing form data (datafield prop) to the ev handler and state
  const bindChildren = populatedFields => {
    let boundChildren = asArray(props.children).map((child, idx) => {
      if (!child.props.datafield) return child;

      let cleanprops = Object.assign({}, child.props);
      // intercept the callback so FilterForm is notified of input field changes.
      // Delete it here, and replace it in newchild (below) with a FilterForm handler.
      // We can also forward the event on to the original callback or to a parent
      // of FilterForm if needed.
      delete cleanprops.callback;

      return React.cloneElement(child, {
        key: idx,
        callback: onChange,
        ...cleanprops
      });
    });

    setKids(boundChildren);
    setFields(populatedFields);
    setGetSuccess(hasData(populatedFields)); // do we still need this??
  };

  useEffect(() => {
    bindChildren(fields);
  }, [fields]);

  useEffect(() => {
    let dfnames = datafieldNames;
    let fMap = fieldMap;

    asArray(props.children).forEach((child, idx) => {
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

        fMap[componentname] = fieldname;
        fields[fMap[componentname]] = child.props.inputVal;

        dfnames.push(fieldname);
      }
    });

    bindChildren(fields);

    setFieldMap(fMap);
    setDatafieldNames(dfnames);
    fetchData(undefined, false);
  }, []);

  return (
    <div>
      {props.title && <Title style="subtitle text-center" title={props.title}></Title>}
      <RBForm
        className={props.className}
        onSubmit={onFormSubmit}
        onReset={onReset}
        key={formkey}
      >
        <ErrorBoundary message="FilterForm children could not be rendered">
          {kids}
        </ErrorBoundary>
        <br></br>
        <ButtonToolbar className="container">
          <Button type="reset" variant="outline-dark m-1 text-white outline-dark-outline">
            {props.clearText || "Reset"}
          </Button>
          &nbsp;
          <Button type="submit" variant="ternary m-1">
            {props.submitText || "Search"}
          </Button>
        </ButtonToolbar>
      </RBForm>
    </div>
  );
};

FilterForm.propTypes = {
  title: PropTypes.string,
  submitText: PropTypes.string,
  clearText: PropTypes.string,
  service: PropTypes.func.isRequired,
  id: PropTypes.string,
  callback: PropTypes.func.isRequired,
  paramCallback: PropTypes.func,
  interval: PropTypes.number
};

export default FilterForm;
