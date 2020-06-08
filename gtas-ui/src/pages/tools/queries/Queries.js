import React from "react";
import Table from "../../../components/table/Table";
import { users } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import QueryBuilder from "../../../components/queryBuilder/QueryBuilder";

const Queries = props => {
  const cb = function(result) {};

  return (
    <div className="container">
      <Title title="Queries" uri={props.uri}></Title>

      <div className="columns">
        <div className="top">
          <Table service={users.get} id="foo" callback={cb}></Table>

          <br />
          <br />
          <QueryBuilder />
        </div>
      </div>
    </div>
  );
};

export default Queries;
