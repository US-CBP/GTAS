import React from "react";
import { settingsinfo } from "../../../services/serviceWrapper";
import Form from "../../../components/form/Form";
import LabelledInput from "../../../components/labelledInput/LabelledInput";
import { Container, Col } from "react-bootstrap";

const Settings = ({ name }) => {
  const onChange = function(result) {};
  // const cb = function() {};

  return (
    <Container>
      <Col lg={{ span: 4, offset: 4 }}>
        <Form
          getService={settingsinfo.get}
          submitService={settingsinfo.put}
          title=""
          action="edit"
        >
          <LabelledInput
            datafield
            labelText="Matching Threshold"
            inputType="number"
            name="matchingThreshold"
            callback={onChange}
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="Maximum Passenger Query Results"
            inputType="number"
            name="maxPassengerQueryResult"
            callback={onChange}
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="Maximum Flight Query Results"
            inputType="number"
            name="maxFlightQueryResult"
            callback={onChange}
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="Watchlists Matching Flight Range"
            inputType="number"
            name="flightRange"
            callback={onChange}
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="Maximum Rule Hits Allowed Per Run on Rule"
            inputType="number"
            name="maxRuleHit"
            callback={onChange}
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="APIS Only Flag"
            inputType="select"
            options={[
              { value: "TRUE", label: "TRUE" },
              { value: "FALSE", label: "FALSE" }
            ]}
            name="apisOnlyFlag"
            callback={onChange}
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="APIS Version"
            inputType="text"
            name="apisVersion"
            callback={onChange}
            alt="nothing"
          />
        </Form>
      </Col>
    </Container>
  );
};

export default Settings;
