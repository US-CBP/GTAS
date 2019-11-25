// import React, {Component} from 'react';
// import PropTypes from 'prop-types';
// import './Modal.css';

// class ModalComponent extends Component {
//   constructor(props) {
//     super(props);

//     this.closeModal = this.closeModal.bind(this);

//     this.state = { 
//       visible: false,
//     };
//   }

//   closeModal(e) {
//     e.preventDefault();
//     this.props.callback(e);
//   }

//   render() {
//     return (
//       <Modal isOpen={this.props.visible} toggle={this.closeModal} className="modal-dialog-centered">
//       <ModalHeader toggle={this.closeModal}>{this.state.title}
//       </ModalHeader>
//       <ModalBody className="error">
//       {this.props.content}
//       </ModalBody>
//       <ModalFooter className="error">
//         <Button onClick={this.closeModal}>Close</Button>
//       </ModalFooter>
//     </Modal>
//     );
//   }
// }

// ModalComponent.propTypes = {
//   title: PropTypes.string.isRequired,
//   content: PropTypes.string.isRequired,
//   callback: PropTypes.func.isRequired,
// }

// export default ModalComponent;
