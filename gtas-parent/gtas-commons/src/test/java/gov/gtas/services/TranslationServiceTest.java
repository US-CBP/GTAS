package gov.gtas.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.model.Translation;
import gov.gtas.repository.TranslationRepository;
import gov.gtas.vo.HitDetailVo;

@RunWith(MockitoJUnitRunner.class)
public class TranslationServiceTest {
	
	@InjectMocks
	TranslationServiceImpl translationService;
	
	@Mock
	TranslationRepository translationRespository;
	
	
	@Before
	public void before() {
		List<Translation> translationList = new ArrayList<Translation>();
		Translation translation = new Translation();
		translation.setCode("head001");
		translation.setLanguage("en");
		translation.setTranslation("Header1");
		translationList.add(translation);
		translation = new Translation();
		translation.setCode("head002");
		translation.setLanguage("en");
		translation.setTranslation("Header2");
		translationList.add(translation);
		Mockito.when(translationRespository.getTranslationsByLang("en")).thenReturn(translationList);
		
		
	}
	
	@Test
	public void getTranslationsByLang() {

		try {

			Map<String,String> translationList = translationService.getTranslationValuesByLang("en");
			Assert.assertEquals(2, translationList.size());
			Assert.assertNotNull(translationList);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
