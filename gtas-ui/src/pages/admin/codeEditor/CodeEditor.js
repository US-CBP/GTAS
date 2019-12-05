import React from "react";
import Table from "../../../components/table/Table";
import { employees } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";

const CodeEditor = ({ name }) => {
  const cb = function(result) {};

  return (
    <div className="container">
      <Title title={name}></Title>

      <div className="columns">
        <div className="top">
          <Table service={employees.get} id="foo" callback={cb}></Table>
        </div>
      </div>
    </div>
  );
};

export default CodeEditor;
