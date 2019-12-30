import React, { useState, useContext, useEffect } from "react";
import Table from "../../components/table/Table";
import { flights } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";
import { store } from '../../appContext';
import {hasData} from "../../utils/text";
import "react-datepicker/dist/react-datepicker.css";
import LabelledDateTimePicker from "../../components/Inputs/LabelledDateTimePicker/LabelledDateTimePicker";
import "./Flights.css";
import { Nav, Container, Row, Col } from "react-bootstrap"
import LabelledSelectInput from "../../components/Inputs/LabelledSelectInput/LabelledSelectInput";


const Flights = props => {
  const cb = () => {};
  const [data, setData] = useState({flights: []});

  const globalState = useContext(store);

  let startDate = new Date();
  let endDate = new Date();
  endDate.setDate(endDate.getDate() + 1);
  startDate.setHours(startDate.getHours()-1);

  useEffect(() => {
    console.log(globalState); // debug statement to prove user loaded.
    let user = JSON.parse(localStorage.getItem("user"));
    console.log(user); // debug statement to prove user was loaded in local storage.
  }, [globalState]);

  const parameterAdapter = (fields) => {
    let paramObject = {pageSize: 10, pageNumber:1};
    const fieldNames = Object.keys(fields);
    fieldNames.forEach(name => {
      if (hasData(fields[name])) {
        paramObject[name] = fields[name];
      }
    });
    return "?request=" + encodeURIComponent(JSON.stringify(paramObject));
  };

  return (
    <Container fluid>
      <Row>
        <Col lg="2" md="3" sm="3">
          <div className="flight-filter-nav">
            <FilterForm
              service={flights}
              title="Filter"
              callback={setData}
              paramAdapter={parameterAdapter}
            >
              <hr />
              <LabelledSelectInput/>
              <LabelledInput
                datafield
                labelText="Destination Airport"
                inputType="text"
                name="destination"
                callback={cb}
                alt="nothing"
              />
              <LabelledInput
                datafield="flightNumber"
                labelText="Flight ID"
                inputType="number"
                name="flightId"
                callback={cb}
                alt="nothing"
              />
              <LabelledInput
                datafield="direction"
                labelText="Direction"
                inputType="text"
                name="direction"
                callback={cb}
                alt="nothing"
              />
              <LabelledDateTimePicker
                  datafield="etaStart"
                  name="etaStart"
                  alt="Start Date"
                  labelText="Start Date and Time"
                  inputType="dateTime"
                  dateFormat="yyyy-MM-dd h:mm aa"
                  callback={cb}
                  showTimeSelect
                  showYearDropdown
                  inputVal={startDate}
                  startDate={startDate}
                  endDate={endDate}
                  dateRange={{
                    'position': 'start'
                  }}
              />
              <LabelledDateTimePicker
                  datafield="etaEnd"
                  name="etaEnd"
                  alt="End Date"
                  labelText="End Date and Time"
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
                    'position': 'end'
                  }}
              />
            </FilterForm>
          </div>
        </Col>

        <Col lg="10" md="9" sm="9" className="flight-body">
          <Title title="Flights" uri={props.uri} />
          <div className="flight-body-box">
            <Link to="../flightpax">Flight Passengers</Link>
            <div className="card events-card">
              <Table
                data={data.flights}
                id="Flights"
                callback={cb}
                key={data}
                ignoredFields={["countDown"]}
              />
            </div>
          </div>
        </Col >
      </Row>
    </Container>
  );
};

export default Flights;
