import React from "react";
import Table from "../../../components/table/Table";
import { hacks } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import Xl8 from "../../../components/xl8/Xl8";

const ManageUsers = ({ name }) => {
  const cb = function(result) {};

  return (
    <Xl8>
      <div className="container">
        <Title title={name}></Title>

        <div className="columns">
          <div className="column">
            <Table service={hacks.get} id="foo" callback={cb}></Table>
          </div>
        </div>
      </div>
    </Xl8>
  );
};

export default ManageUsers;
