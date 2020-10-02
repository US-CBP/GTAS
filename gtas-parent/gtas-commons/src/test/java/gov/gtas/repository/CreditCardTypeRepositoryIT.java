package gov.gtas.repository;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.lookup.CreditCardType;
import gov.gtas.model.lookup.CreditCardTypeRestore;
import gov.gtas.vo.lookup.CreditCardTypeVo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.lang.Iterable;
import java.util.Iterator;
import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@Transactional
@Rollback
public class CreditCardTypeRepositoryIT {

	@Autowired
	private CreditCardTypeRepository cctrepo;
	@Autowired
	private CreditCardTypeRepositoryCustom cctrepocustom;

	private CreditCardType creditCardType;
	private CreditCardTypeRestore creditCardTypeRestore;
	private CreditCardTypeVo creditCardTypeVo;


	@Before
	public void setup() {
		creditCardType = new CreditCardType();
		creditCardType.setId(2L);
		creditCardType.setOriginId(3L);
		creditCardType.setCode("__");
		creditCardType.setDescription("new credit card network");

//		CreditCardTypeRestore creditCardTypeRestore = new CreditCardTypeRestore();
//		creditCardTypeRestore.setActive(true);
//		creditCardTypeRestore.setId(1L);
//		creditCardTypeRestore.setName("Test");
	}

	@Test
	public void testRestoreOfNewTypeReturnsUnchanged() {
		cctrepo.save(creditCardType);
		CreditCardType restored = cctrepocustom.restore(creditCardType);

		assertEquals(creditCardType.getOriginId(), restored.getOriginId());
		assertEquals(creditCardType.getCode(), restored.getCode());
	}


	@Test
	public void testRestoreOfExistingRestoresOriginalValues() {
		Iterable<CreditCardType> existinglist = cctrepo.findAll();
		CreditCardType existing = existinglist.iterator().next();

		assertNotNull(existing);

		CreditCardType updated = existing;
		updated.setCode("__");

		cctrepo.save(updated);
		CreditCardType restored = cctrepocustom.restore(updated);

		assertEquals(restored.getCode(), existing.getCode());
	}

//	@Test
//	public void testRestoreAll() {
//		Iterator<CreditCardType> existing = cctrepo.findAll().iterator();
//
//		int idx = 0;
//		while(existing.hasNext()) {
//			idx++;
//			existing.next();
//		}
//
//		cctrepo.save(creditCardType);
//		cctrepocustom.restoreAll();
//
//		Iterator<CreditCardType> updated = cctrepo.findAll().iterator();
//
//		int updatedidx = 0;
//		while(updated.hasNext()) {
//			updatedidx++;
//			updated.next();
//		}
//
//		assertEquals(idx, updatedidx);
//	}
}
