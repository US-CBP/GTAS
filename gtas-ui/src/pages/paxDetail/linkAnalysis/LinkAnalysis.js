import React from "react";
// import Table from "../../../components/table/Table";
// import { company } from "../../../services/serviceWrapper";
import Graph from "../../../components/graph/Graph";
import ErrorBoundary from "../../../components/errorBoundary/ErrorBoundary";

const LinkAnalysis = () => {
  return (
    <div className="container">
      <div className="columns">
        <div className="top">
          <ErrorBoundary message="something went wrong here ...">
            <Graph></Graph>
          </ErrorBoundary>
        </div>
      </div>
    </div>
  );
};

export default LinkAnalysis;
