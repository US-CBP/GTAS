import React from "react";
import Title from "../../components/title/Title";
import { Link } from "@reach/router";
import Tabs from "../../components/tabs/Tabs";
import { Row, Container, Col, Form } from "react-bootstrap";
import "./PaxDetail.scss";
import PaxInfo from "../../components/paxInfo/PaxInfo";
import SideNav from "../../components/sidenav/SideNav";
import Main from "../../components/main/Main";

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
    <>
      <SideNav className="paxdetails-side-nav">
        <br />
        <PaxInfo></PaxInfo>
      </SideNav>
      <Main>
        <Title title="Passenger Detail" uri={props.uri} />

        <Tabs tabs={tabs} />
      </Main>
    </>
  );
};

export default PaxDetail;
