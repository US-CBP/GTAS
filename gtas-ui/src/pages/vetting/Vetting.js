import React, { useEffect, useState } from "react";
import Table from "../../components/table/Table";
import { cases } from "../../services/serviceWrapper";
import Title from "../../components/title/Title";
import LabelledInput from "../../components/labelledInput/LabelledInput";
import FilterForm from "../../components/filterForm/FilterForm";
import { hasData } from "../../utils/utils";
import { Col, Container } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import LabelledDateTimePickerStartEnd from "../../components/inputs/LabelledDateTimePickerStartEnd/LabelledDateTimePickerStartEnd";
import CheckboxGroup from "../../components/inputs/checkboxGroup/CheckboxGroup";
import "./Vetting.css";
import { useFetchHitCategories } from "../../services/dataInterface/HitCategoryService";
import SideNav from "../../components/sidenav/SideNav";
import Main from "../../components/main/Main";

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
        if (name === "displayStatusCheckBoxes" || name === "ruleTypes") {
          const checkboxObject = fields[name];
          const morphedArray = checkboxObject.map(cb => {
            let name = cb.name;
            let value = cb.checked;
            return { [name]: value };
          });
          paramObject[name] = Object.assign({}, ...morphedArray);
        } else if (name === "ruleCatFilter") {
          const checkboxObject = fields[name];
          const morphedArray = checkboxObject.map(cb => {
            let name = cb.name;
            let value = cb.checked;
            return { name: name, value: value };
          });
          paramObject[name] = [...morphedArray];
        } else {
          paramObject[name] = fields[name];
        }
      }
    });
    return "?requestDto=" + encodeURIComponent(JSON.stringify(paramObject));
  };

  let ruleTypes = {
    name: "ruleTypes",
    value: [
      {
        name: "WATCHLIST",
        label: "Watchlist:",
        type: "checkbox",
        checked: true
      },
      {
        name: "USER_RULE",
        label: "User Created Rule:",
        type: "checkbox",
        checked: true
      },
      {
        name: "GRAPH_RULE",
        label: "Graph Database Rule:",
        type: "checkbox",
        checked: true
      },
      {
        name: "MANUAL",
        label: "Manual: ",
        type: "checkbox",
        checked: true
      },
      {
        name: "PARTIAL_WATCHLIST",
        label: "Partial Watchlist:",
        type: "checkbox",
        checked: false
      }
    ]
  };

  let displayStatusCheckboxGroups = {
    name: "displayStatusCheckboxes",
    value: [
      {
        name: "NEW",
        label: "New:",
        type: "checkbox",
        checked: true
      },
      {
        name: "REVIEWED",
        label: "Reviewed:",
        type: "checkbox",
        checked: true
      },
      {
        name: "RE_OPENED",
        label: "Re Opened:",
        type: "checkbox",
        checked: false
      }
    ]
  };

  const { hitCategories, loading } = useFetchHitCategories();
  const [hitCategoryCheckboxes, setHitCategoryCheckboxes] = useState(
    <div>Loading Checkboxes...</div>
  );

  useEffect(() => {
    if (hitCategories !== undefined) {
      let tranformedResponse = hitCategories.map(hitCat => {
        return {
          ...hitCat,
          label: hitCat.name,
          type: "checkbox",
          checked: true
        };
      });
      const data = {
        name: "hitCategories",
        value: tranformedResponse
      };
      setHitCategoryCheckboxes(
        <CheckboxGroup
          datafield={data}
          inputVal={data.value}
          labelText="Passenger Hit Categories"
          name="ruleCatFilter"
        />
      );
    }
  }, [hitCategories, loading]);

  let renderedFilter;
  if (loading) {
    renderedFilter = <div>Loading Filter!</div>;
  } else {
    renderedFilter = (
      <>
        <SideNav>
          <Col>
            <FilterForm
              service={cases.get}
              title="Filter"
              callback={setDataWrapper}
              paramAdapter={parameterAdapter}
            >
              <hr />
              <CheckboxGroup
                datafield={displayStatusCheckboxGroups}
                inputVal={displayStatusCheckboxGroups.value}
                labelText="Passenger Hit Status"
                name="displayStatusCheckBoxes"
              />
              {hitCategoryCheckboxes}
              <CheckboxGroup
                datafield={ruleTypes}
                inputVal={ruleTypes.value}
                labelText="Hit Types"
                name="ruleTypes"
              />
              <LabelledInput
                datafield="myRulesOnly"
                name="myRulesOnly"
                label="My Rules Only"
                inputType="checkbox"
                inputVal={false}
                callback={cb}
                selected={false}
              />
              <LabelledInput
                datafield
                labelText="Passenger Last Name"
                inputType="text"
                name="paxName"
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
          <Col>
            <Title title="Priority Vetting" uri={props.uri} />
            <Table
              data={data}
              id="FlightDataTable"
              callback={onTableChange}
              header={["flightId", "hitNames", "status"]}
              ignoredFields={[
                "countDown",
                "priorityVettingListRuleTypes",
                "ruleCatFilter"
              ]}
              key={data}
            />
          </Col>
        </Main>
      </>
    );
  }
  return renderedFilter;
};

export default Vetting;
