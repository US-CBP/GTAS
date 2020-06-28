package gov.gtas.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import gov.gtas.model.NoteType;
import gov.gtas.repository.NoteTypeRepository;
import gov.gtas.vo.NoteTypeVo;

@Component
public class NoteTypeServiceImpl implements NoteTypeService {

	private NoteTypeRepository noteTypeRepository;
	
	public NoteTypeServiceImpl(NoteTypeRepository noteTypeRepository) {
		this.noteTypeRepository = noteTypeRepository;
	}
	
	@Override
	public List<NoteTypeVo> getAllNoteTypes() {
		List<NoteType> noteTypeList = noteTypeRepository.findAll();
		List<NoteTypeVo> noteTypeVoList = new ArrayList<>();
		for(NoteType n: noteTypeList) {
			noteTypeVoList.add(NoteTypeVo.from(n));
		}
		noteTypeVoList.sort(Comparator.comparing(NoteTypeVo::getId));
		return noteTypeVoList;
	}


	@Override
	public void saveNoteType(NoteTypeVo noteTypeVo) {
		NoteType noteType = NoteType.from(noteTypeVo);
		noteTypeRepository.save(noteType);
	}

	@Override
	public void deleteNoteTypes(Long id) {
		noteTypeRepository.deleteById(id);
	}

	@Override
	public NoteType getDeletedNoteType() {
		return noteTypeRepository.findByType("DELETED").orElseThrow(RuntimeException::new);
	}
}
