import React from "react";

export default function Loading() {
  return (
    <div className="col-6 align-self-center offset-5">
      <i className="fa fa-spinner fa-pulse fa-3x fa-fw"></i>
      <span className="sr-only">Loading...</span>
    </div>
  );
}
