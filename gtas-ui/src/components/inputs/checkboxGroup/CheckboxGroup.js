/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

import React, {useState} from "react";
import GroupCheckBox from "./GroupCheckBox";
import LabelledInputDisplayWrapper from "../LabelledInputDecorator";

const CheckboxGroup = props => {
  const [values, setValues] = useState([...props.inputVal]);

  const handleFieldChange = (id) => {
      const newValues = [...values];
      newValues[id] = {...newValues[id], "checked": !newValues[id].checked};
      setValues(newValues);
      const filterFormUpdate = {
          name: props.name,
          value: newValues
      };
      props.callback(filterFormUpdate);
  };
    let theCheckboxes = values.map((checkBox, index) => {
                        return <GroupCheckBox key={index}
                                              id={index}
                                              name={checkBox.label}
                                              type={checkBox.type}
                                              onChange={handleFieldChange}
                                              value={checkBox.checked}
                                              checked={checkBox.checked}
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
