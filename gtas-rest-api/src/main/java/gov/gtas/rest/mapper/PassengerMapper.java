package gov.gtas.rest.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import gov.gtas.rest.model.Passenger;

@Mapper
public interface PassengerMapper {
	


	@Select("SELECT * FROM passenger where first_name = #{firstName} and last_name = #{lastName} ")
	@ResultType(Passenger.class)
	@Results(id = "findPassengerByName", value = {
			  @Result(property = "id", column = "id", id = true),
			  @Result(property = "firstName", column = "first_name"),
			  @Result(property = "lastName", column = "last_name"),
			  @Result(property = "title", column = "title"),
			  @Result(property = "suffix", column = "suffix"),
			  @Result(property = "dob", column = "dob"),
			  @Result(property = "gender", column = "gender"),
			  @Result(property = "age", column = "age"),
			  @Result(property = "citizenshipCountry", column = "citizenship_country"),
			  @Result(property = "residencyCountry", column = "residency_country")
			  
			})
	public List<Passenger> findPassengerByName(@Param("firstName")String firstName, @Param("lastName")String lastName);
}
