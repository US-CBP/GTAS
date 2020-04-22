import React, { useState, useContext, useEffect } from "react";
import Table from "../../components/table/Table";
import { flights } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import LabelledSelectInput from "../../components/inputs/LabelledSelectInput/LabelledSelectInput";
import FilterForm from "../../components/filterForm/FilterForm";
import { store } from "../../appContext";
import { hasData } from "../../utils/utils";
import "react-datepicker/dist/react-datepicker.css";
import "./Flights.css";
import { Container, Row, Col } from "react-bootstrap";
import LabelledDateTimePickerStartEnd from "../../components/inputs/LabelledDateTimePickerStartEnd/LabelledDateTimePickerStartEnd";
import Main from "../../components/main/Main";
import SideNav from "../../components/sidenav/SideNav";
import { components } from "react-select";

const Flights = props => {
  const cb = () => {};
  const [data, setData] = useState([{}]);
  const globalState = useContext(store);

  const setDataWrapper = data => {
    const dataOrEmptyArray = data?.flights || [{}];
    setData(dataOrEmptyArray);
  };

  let sDate = new Date();
  let eDate = new Date();
  eDate.setDate(eDate.getDate() + 1);
  sDate.setHours(sDate.getHours() - 1);
  const [startDate, setStartData] = useState(sDate);
  const [endDate, setEndData] = useState(eDate);

  useEffect(() => {
    console.log(globalState); // debug statement to prove user loaded.
    let user = JSON.parse(localStorage.getItem("user"));
    console.log(user); // debug statement to prove user was loaded in local storage.
  }, [globalState, endDate, startDate]);

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
    <>
      <SideNav>
        <Col>
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
      </SideNav>
      <Main>
        <Title title="Flights" uri={props.uri} />
        <Table data={data} key={data} id="Flights" header={Headers} callback={cb} />
      </Main>
    </>
  );
};

export default Flights;
