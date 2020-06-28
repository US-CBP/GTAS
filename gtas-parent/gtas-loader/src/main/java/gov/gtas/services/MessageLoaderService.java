/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import gov.gtas.model.Bag;
import gov.gtas.model.BagMeasurements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.vo.MessageVo;

import javax.transaction.Transactional;

@Service
public abstract class MessageLoaderService {

	@Autowired
	protected GtasLoader loaderRepo;

	@Autowired
	protected LoaderUtils utils;

	public abstract List<String> preprocess(String message);

	public abstract MessageVo parse(String message);

	public abstract boolean load(MessageVo parsedMessage);

	protected String filePath = null;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	protected boolean useIndexer;

	public void setUseIndexer(boolean useIndexer) {
		this.useIndexer = useIndexer;
	}

	@Transactional
	public abstract MessageDto parse(MessageDto msgDto);

	@Transactional
	public abstract MessageInformation load(MessageDto msgDto);

	WeightCountDto getBagStatistics(Set<Bag> bagSet) {
		Set<BagMeasurements> bagMeasurementsSet = bagSet.stream().map(Bag::getBagMeasurements).filter(Objects::nonNull)
				.collect(Collectors.toSet());
		Integer bagCount = 0;
		Double bagWeight = 0D;
		for (BagMeasurements bagMeasurements : bagMeasurementsSet) {
			if (bagMeasurements.getBagCount() != null) {
				bagCount += bagMeasurements.getBagCount();
			}
			if (bagMeasurements.getWeight() != null) {
				bagWeight += bagMeasurements.getWeight();
			}
		}
		WeightCountDto weightCountDto = new WeightCountDto();
		weightCountDto.setCount(bagCount);
		weightCountDto.setWeight(bagWeight);
		return weightCountDto;
	}
}
