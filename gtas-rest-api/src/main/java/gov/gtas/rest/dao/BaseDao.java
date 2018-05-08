package gov.gtas.rest.dao;

import gov.gtas.rest.request.BaseRequest;
import gov.gtas.rest.response.BaseResponse;

public interface BaseDao <R extends BaseRequest<?>, P extends BaseResponse<?> >{

	P findByID (R r);
}
