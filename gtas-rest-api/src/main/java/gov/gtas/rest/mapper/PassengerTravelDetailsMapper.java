package gov.gtas.rest.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import gov.gtas.rest.model.Passenger;
import gov.gtas.rest.model.PassengerTravelDetail;


@Mapper
public interface PassengerTravelDetailsMapper {
	
	
	@Select("select * from gtas.flight_pax fp LEFT JOIN flight fl ON fp.flight_id = fl.id where fp.passenger_id = #{passengerId}")
	@ResultType(PassengerTravelDetail.class)
	@Results(id = "findPassengerDetailById", value = {
			  @Result(property = "id", column = "id", id = true),
			  @Result(property = "debarkation", column = "debarkation"),
			  @Result(property = "debCountry", column = "deb_country"),
			  @Result(property = "embarkation", column = "embarkation"),
			  @Result(property = "embCountry", column = "emb_country"),
			  @Result(property = "headOfPool", column = "head_of_pool"),
			  @Result(property = "msgSource", column = "msg_source"),
			  @Result(property = "firstArrivalPort", column = "first_arrival_port"),
			  @Result(property = "residenceCountry", column = "residence_country"),
			  @Result(property = "refNumber", column = "ref_number"),
			  @Result(property = "travelerType", column = "traveler_type"),
			  @Result(property = "averageBagWeight", column = "average_bag_weight"),
			  @Result(property = "bagCount", column = "bag_count"),
			  @Result(property = "bagWeight", column = "bag_weight"),
			  @Result(property = "flightId", column = "flight_id"),
			  @Result(property = "installAddressId", column = "install_address_id"),
			  @Result(property = "passengerId", column = "passenger_id"),
			  @Result(property = "carrier", column = "carrier"),
			  @Result(property = "eta", column = "eta"),
			  @Result(property = "etd", column = "etd"),
			  @Result(property = "flightDate", column = "flight_date"),
			  @Result(property = "fullFlightNumber", column = "full_flight_number")
			
			})
	public List<PassengerTravelDetail> findPassengerTravelDetail(@Param("passengerId") Long passengerId);

}
