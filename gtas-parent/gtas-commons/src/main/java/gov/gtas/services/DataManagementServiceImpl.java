package gov.gtas.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.model.User;
import gov.gtas.repository.DataManagementRepository;

@Service
public class DataManagementServiceImpl implements DataManagementService
{
	
	@Autowired
	private DataManagementRepository dataManagementRepository;
	

	@Override
	@Transactional
	public void truncateAllMessageDataByDate(LocalDate localDate, User currentUser) throws Exception
	{
		dataManagementRepository.truncateAllMessageDataByDate(localDate, currentUser);
		
	}
}
