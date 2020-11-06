import React, { useState } from "react";
import Title from "../../../components/title/Title";
import SegmentTable from "../../../components/segmentTable/SegmentTable";

const PNR = props => {
  const mockdata = [
    { key: "0", value: "UNB+IATA:1+UA++191228:1610+338471103105'" },
    { key: "1", value: "UNH+1+PNRGOV:13:1:IA+123456789'" },
    { key: "2", value: "MSG+:22'" },
    { key: "3", value: "ORG+UA:DJE+55107'" },
    { key: "TVLFRA ", value: "TVL+311219:1710:1955+FRA+IAD+UA+933'" },
    { key: "5", value: "EQN+1'" },
    { key: "6", value: "SRC'" },
    { key: "7", value: "RCI+UA:BB9E79::141119:1610'" },
    { key: "8", value: "DAT+700:311219:1416+710:281219:1610'" },
    { key: "AGEN70379193 ", value: "ORG+DL:IAD+70379193:LON+++A+GB:GBP+D050517'" },
    {
      key: "ADDGUADALAJARA PHONE715898537516 ",
      value: "ADD++700:6082 TATLOW AVENUE:GUADALAJARA:::MX::715898537516'"
    },
    { key: "11", value: "TIF+O GRADY+ELLIOT:A:1'" },
    { key: "FTI462075 ", value: "FTI+UA:462075:::'" },
    {
      key: "EMAIL ELLIOT.O GRADY@YAHOO.COM ",
      value: "IFT+4:28::YY+CTCE ELLIOT.O GRADY//YAHOO.COM'"
    },
    { key: "14", value: "REF+:695553137'" },
    { key: "15", value: "FAR+N+++++'" },
    {
      key: "SSRELLIOT DOCS758471042 ",
      value:
        "SSR+DOCS:HK::DL:::::/P/GBR/758471042/USA/06OCT68/M/19OCT19/O GRADY/ELLIOT/OSCAR'"
    },
    { key: "17", value: "TKT+15158035510059:T:1'" },
    { key: "18", value: "MON+B:94.34:GBP+T:100.00:GBP'" },
    { key: "19", value: "PTK+NR++150417:1010+DL+006+LON'" },
    {
      key: "20",
      value: "TXD++0.57:::AY6+0.85:::YQ+1.13:::YC+1.42:::XY+0.57:::XA+1.13:::XF'"
    },
    { key: "21", value: "DAT+710:311219'" },
    { key: "FOPXXXXXXXXXXXX7236 ", value: "FOP+CC::100.00:VI:XXXXXXXXXXXX7236:0321'" },
    {
      key: "23",
      value: "IFT+4:43+ELLIOT O GRADY+9178 ECCLESTON AVENUE TOKYO USA NRPXC+126087571132'"
    },
    { key: "24", value: "TIF+ELLROTT+DANIEL:A:2'" },
    { key: "FTI949218 ", value: "FTI+UA:949218:::'" },
    {
      key: "EMAIL DANIEL.ELLROTT@HOTMAIL.COM ",
      value: "IFT+4:28::YY+CTCE DANIEL.ELLROTT//HOTMAIL.COM'"
    },
    { key: "27", value: "REF+:5893702'" },
    { key: "28", value: "FAR+N+++++'" },
    {
      key: "SSRDANIEL DOCS938630055 ",
      value:
        "SSR+DOCS:HK::DL:::::/P/GBR/938630055/DEU/28DEC74/M/17DEC21/ELLROTT/DANIEL/EWAN'"
    },
    { key: "30", value: "TKT+15158019063007:T:1'" },
    { key: "31", value: "MON+B:235.85:GBP+T:250.00:GBP'" },
    { key: "32", value: "PTK+NR++150417:1010+DL+006+LON'" },
    {
      key: "33",
      value: "TXD++1.42:::AY6+2.12:::YQ+2.83:::YC+3.54:::XY+1.42:::XA+2.83:::XF'"
    },
    { key: "34", value: "DAT+710:311219'" },
    { key: "FOPXXXXXXXXXXXX9672 ", value: "FOP+CC::250.00:VI:XXXXXXXXXXXX9672:0217'" },
    {
      key: "36",
      value: "IFT+4:43+DANIEL ELLROTT+9178 ECCLESTON AVENUE TOKYO USA NRPXC+126087571132'"
    },
    { key: "TVLFRA ", value: "TVL+311219:1710::1955+FRA+IAD+UA+933:B'" },
    { key: "38", value: "APD+388'" },
    { key: "SEAT12D SEAT48G ", value: "SSR+SEAT:HK:2:UA:::FRA:IAD+12D::1+48G::2'" },
    {
      key: "SSRELLIOT ",
      value: "SSR+TKNE::::::::TKT NBR 15158035510059 31DEC19 E ELLIOTO GRADY'"
    },
    { key: "41", value: "RCI+UA:BB9E79'" },
    { key: "42", value: "ABI+1+:LHRRR+LON++DL'" },
    { key: "43", value: "UNT+25'" },
    { key: "44", value: "UNZ+1+0113360543'" }
  ];

  const segmentRef = React.createRef();
  const [foo, setFoo] = useState(0);

  // To fire on the onclick event of child tables, passing the key of the matching raw pnr record.
  // Use as the child's callback reference
  // Currently just iterates through the numeric keys as a demo
  const setActiveKeyWrapper = key => {
    setFoo(foo + 1);
    segmentRef.current.setActiveKey(foo);
    // segmentRef.current.setActiveKey(key);
  };

  return (
    <div className="container">
      <Title title="PNR"></Title>

      <div className="columns">
        <div className="top">
          <input type="button" onClick={setActiveKeyWrapper} value="click"></input>
          <SegmentTable
            title="Segment Table"
            data={mockdata}
            id="rawPnrSegments"
            ref={segmentRef}
          />
        </div>
      </div>
    </div>
  );
};

export default PNR;
