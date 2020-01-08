import React, { useState } from "react";
import Table from "../../components/table/Table";
import { cases } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";
import { hasData } from "../../utils/text";
import { Col, Container } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import LabelledDateTimePickerStartEnd from "../../components/inputs/labelledDateTimePickerStartEnd/LabelledDateTimePickerStartEnd";
import CheckboxGroup from "../../components/inputs/checkboxGroup/CheckboxGroup";

const Vetting = props => {
  const onTableChange = () => {};
  const onTextChange = () => {};
  const cb = () => {};

  let sDate = new Date();
  let eDate = new Date();
  eDate.setDate(eDate.getDate() + 1);
  sDate.setHours(sDate.getHours() - 1);
  const [startDate, setStartData] = useState(sDate);
  const [endDate, setEndData] = useState(eDate);

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
    return "?requestDto=" + encodeURIComponent(JSON.stringify(paramObject));
  };

  let displayStatusCheckboxGroups = { NEW: true, REVIEWED: true, RE_OPENED: false };

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
            <CheckboxGroup
              datafield={displayStatusCheckboxGroups}
              inputVal={displayStatusCheckboxGroups}
              labelText="Passenger Hit Status"
              name="displayStatusCheckBoxes"
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
            <LabelledDateTimePickerStartEnd
              datafield={["etaStart", "etaEnd"]}
              name={["etaStart", "etaEnd"]}
              alt="Start/End Datepicker"
              inputType="dateTime"
              dateFormat="yyyy-MM-dd h:mm aa"
              callback={cb}
              showTimeSelect
              showYearDropdown
              inputVal={{ etaStart: startDate, etaEnd: endDate }}
              startDate={startDate}
              endDate={endDate}
              endMut={setEndData}
              startMut={setStartData}
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
