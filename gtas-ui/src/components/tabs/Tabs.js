import React from "react";
import RRTabs from "react-responsive-tabs";
import "react-responsive-tabs/styles.css";
import "./Tabs.css";

// const tabs = [{ title: 'Settings', link: '/tools/settings' }, { title: 'Audit Log', link: '/tools/auditLog' }, { title: 'Code Editor', link: '/tools/codeEditor' }];

const Tabs = props => {
  const tablist = props.tabs.map((tab, index) => ({
    title: tab.title,
    getContent: () => tab.link,
    key: index,
    tabClassName: "tab",
    panelClassName: "panel"
  }));

  return <RRTabs items={tablist} className="is-fullwidth" showInkBar={true} />;
};
export default Tabs;
