import React from "react";
import Table from "../../components/table/Table";
import { employees } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";

const Dashboard = () => {
  return (
    <div className="container">
      <Title title="Dashboard"></Title>

      <div className="columns">
        <div className="column">
          <div className="box2">
            <div className="top">
              <Table service={employees.get} id="foo"></Table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
