import React from "react";
import Table from "../../../components/table/Table";
import { auditlog } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";

const AuditLog = ({ name }) => {
  const cb = function(result) {};

  const visibleCols = ["actionType", "status", "message", "user", "timestamp"];

  return (
    <div className="container">
      <Title title={name}></Title>

      <div className="columns">
        <div className="top">
          <Table
            service={auditlog.get}
            id="foo"
            callback={cb}
            header={visibleCols}
          ></Table>
        </div>
      </div>
    </div>
  );
};

export default AuditLog;
