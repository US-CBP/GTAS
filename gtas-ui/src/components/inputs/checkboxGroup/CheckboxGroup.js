/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React, { useState } from "react";
import GroupCheckBox from "./GroupCheckBox";
import LabelledInputDisplayWrapper from "../LabelledInputDecorator";

const CheckboxGroup = props => {
  const [values, setValues] = useState({...props.inputVal});

  const handleFieldChange = (name) => {
      let update = !values[name];
      let newValues = {...values, [name]: update};
      setValues(newValues);
      const filterFormUpdate = {
          name: props.name,
          value: newValues
      };
      props.callback(filterFormUpdate);
  };
    let theCheckboxes = Object.keys(values).map((checkboxName, index) => {
                        return <GroupCheckBox key={checkboxName}
                                              id={checkboxName}
                                              name={props.optionNames[index]}
                                              type={props.type}
                                              onChange={handleFieldChange}
                                              value={values[checkboxName]}
                                              checked={values[checkboxName]}
                        />;
            });
    // useful debugging statement: <pre>{JSON.stringify(values, null, 2)}</pre>
  return (
      <div>
        {theCheckboxes}
      </div>
  );
};

export default LabelledInputDisplayWrapper(CheckboxGroup);
