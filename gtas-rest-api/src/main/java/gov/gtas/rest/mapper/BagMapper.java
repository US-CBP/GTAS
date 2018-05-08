package gov.gtas.rest.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import gov.gtas.rest.model.Bag;
import gov.gtas.rest.model.Passenger;

@Mapper
public interface BagMapper {
	
	
	@Select("SELECT * FROM bag where  passenger_id= #{passengerId} and flight_id=#{flightId} ")
	@ResultType(Bag.class)
	@Results(id = "findBagByFlightIdPassengerId", value = {
			  @Result(property = "id", column = "id", id = true),
			  @Result(property = "bagId", column = "bag_identification"),
			  @Result(property = "dataSource", column = "data_source"),
			  @Result(property = "passengerId", column = "passenger_id"),
			  @Result(property = "flightId", column = "flight_id")
			})
	public List<Bag> findBagByFlightIdPassengerId(@Param("passengerId")Long flightId, @Param("flightId")Long passengerId);

	

}
