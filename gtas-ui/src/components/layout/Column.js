import React from 'react';

const Column = (props => {
  let width = 'col-' + props.width || 6;
  let className = width + " align-self-center";

  return (
    <div className={className}>
      {props.children}
    </div>
  )
});

export default Column;
