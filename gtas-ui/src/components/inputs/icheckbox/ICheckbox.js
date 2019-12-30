import React from "react";
import { titleCase } from "../../../utils/text";

/**
 * **DO NOT USE**
 * 
 * **Checkbox with useRef and useEffect. Will eventually be merged with Checkbox**
 */
const ICheckbox = React.forwardRef(({ indeterminate, ...rest }, ref) => {
  const defaultRef = React.useRef();
  const resolvedRef = ref || defaultRef;

  React.useEffect(() => {
    resolvedRef.current.indeterminate = indeterminate;
  }, [resolvedRef, indeterminate]);

  return (
    <>
      <input type="checkbox" ref={resolvedRef} {...rest} />
      {` ${titleCase(rest.text)}`}
    </>
  );
});

export default ICheckbox;
