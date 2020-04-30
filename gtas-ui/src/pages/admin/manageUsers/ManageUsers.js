import React from "react";
import { Col, Button, Container } from "react-bootstrap";
import Table from "../../../components/table/Table";
import { userService, users } from "../../../services/serviceWrapper";
import Title from "../../../components/title/Title";
import Xl8 from "../../../components/xl8/Xl8";
import SideNav from "../../../components/sidenav/SideNav";
import Main from "../../../components/main/Main";
import LabelledInput from "../../../components/labelledInput/LabelledInput";
import FilterForm from "../../../components/filterForm/FilterForm";

import "./ManageUsers.scss";

const ManageUsers = props => {
  const cb = function(result) {};
  const cbFilterForm = result => {};
  const roles = [
    { value: "Admin", label: "Admin" },
    { value: "User", label: "User" }
  ];

  return (
    <>
      <SideNav className="manage-users-side-nav">
        <Col>
          <br />
          <Button
            block
            variant="ternary"
            className="btn btn-outline-info"
            name={props.name}
            placeholder={props.placeholder}
            // onClick={addUser} //TODO
            required={props.required}
            value={props.inputVal}
            alt={props.alt}
          >
            Add New User
          </Button>

          <br />
          <hr />

          <FilterForm
            service={users.get}
            callback={cbFilterForm} //TODO
            className="fg"
            title="Filter"
          >
            <br />
            <LabelledInput
              datafield
              name="username"
              className="text"
              callback={cb}
              alt="Filter by userName"
              inputType="text"
              placeholder="user name"
            />
            <LabelledInput
              datafield
              name="firstName"
              callback={cb}
              alt="Filter by first name"
              inputType="text"
              placeholder="first name"
            />
            <LabelledInput
              datafield
              name="lastName"
              callback={cb}
              alt="Filter by last name"
              inputType="text"
              placeholder="last name"
            />
            <LabelledInput
              datafield
              name="active"
              inputType="select"
              inputVal=""
              alt="User Active"
              placeholder="select active status"
              options={[
                { value: true, label: "True" },
                { value: false, label: "False" }
              ]}
              callback={cb}
            />
            <LabelledInput
              datafield
              name="role"
              inputType="select"
              inputVal=""
              alt="User Role"
              placeholder="select role"
              options={roles}
              callback={cb}
            />
          </FilterForm>
        </Col>
      </SideNav>
      <Main>
        <Table
          service={users.get}
          id="users"
          callback={cb}
          ignoredFields={["roles", "password"]}
        ></Table>
      </Main>
    </>
  );
};

export default ManageUsers;
