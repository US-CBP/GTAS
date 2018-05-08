package gov.gtas.rest.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import gov.gtas.rest.model.CaseHitDispComment;
import gov.gtas.rest.model.CaseHitDisposition;
import gov.gtas.rest.model.Cases;
import gov.gtas.rest.model.PassengerTravelDetail;


@Mapper
public interface CasesMapper {
	
	
	@Select("select * from gtas.cases  where firstName = #{firstName} and lastName = #{lastName} and date_format(dob, '%Y-%m-%d') = #{dob} ")
	@ResultType(PassengerTravelDetail.class)
	@Results(id = "findCasesById", value = {
			  @Result(property = "id", column = "id", id = true),
			  @Result(property = "createdAt", column = "created_at"),
			  @Result(property = "updatedAt", column = "updated_at"),
			  @Result(property = "description", column = "description"),
			  @Result(property = "citizenshipCountry", column = "citizenshipCountry"),
			  @Result(property = "dob", column = "dob"),
			  @Result(property = "document", column = "document"),
			  @Result(property = "firstName", column = "firstName"),
			  @Result(property = "etaDate", column = "eta_date"),
			  @Result(property = "etdDate", column = "etd_date"),
			  @Result(property = "flightId", column = "flightId"),
			  @Result(property = "flightNumber", column = "flightNumber"),
			  @Result(property = "highPriorityRuleCatId", column = "highPriorityRuleCatId"),
			  @Result(property = "lastName", column = "lastName"),
			  @Result(property = "paxId", column = "paxId"),
			  @Result(property = "passengerName", column = "passengerName"),
			  @Result(property = "passengerType", column = "passengerType"),
			  @Result(property = "status", column = "status")
			 
			})
	public List<Cases> findCasesByName(@Param("firstName")String firstName, @Param("lastName")String lastName, @Param("dob")String dob);
	
	
	
	@Select("SELECT cd.case_id,hd.id,hd.created_at,hd.updated_at,hd.description,hd.hit_id,hd.`status`,hd.valid,rc.category, rc.description as category_description \r\n" + 
			"FROM gtas.case_hit_disp cd LEFT JOIN gtas.hits_disposition hd ON cd.hit_disp_id = hd.id LEFT JOIN rule_category rc ON rc.id = hd.rule_cat_id  WHERE cd.case_id =#{caseId}")
	@ResultType(CaseHitDisposition.class)
	@Results(id = "findhitDispByCaseId", value = {
			  @Result(property = "caseId", column = "case_id", id = true),
			  @Result(property = "id", column = "id"),
			  @Result(property = "createdAt", column = "created_at"),
			  @Result(property = "updatedAt", column = "updated_at"),
			  @Result(property = "description", column = "description"),
			  @Result(property = "hitId", column = "hit_id"),
			  @Result(property = "hitId", column = "document"),
			  @Result(property = "status", column = "status"),
			  @Result(property = "valid", column = "valid"),
			  @Result(property = "category", column = "category"),
			  @Result(property = "categoryDescription", column = "category_description")
			 
			})
	public List<CaseHitDisposition> findCaseHitDisposition(@Param("caseId")Long caseId);
	

	
	@Select("SELECT * from case_hit_disp_comments chd LEFT JOIN hits_disposition_comments hdc ON chd.hit_disp_comments_id = hdc.id WHERE chd.hit_disp_id =#{hitDispId}")
	@ResultType(CaseHitDispComment.class)
	@Results(id = "findhitDispCommentsByHitDispd", value = {
			  @Result(property = "hitDispId", column = "hit_disp_id", id = true),
			  @Result(property = "hitDispCommentsId", column = "hit_disp_comments_id"),
			  @Result(property = "createdAt", column = "created_at"),
			  @Result(property = "updatedAt", column = "updated_at"),
			  @Result(property = "comments", column = "comments")
			})
	public List<CaseHitDispComment> findCaseHitDispComments(@Param("hitDispId")Long hitDispId);
	

	
}
