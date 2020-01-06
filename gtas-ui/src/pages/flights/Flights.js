import React, { useState, useContext, useEffect } from "react";
import Table from "../../components/table/Table";
import { flights } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import LabelledSelectInput from "../../components/inputs/LabelledSelectInput/LabelledSelectInput";
import FilterForm from "../../components/filterForm/FilterForm";
import { store } from "../../appContext";
import { hasData } from "../../utils/text";
import "react-datepicker/dist/react-datepicker.css";
import LabelledDateTimePicker from "../../components/inputs/LabelledDateTimePicker/LabelledDateTimePicker";
import "./Flights.css";
import { Container, Row, Col } from "react-bootstrap";

const Flights = props => {
  const cb = () => {};
  const [data, setData] = useState([{}]);
  const globalState = useContext(store);

  const setDataWrapper = data => {
    const dataOrEmptyArray = data?.flights || [{}];
    setData(dataOrEmptyArray);
  };

  let startDate = new Date();
  let endDate = new Date();
  // let startDate = new Date(2018, 0, 0);
  // let endDate = new Date(2020, 0, 0);

  endDate.setDate(endDate.getDate() + 1);
  startDate.setHours(startDate.getHours() - 1);

  useEffect(() => {
    console.log(globalState); // debug statement to prove user loaded.
    let user = JSON.parse(localStorage.getItem("user"));
    console.log(user); // debug statement to prove user was loaded in local storage.
  }, [globalState]);

  const parameterAdapter = fields => {
    let paramObject = { pageSize: 20, pageNumber: 1 };
    const fieldNames = Object.keys(fields);
    fieldNames.forEach(name => {
      if (hasData(fields[name])) {
        paramObject[name] = fields[name];
      }
    });

    return "?request=" + encodeURIComponent(JSON.stringify(paramObject));
  };

  const Headers = [
    {
      Accessor: "passengerCount",
      Cell: ({ row }) => (
        <Link to={"../flightpax?flightId=" + row.original.id}>
          {row.original.passengerCount}
        </Link>
      )
    },
    { Accessor: "fullFlightNumber" },
    { Accessor: "origin" },
    { Accessor: "originCountry" },
    { Accessor: "destination" },
    { Accessor: "destinationCountry" },
    { Accessor: "direction" },
    { Accessor: "etaDate" },
    { Accessor: "etdDate" },
    { Accessor: "ruleHitCount" },
    { Accessor: "listHitCount" },
    { Accessor: "graphHitCount" }
  ];

  const options = [
    { value: "foo", label: "foo" },
    { value: "bar", label: "bar" },
    { value: "baz", label: "baz" }
  ];
  const directions = [
    { value: "I", label: "Inbound" },
    { value: "O", label: "Outbound" },
    { value: "A", label: "All" }
  ];
  return (
    <Container fluid>
      <Row flex>
        <Col lg="3" md="3" xs="12">
          <div className="filter-side-nav">
            <FilterForm
              service={flights.get}
              title="Filter"
              callback={setDataWrapper}
              paramAdapter={parameterAdapter}
            >
              <hr />
              <LabelledSelectInput
                name="originAirports"
                labelText="Origin Airports"
                datafield="originAirports"
                ReturnStringArray
                isMulti
                options={options}
              />
              <LabelledSelectInput
                datafield
                labelText="Destination Airports"
                inputType="text"
                name="destinationAirports"
                ReturnStringArray
                isMulti
                options={options}
              />
              <LabelledInput
                datafield="flightNumber"
                labelText="Flight Number"
                inputType="number"
                name="flightId"
                callback={cb}
                alt="nothing"
              />
              <LabelledSelectInput
                datafield
                defaultValue={{ value: "A", label: "All" }}
                labelText="Direction"
                name="direction"
                ReturnString
                options={directions}
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
                  position: "start"
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
                  position: "end"
                }}
              />
            </FilterForm>
          </div>
        </Col>

        <Col lg="9" md="9" xs="12">
          <Title title="Flights" uri={props.uri} />
            <Table data={data} key={data} id="Flights" header={Headers} callback={cb} />
        </Col>
      </Row>
    </Container>
  );
};

export default Flights;