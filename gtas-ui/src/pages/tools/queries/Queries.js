import React from "react";
import Table from "../../../components/table/Table";
import { errorlog } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import QueryBuilder from "../../../components/queryBuilder/QueryBuilder";

const Queries = props => {
  const cb = function(result) {};

  return (
    <div className="container">
      <Title title="Queries" uri={props.uri}></Title>

      <div className="columns">
        <div className="top">
          <br />
          <br />
          <QueryBuilder />
        </div>
      </div>
    </div>
  );
};

export default Queries;
