import React from "react";
import Table from "../../../components/table/Table";
import { errorlog } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";

const ErrorLog = ({ name }) => {
  const cb = function(result) {};

  const visibleCols = ["errorId", "errorCode", "errorDescription", "errorTimestamp"];

  return (
    <div className="container">
      <Title title={name}></Title>

      <div className="columns">
        <div className="top">
          <Table
            service={errorlog.get}
            id="foo"
            callback={cb}
            header={visibleCols}
          ></Table>
        </div>
      </div>
    </div>
  );
};

export default ErrorLog;
