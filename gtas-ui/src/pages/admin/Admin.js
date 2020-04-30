import React from "react";
import Title from "../../components/title/Title";
import Tabs from "../../components/tabs/Tabs";
import Banner from "../../components/banner/Banner";
import { Container } from "react-bootstrap";

const Admin = props => {
  const tabcontent = props.children.props.children;

  const tablist = tabcontent.map((tab, idx) => {
    return { title: tab.props.name, key: tab.props.name, link: tab };
  });

  // use cases for banner vs notification?
  const showBanner = () => {
    return false;
  };

  return (
    <>
      <Banner
        id="banner"
        styleName="warning"
        text="Something has happened."
        defaultState={showBanner}
      />

      <Tabs tabs={tablist} />
    </>
  );
};

export default Admin;
