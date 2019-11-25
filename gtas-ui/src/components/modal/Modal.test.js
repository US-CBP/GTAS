import React from 'react';
import ReactDOM from 'react-dom';
import ModalComponent from './Modal';

const callback = ( )=>{};
const fake = 'faketext';
const div = document.createElement('div');

describe('ModalComponent', () => {
  it('renders without crashing', () => {

    ReactDOM.render(<ModalComponent title={fake} callback={callback} content={fake}/>, div);
    ReactDOM.unmountComponentAtNode(div);
  });
});   //describe
