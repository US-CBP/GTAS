import React from "react";
import Title from "../../components/title/Title";
import Tabs from "../../components/tabs/Tabs";
import Banner from "../../components/banner/Banner";

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
    <div className="container">
      <Title title="Admin" uri={props.uri}></Title>
      <Banner
        id="banner"
        styleName="primary"
        text="Something has happened."
        defaultState={showBanner}
      ></Banner>
      <div className="columns">
        <div className="column">
          <div className="box2">
            <div className="top">
              <Tabs tabs={tablist} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Admin;
