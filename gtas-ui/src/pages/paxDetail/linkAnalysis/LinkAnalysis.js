import React from 'react';
import Table from '../../../components/table/Table';
import {company} from '../../../services/serviceWrapper';
import Title from '../../../components/title/Title';

const LinkAnalysis = () => {
return (
    <div className='container'>
      <Title title='LinkAnalysis'></Title>

      <div className='columns'>
          <div className='top'>
            <Table service={company.get} id='foo' ></Table>
          </div>
      </div>
    </div>
)

}

export default LinkAnalysis;