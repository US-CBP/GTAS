import React, { useState, Suspense } from "react";
import Table from "../../components/table/Table";
import { flights } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";
// import { useTranslation } from "react-i18next";
import Xl8 from "../../components/xl8/Xl8";

import Banner from "../../components/banner/Banner";

const Flights = props => {
  const cb = () => {};

  // const [t, i18n] = useTranslation();

  const [data, setData] = useState([{}]);
  const headers = [
    {
      Accessor: "passengers",
      Cell: ({ row }) => (
        <Link to={"../flightpax?flightId=" + row.original.id}>
          {row.original.passengers}
        </Link>
      )
    },
    { Accessor: "flightId" },
    { Accessor: "destination" },
    { Accessor: "direction" },
    { Accessor: "departure" },
    { Accessor: "arrival" },
    { Accessor: "origin" },
    { Accessor: "ruleHits" },
    { Accessor: "watchlistHits" },
    { Accessor: "graphHits" },
    { Accessor: "watchlistNameCounts" }
  ];

  return (
    <Xl8>
      <div className="container">
        <Title title="Flights" uri={props.uri}></Title>
        <div className="columns">
          <div className="column is-3">
            <div className="box2">
              <aside className="menu">
                <FilterForm
                  service={flights}
                  title="Filter"
                  callback={setData}
                  header={headers}
                >
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
                    inputType="text"
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
              <div className="card events-card">
                <Table
                  data={data}
                  id="Flights"
                  callback={cb}
                  key={data}
                  header={headers}
                ></Table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Xl8>
  );
};

export default Flights;
