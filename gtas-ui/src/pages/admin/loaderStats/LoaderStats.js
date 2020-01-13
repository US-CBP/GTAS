import React from "react";
import LabelledInput from "../../../components/labelledInput/LabelledInput";
import Form from "../../../components/form/Form";
import { loaderStats } from "../../../services/serviceWrapper";
import { Container, Col } from "react-bootstrap";

const LoaderStats = ({ name }) => {
  const cb = function(result) {};
  const onChange = function(result) {};

  return (
    <Container>
      <Col lg={{ span: 4, offset: 4 }}>
        <Form
          getService={loaderStats.get}
          title=""
          callback={cb}
          action="edit"
          submitText="Refresh"
        >
          <LabelledInput
            datafield
            labelText="Last message received:"
            inputType="text"
            name="lastMessageInSystem"
            alt="nothing"
            readOnly
            callback={onChange}
          />
          <LabelledInput
            datafield
            labelText="Last message analyzed:"
            inputType="text"
            name="lastMessageAnalyzedByDrools"
            callback={onChange}
            readOnly
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="Most recent rule hIt (Partial excluded) timestamp:"
            inputType="text"
            name="mostRecentRuleHit"
            callback={onChange}
            readOnly
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="Passengers Count from past 500 messages:"
            inputType="text"
            name="passengerCount"
            callback={onChange}
            readOnly
            alt="nothing"
          />
          <LabelledInput
            datafield
            labelText="Loading/Parsing errors past 500 messages:"
            inputType="text"
            name="totalLoadingParsingErrors"
            callback={onChange}
            readOnly
            alt="nothing"
          />

          <LabelledInput
            datafield
            labelText="Rule errors last 500 messages:"
            inputType="text"
            name="totalRuleErros"
            callback={onChange}
            readOnly
            alt="nothing"
          />
        </Form>
      </Col>
    </Container>
  );
};

export default LoaderStats;
