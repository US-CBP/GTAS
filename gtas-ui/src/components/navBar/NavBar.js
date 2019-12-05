// import React from 'react';
// import {
//   Collapse,
//   Navbar,
//   NavbarToggler,
//   NavbarBrand,
//   Nav,
//   NavItem,
//   NavLink,
//   UncontrolledDropdown,
//   DropdownToggle,
//   DropdownMenu,
//   DropdownItem } from 'reactstrap';
//   import './NavBar.css';
// // APB drop reactstrap

// export default class Example extends React.Component {
//   constructor(props) {
//     super(props);

//     this.toggle = this.toggle.bind(this);

//     this.state = {
//       isOpen: false,
//       isModalVisible: false
//     };
//   }

//   toggle() {
//     this.setState({
//       isOpen: !this.state.isOpen
//     });
//   }

//   render() {
//     return (
//       <div className="NavBar">
//         <Navbar className="navColor" expand="md" id="navbar">
//           <NavbarBrand className="navLinkColor" id="nav-home" href="/">
//           <img src={require('../../images/foo.png')} alt="logo" />GTAS</NavbarBrand>
//           <NavbarToggler onClick={this.toggle} />
//           <Collapse isOpen={this.state.isOpen} navbar>
//             <Nav className="ml-auto" navbar>
//               <UncontrolledDropdown nav inNavbar>
//                 <DropdownToggle  className="navLinkColor" nav caret>
//                   Forms
//                 </DropdownToggle>
//                 <DropdownMenu right>
//                   <DropdownItem>
//                     <NavLink className="navDropdownColor" id="nav-blank" href="#/blank">Blank</NavLink>
//                   </DropdownItem>
//                   <DropdownItem>
//                     <NavLink className="navDropdownColor" id="nav-foo" href="#/i9">Foo</NavLink>
//                   </DropdownItem>
//                   <DropdownItem>
//                     <NavLink className="navDropdownColor" id="nav-company" href="#/company">Company Form</NavLink>
//                   </DropdownItem>
//                 </DropdownMenu>

//               </UncontrolledDropdown>
//               <UncontrolledDropdown nav inNavbar>
//                 <DropdownToggle className="navLinkColor" nav caret>
//                   Admin
//                 </DropdownToggle>
//                 <DropdownMenu right>
//                   <DropdownItem>
//                     <NavLink className="navDropdownColor" id="nav-external" href="#/external">External Links</NavLink>
//                   </DropdownItem>
//                   <DropdownItem>
//                     <NavLink className="navDropdownColor" id="nav-external2" href="#/external2">External Links 2</NavLink>
//                   </DropdownItem>
//                 </DropdownMenu>
//               </UncontrolledDropdown>
//               <NavItem>
//                 <NavLink className="navLinkColor" href=""></NavLink>
//               </NavItem>
//             </Nav>
//           </Collapse>
//         </Navbar>

//       </div>
//     );
//   }
// }
