package gov.gtas.services.matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.Passenger;
import gov.gtas.model.PaxWatchlistLink;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PaxWatchlistLinkRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

public interface MatchingService {
	public List<PaxWatchlistLinkVo> findByPassengerId(Long id);
	
	public void saveWatchListMatchByPaxId(Long id);
}
