package gov.gtas.rest.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import gov.gtas.rest.model.Bag;
import gov.gtas.rest.model.Document;

@Mapper
public interface DocumentMapper {
	
	
	@Select("SELECT document_number,document_type, expiration_date, issuance_country, issuance_date FROM document WHERE  passenger_id= #{passengerId}")
	@ResultType(Bag.class)
	@Results(id = "findDocumentByPassengerId", value = {
			  @Result(property = "id", column = "id", id = true),
			  @Result(property = "documentNumber", column = "document_number"),
			  @Result(property = "expirationDate", column = "expiration_date"),
			  @Result(property = "issuanceCountry", column = "issuance_country"),
			  @Result(property = "documentType", column = "document_type"),
			  @Result(property = "issuanceDate", column = "issuance_date")
			 
			})
	public List<Document> findDocumentByPassengerId(@Param("passengerId")Long passengerId);



	

}
