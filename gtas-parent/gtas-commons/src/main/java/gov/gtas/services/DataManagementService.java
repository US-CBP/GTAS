package gov.gtas.services;

import java.time.LocalDate;

import gov.gtas.enumtype.DataManagementTruncation;
import gov.gtas.model.User;

public interface DataManagementService {
	void truncateAllMessageDataByDate(LocalDate localDate, User currentUser, DataManagementTruncation type)
			throws Exception;
}
