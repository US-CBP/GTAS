package gov.gtas.repository;

import java.time.LocalDate;

import gov.gtas.enumtype.DataManagementTruncation;
import gov.gtas.model.User;

public interface DataManagementRepository 
{
	
    void truncateAllMessageDataByDate(LocalDate localDate,  User currentUser, DataManagementTruncation type) throws Exception;


}
