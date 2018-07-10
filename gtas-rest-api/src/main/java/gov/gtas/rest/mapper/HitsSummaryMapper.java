package gov.gtas.rest.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import gov.gtas.rest.model.Bag;
import gov.gtas.rest.model.HitDetail;
import gov.gtas.rest.model.HitsSummary;

@Mapper
public interface HitsSummaryMapper {
	
	
	@Select("SELECT * FROM hits_summary WHERE  passenger_id= #{passengerId}")
	@ResultType(Bag.class)
	@Results(id = "findHitsSummaryByPassengerId", value = {
			  @Result(property = "id", column = "id", id = true),
			  @Result(property = "createdDate", column = "created_date"),
			  @Result(property = "hitType", column = "hit_type"),
			  @Result(property = "ruleHitCount", column = "rule_hit_count"),
			  @Result(property = "wlHitCount", column = "wl_hit_count"),
			  @Result(property = "passengerId", column = "passenger_id"),
			  @Result(property = "flightId", column = "flight_id")
			 
			})
	public List<HitsSummary> findHitSummaryByPassengerId(@Param("passengerId")Long passengerId);
	
	
	
	
	
	
	@Select("SELECT * FROM hit_detail WHERE  hits_summary_id= #{hitsSummaryId}")
	@ResultType(Bag.class)
	@Results(id = "findHitDetailByHitsSummaryId", value = {
			  @Result(property = "id", column = "id", id = true),
			  @Result(property = "description", column = "description"),
			  @Result(property = "title", column = "title"),
			  @Result(property = "createdDate", column = "created_date"),
			  @Result(property = "hitType", column = "hit_type"),
			  @Result(property = "condText", column = "cond_text"),
			  @Result(property = "hitsSummaryId", column = "hit_summary_id"),
			  @Result(property = "ruleId", column = "rule_id")
			 
			})
	public List<HitDetail> findHitDetailByHitSummaryId(@Param("hitsSummaryId")Long hitsSummaryId);
	

}
