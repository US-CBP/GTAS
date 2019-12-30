import React, { useState } from "react";
import Table from "../../components/table/Table";
import { cases } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
// import { Link } from "@reach/router";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";

const Vetting = props => {
  const onTableChange = () => {};
  const onTextChange = () => {};
  const [data, setData] = useState([]);

  return (
    <div className="container">
      <Title title="Priority Vetting" uri={props.uri}></Title>

      <div className="columns">
        <div className="column is-3">
          <div className="box2">
            <aside className="menu">
              <FilterForm service={cases} callback={setData}>
                <LabelledInput
                  datafield
                  labelText="Passenger Hit Status"
                  inputType="text"
                  name="hitStatus"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="My Rules Only"
                  inputType="text"
                  name="myRules"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Rule Category"
                  inputType="text"
                  name="ruleCat"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Rule Type"
                  inputType="text"
                  name="ruleType"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Start Date"
                  inputType="text"
                  name="direction"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="End Date"
                  inputType="text"
                  name="direction"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Passenger Last Name"
                  inputType="text"
                  name="paxLastName"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Full Flight ID"
                  inputType="text"
                  name="flightId"
                  callback={onTextChange}
                  alt="nothing"
                />
              </FilterForm>
            </aside>
          </div>
        </div>

        <div className="column">
          <div className="box2">
            <div className="top">
              <Table
                data={data}
                id="FlightDataTable"
                callback={onTableChange}
                header={[
                  { Accessor: "flightId", xid: "20" },
                  { Accessor: "hitNames", xid: "21" },
                  { Accessor: "status", xid: "22" }
                ]}
                key={data}
              ></Table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Vetting;
