import React from 'react';

const DoubleColumn = (props => {
  return (
       <div className="FormContainer d-flex align-items-center justify-content-center">
        {props.children}
       </div>
      )
});

export default DoubleColumn;
