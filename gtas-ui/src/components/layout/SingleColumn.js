import React from 'react';

const SingleColumn = (props => {
  return (
       <div className="FormContainer d-flex align-items-center justify-content-center flex-column">
        {props.children}
       </div>
      )
});

export default SingleColumn;
