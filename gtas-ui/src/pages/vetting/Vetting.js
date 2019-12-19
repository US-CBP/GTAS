import React, { useState } from "react";
import Table from "../../components/table/Table";
import { cases } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";
import {hasData} from "../../utils/text";

const Vetting = props => {
  const onTableChange = () => {};
  const onTextChange = () => {};
  const [data, setData] = useState({cases: []});

  const parameterAdapter = (fields) => {
    let paramObject = {pageSize: 100, pageNumber:1};
    const fieldNames = Object.keys(fields);

    fieldNames.forEach(name => {
      if (hasData(fields[name])) {
        paramObject[name] = fields[name];
      }
    });
    return "?requestDto=" + encodeURIComponent(JSON.stringify(paramObject));
  };


  return (
    <div className="container">
      <Title title="Priority Vetting" uri={props.uri}/>

      <div className="columns">
        <div className="column is-3">
          <div className="box2">
            <aside className="menu">
              <FilterForm
                  service={cases}
                  title="Filter"
                  callback={setData}
                  paramAdapter={parameterAdapter}
              >
                <hr/>
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
                  datafield="etaStart"
                  labelText="Start Date"
                  inputType="text"
                  name="etaStart"
                  inputVal="2015-10-02T18:33:03.412Z"
                  callback={onTextChange}
                  alt="nothing"
                />
                <LabelledInput
                  datafield="etaEnd"
                  labelText="End Date"
                  inputVal="2025-10-02T18:33:03.412Z"
                  inputType="text"
                  name="etaEnd"
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
                  name="fullFlightId"
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
                data={data.cases}
                id="FlightDataTable"
                callback={onTableChange}
                header={["flightId", "hitNames", "status"]}
                ignoredFields = {["countDown", "priorityVettingListRuleTypes", "ruleCatFilter"]}
                key={data}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Vetting;
