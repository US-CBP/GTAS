import React, { Suspense } from "react";
import { Router, Redirect } from "@reach/router";
import IdleTimer from "react-idle-timer";
// import logo from './logo.svg';
import Login from "./pages/login/Login";
import Flights from "./pages/flights/Flights";
import PriorityVetting from "./pages/vetting/Vetting";
import Home from "./pages/home/Home";
import "bootstrap/dist/css/bootstrap.min.css";
import "font-awesome/css/font-awesome.min.css";
import "./App.css";
import Dashboard from "./pages/dashboard/Dashboard";
import PaxDetail from "./pages/paxDetail/PaxDetail";
import Summary from "./pages/paxDetail/summary/Summary";
import APIS from "./pages/paxDetail/apis/APIS";
import PNR from "./pages/paxDetail/pnr/PNR";
import FlightHistory from "./pages/paxDetail/flightHistory/FlightHistory";
import FlightPax from "./pages/flightPax/FlightPax";
import Admin from "./pages/admin/Admin";
import ManageUser from "./pages/admin/manageUsers/ManageUsers";

import FileDownload from "./pages/admin/fileDownload/FileDownload";
import AuditLog from "./pages/admin/auditLog/AuditLog";
import ErrorLog from "./pages/admin/errorLog/ErrorLog";
import CodeEditor from "./pages/admin/codeEditor/CodeEditor";
import LoaderStats from "./pages/admin/loaderStats/LoaderStats";
import Settings from "./pages/admin/settings/Settings";
import WatchlistCats from "./pages/admin/watchlistCats/WatchlistCats";
import NoteTypeCats from "./pages/admin/noteTypeCats/NoteTypeCats";

import Queries from "./pages/tools/queries/Queries";
import Rules from "./pages/tools/rules/Rules";
import Neo4J from "./pages/tools/neo4J/Neo4J";
import Watchlist from "./pages/tools/watchlist/Watchlist";
import About from "./pages/tools/about/About";
import GModal from "./components/modal/GModal";
import AddUser from "./pages/admin/manageUsers/addUser/AddUser";

import Page404 from "./pages/page404/Page404";
import PageUnauthorized from "./pages/pageUnauthorized/PageUnauthorized";
import ErrorBoundary from "./components/errorBoundary/ErrorBoundary";
import Loading from "./components/loading/Loading";

import Authenticator from "./context/authenticator/Authenticator";
import RoleAuthenticator from "./context/roleAuthenticator/RoleAuthenticator";
import UserProvider from "./context/user/UserContext";

import { ROLE } from "./utils/constants";

//Split Link Analysis (Graph component, d3, jquery deps) into a separate bundle
const LinkAnalysis = React.lazy(() =>
  import("./pages/paxDetail/linkAnalysis/LinkAnalysis")
);

export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.idleTimer = null;
    this.onAction = this._onAction.bind(this);
    this.onActive = this._onActive.bind(this);
    this.onIdle = this._onIdle.bind(this);
    this.state = {
      showModal: false,
      redirect: false
    };
  }

  _onAction(e) {
    // console.log('user did something', e)
  }

  _onActive(e) {
    // console.log('user is active', e)
    // console.log('time remaining', this.idleTimer.getRemainingTime())
  }

  _onIdle(e) {
    console.log("user is idle", e);
    console.log("last active", this.idleTimer.getLastActiveTime());

    // Logout and redirect to login page
    this.setState({ redirect: true });
  }

  toggleModal() {
    this.setState({ showModal: !this.state.showModal });
  }

  render() {
    if (this.state.redirect) {
      this.setState({ redirect: false });
      // logout and ...
      return <Redirect to="/login" />;
    }

    return (
      <React.StrictMode>
        <UserProvider>
          <Router>
            <Login path="/login"></Login>
          </Router>
          {this.state.showModal ? (
            <GModal>
              <div>
                <h1>You have been inactive for {this.idleTimer.getElapsedTime()}</h1>
                <button onClick={this.toggleModal}>OK</button>
              </div>
            </GModal>
          ) : null}
          <div className="App">
            <IdleTimer
              ref={ref => {
                this.idleTimer = ref;
              }}
              element={document}
              onActive={this.onActive}
              onIdle={this.onIdle}
              onAction={this.onAction}
              debounce={250}
              timeout={1000 * 25 * 60}
            />
            <Suspense fallback="loading">
              <Authenticator>
                <Router>
                  <RoleAuthenticator
                    path="/"
                    alt={<PageUnauthorized path="pageUnauthorized"></PageUnauthorized>}
                    roles={[ROLE.ADMIN, ROLE.USER]}
                  >
                    <Redirect from="/" to="/gtas" noThrow />
                    <Home path="/gtas">
                      <Redirect from="/gtas" to="/gtas/flights" noThrow />
                      <Dashboard path="dashboard"></Dashboard>
                      <Flights path="flights"></Flights>
                      <FlightPax path="flightpax"></FlightPax>
                      <PriorityVetting path="vetting"></PriorityVetting>
                      <Queries path="tools/queries"></Queries>
                      <Rules path="tools/rules"></Rules>
                      <Neo4J path="tools/neo4j"></Neo4J>
                      <Watchlist path="tools/watchlist"></Watchlist>
                      <About path="tools/about"></About>
                      <Admin path="admin">
                        <ManageUser name="Manage Users" path="manageusers">
                          <AddUser name="Add User" path="/gtas/admin/adduser"></AddUser>
                        </ManageUser>
                        <AuditLog name="Audit Log View" path="auditlog"></AuditLog>
                        <ErrorLog name="Error Log View" path="errorlog"></ErrorLog>
                        <Settings name="Settings" path="settings"></Settings>
                        <FileDownload
                          name="File Download"
                          path="filedownload"
                        ></FileDownload>
                        <CodeEditor name="Code Editor" path="codeeditor"></CodeEditor>
                        <LoaderStats
                          name="Loader Statistics"
                          path="loaderstats"
                        ></LoaderStats>
                        <WatchlistCats
                          name="Watchlist Categories"
                          path="watchlistcats"
                        ></WatchlistCats>
                        <NoteTypeCats
                          name="Note Type Categories"
                          path="notetypecats"
                        ></NoteTypeCats>
                      </Admin>
                      <PaxDetail path="paxdetail">
                        <Summary path="summary"></Summary>
                        <APIS path="apis"></APIS>
                        <PNR path="pnr"></PNR>
                        <FlightHistory path="flighthistory"></FlightHistory>
                        <ErrorBoundary>
                          <Suspense fallback={<Loading></Loading>}>
                            <LinkAnalysis path="linkanalysis"></LinkAnalysis>
                          </Suspense>
                        </ErrorBoundary>
                      </PaxDetail>
                      <Page404 default></Page404>
                      <PageUnauthorized path="pageUnauthorized"></PageUnauthorized>
                    </Home>
                  </RoleAuthenticator>
                </Router>
              </Authenticator>
            </Suspense>
          </div>
        </UserProvider>
      </React.StrictMode>
    );
  }
}
