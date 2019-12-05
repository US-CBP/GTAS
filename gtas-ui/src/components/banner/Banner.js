import React, { useState } from "react";
/**
 *
 * **This is a simple Notification Banner for displaying dismissable messages to the user.**
 */

//  * We are currently assuming this is a dummy display and that parent pages will not need to know when the banner is closed.
// If we decide the parent needs this info, convert this to a hook and handle the onClose in the parent.

const Banner = ({ id, styleName, text, defaultState }) => {
  const [state, setState] = useState(defaultState);

  if (!state) {
    return null;
  }

  const close = () => {
    setState(false);
  };

  return (
    <div id={id} className={`notification is-${styleName}`}>
      <button className="delete" onClick={close}></button>
      {text}
    </div>
  );
};

export default Banner;
