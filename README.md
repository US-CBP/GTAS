#
<p align="center"><img width=50% src="https://user-images.githubusercontent.com/20464494/62487148-1ad6c580-b78f-11e9-9327-816d1590ad7f.png"></p>

## Developed by U.S. Customs and Border Protection for The World Customs Organization
<center>
  <table>
    <tr>
      <td><a href="https://user-images.githubusercontent.com/20464494/62390248-d4376000-b52f-11e9-9fff-6d3471e3e9cc.png"><img width="100%" alt="Flights Grid" src="https://user-images.githubusercontent.com/20464494/62390248-d4376000-b52f-11e9-9fff-6d3471e3e9cc.png"></a></td>
      <td><a href="https://user-images.githubusercontent.com/20464494/62390364-106ac080-b530-11e9-8617-0e48c678ebaf.png"><img width="100%" alt="Seating Chart" src="https://user-images.githubusercontent.com/20464494/62390364-106ac080-b530-11e9-8617-0e48c678ebaf.png"></a></td>
    </tr>
        <tr>
      <td><a href="https://user-images.githubusercontent.com/20464494/62390314-f7faa600-b52f-11e9-9df2-49e1a6f11f19.png"><img width="100%" alt="Passenger Details" src="https://user-images.githubusercontent.com/20464494/62390314-f7faa600-b52f-11e9-9df2-49e1a6f11f19.png"></a></td>
      <td><a href="https://user-images.githubusercontent.com/20464494/62390327-ffba4a80-b52f-11e9-85e2-824fdd349c90.png"><img width="100%" alt="Graph Database" src="https://user-images.githubusercontent.com/20464494/62390327-ffba4a80-b52f-11e9-85e2-824fdd349c90.png"></a></td>
    </tr>
  </table>
</center>
  
## About GTAS
The Global Travel Assessment System (GTAS) is web application for improving border security. It enables government agencies to automate the identification of high-risk air travelers in advance of their intended travel. 

The United Nations has called upon members to use Advance Passenger Information (API) and Passenger Name Record (PNR) data for preventing the movement of high-risk travelers, and GTAS was designed to give every country that capability. The World Customs Organization (WCO) has partnered with U.S. Customs and Border Protection (US-CBP) because of the shared belief that every border security agency should have access to the latest tools. US-CBP has made this repository avaialble to the WCO to facilite deployment for its member states.

This belief has become a reality with GTAS in production. It is able to handle the load of a high-volume country, and has successfully identified high-risk travelers. The mission GTAS supports goes beyond combating terrorism and includes;

* Preventing the spread of human health outbreaks
* Protecting wildlife by preventing the transport of illegal animal products
* Finding missing persons
* Fighting drug trade
* Protecting agriculture

Which results in;

* Safer travel
* Expedited Screening of passengers
* Less waiting time at airports

GTAS accomplishes these goals through providing all the necessary decision support system features to 
(1) receive and store air traveler data
(2) provide real-time risk assessment against this data based on your own specific risk criteria and/or watch lists
(3) view high risk travelers, their associated flight and reservation information, and possible affiliates

## Where is GTAS currently Deployed?

* Maldives | Maldives Customs Service http://www.wcoomd.org/es-es/media/newsroom/2019/july/gtas-go-live-in-maldives.aspx
* Two other deployments scheduled before the end of 2019

## About the code
GTAS is developed in Java and uses open source software components and platforms.

## About the data
GTAS parses data provided by airline departure control systems (API) and reservation systems (PNR). Respectively, these messages conform to WCO UN/EDIFACT PAXLST and PNRGOV message formats.

* UN/EDIFACT PAXLST 02B and later
* PNRGOV 11.1 and later

## Features
* API and PNR Data Processing
* Criteria Based Risk Assessment
* Watch List Based Risk Assessment
* Identification of partial Watch List matches
* Risk Criteria Management Interface
* Watch List Management Interface
* View Flights and Passengers Interface
* Query Flights and Passengers Interface
* Cloud-Ready 
* Case Management for Decision Support
* Free-Text Search
* Customizable Dashboard
* Graph Database (Neo4J)
* Graph Database Rules Engine for Identifying Risk Patterns


## Issues

Find a bug or want to request a new feature? Please let us know by submitting an issue. The GTAS team manages all GTAS updates, bugs, and feature additions via GitHub's public issue tracker in this repository. In the spirit of open source software, everyone is encouraged to help improve this project. 

### Submit Issues

* Before submitting a new issue, check to make sure a similar issue isn't already open. If one is, contribute to that issue thread with your feedback.

* When submitting a bug report, please try to provide as much detail as possible, i.e. a screenshot or gist that demonstrates the problem, the browser you are using, and any relevant links. 

### Pull Requests

If you'd like to contribute to this project, please make a pull request. We'll review the pull request and discuss the changes.



