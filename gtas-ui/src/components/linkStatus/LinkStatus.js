// import React, { Component } from 'react';
// import PropTypes from 'prop-types';
// import {Row} from 'reactstrap';
// import {pingUrl} from '../../Utils/request';

// // APB 508, unit tests
// // need timeout.
// // show status text in the well??
// class LinkStatus extends Component {
//   constructor(props) {
//     super(props);

//     this.state = {
//       style: 'container input-group-text'
//     }

//     this.ping();
//   }

//   ping() {
//     const style = this.state.style;
//     const green = `${style}-up`;
//     const red = `${style}-down`;

//     pingUrl(this.props.url)
//     .then(resp => {
//       if (resp === true) {
//         this.setState({
//           style: green
//         });
//       }
//       else {
//         this.setState({
//           style: red
//         });
//       }
//     })
//     .catch(() => {
//       this.setState({
//         style: red
//       });
//     })
//   }

//   render() {
//     return (
//       <Row className='p-2'>
//         <div className={this.state.style}>
//           <span className='col-2 link-status-title'>{this.props.title}</span>
//           <span className='ml-3'><a href={this.props.url} target='_blank'>{this.props.url}</a></span>
//         </div>
//       </Row>
//     )
//   }
// }

// LinkStatus.propTypes = {
//   url: PropTypes.string.isRequired,
//   title: PropTypes.string,
//   desc: PropTypes.string
// };

// export default LinkStatus;
