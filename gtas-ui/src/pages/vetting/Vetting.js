import React from 'react';
import Table from '../../components/table/Table';
import {files} from '../../services/serviceWrapper';
import Title from '../../components/title/Title';
import { Link } from "@reach/router";
import LabelledInput from '../../components/labelledInput/LabelledInput';

const Vetting = (props) => {
  const onTextChange = () => {

  }

return (
    <div className='container'>
      <Title title='Priority Vetting' uri={props.uri}></Title>

      <div className='columns'>
      <div className='column is-3'>
        <div className='box2'>
            <aside className="menu">
                <p className="menu-label">Filters</p>
                <hr></hr>
                <LabelledInput labelText='Passenger Hit Status' inputType='text' name='hitStatus' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='My Rules Only' inputType='text' name='myRules' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Rule Category' inputType='text' name='ruleCat' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Rule Type' inputType='text' name='ruleType' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Start Date' inputType='text' name='direction' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='End Date' inputType='text' name='direction' callback={onTextChange} alt='nothing' />
                <LabelledInput labelText='Passenger Last Name' inputType='text' name='paxLastName' callback={onTextChange} alt='nothing'/>
                <LabelledInput labelText='Full Flight ID' inputType='text' name='fullFlightId' callback={onTextChange} alt='nothing' />
            </aside>
        </div>
      </div>

      <div className='column'>
        <div className='box2'>
          <div className='top'>
            <Table service={files.get} id='foo' ></Table>
          </div>
        </div>
      </div>
    </div>
  </div>
)

}

export default Vetting;