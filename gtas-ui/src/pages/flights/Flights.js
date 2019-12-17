import React, { useState, useContext, useEffect } from "react";
import Table from "../../components/table/Table";
import { flights } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";
import { store } from '../../appContext';

const Flights = props => {
  const cb = () => {};

  const [data, setData] = useState([{}]);

  const globalState = useContext(store);

  useEffect(() => {
    console.log(globalState); // debug statement to prove user loaded.
  }, [globalState]);

  return (
    <div className="container">
      <Title title="Flights" uri={props.uri}></Title>
      <div className="columns">
        <div className="column is-3">
          <div className="box2">
            <aside className="menu">
              <FilterForm service={flights} title="Filter" callback={setData}>
                <hr></hr>
                <LabelledInput
                  datafield
                  labelText="Origin Airport"
                  inputType="text"
                  name="origin"
                  callback={cb}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Destination Airport"
                  inputType="text"
                  name="destination"
                  callback={cb}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Flight ID"
                  inputType="number"
                  name="flightId"
                  callback={cb}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Direction"
                  inputType="text"
                  name="direction"
                  callback={cb}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="Start Date"
                  inputType="text"
                  name="departure"
                  callback={cb}
                  alt="nothing"
                />
                <LabelledInput
                  datafield
                  labelText="End Date"
                  inputType="text"
                  name="arrival"
                  callback={cb}
                  alt="nothing"
                />
              </FilterForm>
            </aside>
          </div>
        </div>

        <div className="column">
          <div className="box2">
            <Link to="../flightpax">Flight Passengers</Link>
            <div className="card events-card">
              <Table data={data} id="Flights" callback={cb} key={data}></Table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Flights;
