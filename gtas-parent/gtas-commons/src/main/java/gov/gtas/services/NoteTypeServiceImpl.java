package gov.gtas.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	public Set<NoteTypeVo> getAllNoteTypes() {
		List<NoteType> noteTypeList = noteTypeRepository.findAll();
		Set<NoteTypeVo> noteTypeVoSet = new HashSet<>();
		for(NoteType n: noteTypeList) {
			noteTypeVoSet.add(NoteTypeVo.from(n));
		}
		return noteTypeVoSet;
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
}
