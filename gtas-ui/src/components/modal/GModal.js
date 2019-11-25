import React, { useEffect, useRef } from "react";
import { createPortal } from "react-dom";

const GModal = ({ children }) => {
  const elRef = useRef(null);

  if (!elRef.current) {
    const div = document.createElement("div");
    elRef.current = div;
  }

  useEffect(() => {
    const modalRoot = document.getElementById("modal");
    modalRoot.appendChild(elRef.current);

    return () => modalRoot.removeChild(elRef.current); // return fxn is the cleanup/onClose fxn!
  }, []); //empty array means run just once (no deps)

  return createPortal(<div>{children}</div>, elRef.current);
};

export default GModal;
