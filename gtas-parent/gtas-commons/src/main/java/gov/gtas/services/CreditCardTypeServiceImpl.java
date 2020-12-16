/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.CreditCardType;
import gov.gtas.repository.CreditCardTypeRepository;
import gov.gtas.repository.CreditCardTypeRepositoryCustom;
import gov.gtas.vo.lookup.CreditCardTypeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreditCardTypeServiceImpl implements CreditCardTypeService {

  @Resource
  private CreditCardTypeRepository cctypeRespository;
  @Resource
  private CreditCardTypeRepositoryCustom cctypeRepoCust;

  @Override
  @Transactional
  public CreditCardTypeVo create(CreditCardTypeVo cctypeVo) {
    CreditCardType savedCreditCardType = cctypeRespository.save(buildCreditCardType(cctypeVo));

    return buildCreditCardTypeVo(savedCreditCardType);
  }

  @Override
  @Transactional
  public CreditCardTypeVo delete(Long id) {
    CreditCardTypeVo CreditCardTypeVo = this.findById(id);

    if (CreditCardTypeVo != null) {
      //cctypeRespository.delete(buildCreditCardType(CreditCardTypeVo));
      CreditCardTypeVo = archive(CreditCardTypeVo);
    }

    return CreditCardTypeVo;
  }

  @Override
  @Transactional
  public List<CreditCardTypeVo> findAll() {

    List<CreditCardType> allCreditCardTypes = (List<CreditCardType>) cctypeRespository.findAll();

    List<CreditCardTypeVo> allCreditCardTypeVos = new ArrayList<>();

    for (CreditCardType cctype : allCreditCardTypes) {
      allCreditCardTypeVos.add(buildCreditCardTypeVo(cctype));
    }

    return allCreditCardTypeVos;
  }

  @Override
  @Transactional
  public CreditCardTypeVo update(CreditCardTypeVo cctypeVo) {
    CreditCardType savedCreditCardType = cctypeRespository.save(buildCreditCardType(cctypeVo));

    return buildCreditCardTypeVo(savedCreditCardType);
  }

  @Override
  @Transactional
  public CreditCardTypeVo findById(Long id) {
    CreditCardType cctype = cctypeRespository.findById(id).orElse(null);

    if (cctype != null) {
      return buildCreditCardTypeVo(cctype);
    }

    return null;
  }

  @Override
  public List<CreditCardTypeVo> findAllNonArchived() {
    List<CreditCardType> allNonArchivedCreditCardTypes = cctypeRespository.findAllNonArchived();
    List<CreditCardTypeVo> allNonArchivedCreditCardTypeVos = new ArrayList<>();

    for (CreditCardType cctype : allNonArchivedCreditCardTypes) {
      allNonArchivedCreditCardTypeVos.add(buildCreditCardTypeVo(cctype));
    }

    return allNonArchivedCreditCardTypeVos;
  }

  @Override
  @Transactional
  public CreditCardTypeVo restore(CreditCardTypeVo cctypeVo) {
    CreditCardType restoredCreditCardType = cctypeRepoCust.restore(buildCreditCardType(cctypeVo));

    return buildCreditCardTypeVo(restoredCreditCardType);
  }

  @Override
  @Transactional
  public int restoreAll() {
    return cctypeRepoCust.restoreAll();
  }

  private CreditCardTypeVo archive(CreditCardTypeVo cctvo){
    if (cctvo != null) {
      CreditCardType cct =  buildCreditCardType(cctvo);
      cct.setArchived(true);
      cctypeRespository.save(cct);
    }

    return cctvo;
  }

//  @Override
//  @Transactional
//  public CreditCardTypeVo getCreditCardTypeByCode(String cctypeCode) {
//    List<CreditCardType> cctypes = cctypeRespository.getCreditCardTypeByCode(cctypeCode);
//
//    if (cctypes != null && cctypes.size() > 0) {
//      return buildCreditCardTypeVo(cctypes.get(0));
//    }
//
//    return null;
//  }

  private CreditCardType buildCreditCardType(CreditCardTypeVo cctypeVo) {
    return new CreditCardType(cctypeVo.getId(), cctypeVo.getOriginId(), cctypeVo.getCode(), cctypeVo.getDescription(), cctypeVo.getArchived());
  }

  private CreditCardTypeVo buildCreditCardTypeVo(CreditCardType cctype) {
    return new CreditCardTypeVo(cctype.getId(), cctype.getOriginId(), cctype.getCode(), cctype.getDescription(), cctype.getArchived());
  }

}
