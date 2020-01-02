import React, { useState } from "react";
import Table from "../../components/table/Table";
import { cases } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";
import { hasData } from "../../utils/text";
import LabelledDateTimePicker from "../../components/inputs/labelledDateTimePicker/LabelledDateTimePicker";
import { Col, Container } from "react-bootstrap";
import Row from "react-bootstrap/Row";

const Vetting = props => {
  const onTableChange = () => {};
  const onTextChange = () => {};
  const cb = () => {};

  let startDate = new Date();
  let endDate = new Date();
  endDate.setDate(endDate.getDate() + 1);
  startDate.setHours(startDate.getHours() - 1);

  const [data, setData] = useState([{}]);

  const setDataWrapper = data => {
    setData(data?.cases || []);
  };

  const parameterAdapter = fields => {
    let paramObject = { pageSize: 100, pageNumber: 1 };
    const fieldNames = Object.keys(fields);

    fieldNames.forEach(name => {
      if (hasData(fields[name])) {
        paramObject[name] = fields[name];
      }
    });
    // return "?requestDto=" + encodeURIComponent(JSON.stringify(paramObject));
    return paramObject;
  };

  return (
    <Container fluid>
      <Title title="Priority Vetting" uri={props.uri} />
      <Row>
        <Col lg="2" md="3" sm="3" className="box2">
          <FilterForm
            service={cases.get}
            title="Filter"
            callback={setDataWrapper}
            paramAdapter={parameterAdapter}
          >
            <hr />
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
            <LabelledDateTimePicker
              datafield="etaStart"
              name="etaStart"
              alt="Start Date ETA"
              labelText="Start Date ETA"
              inputType="dateTime"
              dateFormat="yyyy-MM-dd h:mm aa"
              callback={cb}
              showTimeSelect
              showYearDropdown
              inputVal={startDate}
              startDate={startDate}
              endDate={endDate}
              dateRange={{
                position: "start"
              }}
            />
            <LabelledDateTimePicker
              datafield="etaEnd"
              name="etaEnd"
              alt="Start Date"
              labelText="End Date ETD"
              inputType="dateTime"
              dateFormat="yyyy-MM-dd h:mm aa"
              callback={cb}
              showTimeSelect
              inputVal={endDate}
              startDate={startDate}
              endDate={endDate}
              showYearDropdown
              end
              dateRange={{
                position: "end"
              }}
            />
          </FilterForm>
        </Col>
        <Col lg="10" md="9" sm="9" className="flight-body">
          <Table
            data={data}
            id="FlightDataTable"
            callback={onTableChange}
            header={["flightId", "hitNames", "status"]}
            ignoredFields={["countDown", "priorityVettingListRuleTypes", "ruleCatFilter"]}
            key={data}
          />
        </Col>
      </Row>
    </Container>
  );
};

export default Vetting;
