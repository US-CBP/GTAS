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
  const [values, setValues] = useState({...props.options});

  const handleFieldChange = (name) => {
      let update = !values[name];
      setValues({ ...values, [name]: update });
  };
    let theCheckboxes = Object.keys(values).map(checkboxName => {
                        return <GroupCheckBox key={checkboxName}
                                              id={checkboxName}
                                              name={checkboxName}
                                              type={props.type}
                                              onChange={handleFieldChange}
                                              value={values[checkboxName]}
                                              checked={values[checkboxName]}
                        />;
            });
  return (
      <div>
        {theCheckboxes}
      </div>
  );
};

export default LabelledInputDisplayWrapper(CheckboxGroup);
