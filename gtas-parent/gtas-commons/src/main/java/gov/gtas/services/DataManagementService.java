package gov.gtas.services;

import java.time.LocalDate;

import gov.gtas.model.User;
import gov.gtas.repository.DataManagementRepositoryImpl.DataTruncationType;

public interface DataManagementService 
{
	void truncateAllMessageDataByDate(LocalDate localDate, User currentUser, DataTruncationType type) throws Exception;
}
