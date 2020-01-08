import React from "react";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import Tabs from "../../components/tabs/Tabs";
import { Row, Container, Col, Form } from "react-bootstrap";
import "./PaxDetail.css";
import PaxInfo from "../../components/paxInfo/PaxInfo";

const PaxDetail = props => {
  const tabcontent = props.children.props.children;
  const tabs = [
    { title: "Summary", link: tabcontent[0] },
    { title: "APIS", link: tabcontent[1] },
    { title: "PNR", link: tabcontent[2] },
    { title: "Flight History", link: tabcontent[3] },
    { title: "Link Analysis", link: tabcontent[4] }
  ];
  return (
    <Container fluid>
      <Title title="Passenger Detail" uri={props.uri} />
      <Row flex>
        <Col lg="3" md="3" sm="3" className="box2">
          <PaxInfo></PaxInfo>
        </Col>

        <Col lg="9" md="9" sm="9" className="box2">
          <div>
            <Tabs tabs={tabs} />
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default PaxDetail;
