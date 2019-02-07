package gov.gtas.repository;

import java.time.LocalDate;

import gov.gtas.model.User;
import gov.gtas.repository.DataManagementRepositoryImpl.DataTruncationType;

public interface DataManagementRepository 
{
	
    void truncateAllMessageDataByDate(LocalDate localDate,  User currentUser, DataTruncationType type) throws Exception;


}
