import React from "react";
import Table from "../../../components/table/Table";
import { files } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";

const FlightHistory = () => {
  return (
    <div className="container">
      <Title title="Flight History"></Title>

      <div className="columns">
        <div className="top">
          <Table service={files.get} id="foo"></Table>
        </div>
      </div>
    </div>
  );
};

export default FlightHistory;
