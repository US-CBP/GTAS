import React from "react";
import Table from "../../../components/table/Table";
import {} from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";

const APIS = () => {
  const cb = function(result) {};

  return (
    <div className="container">
      <Title title="APIS"></Title>

      <div className="columns">
        <div className="top">
          <Table id="foo" callback={cb}></Table>
        </div>
      </div>
    </div>
  );
};

export default APIS;
