package gov.gtas.services;

import java.util.*;
import java.util.stream.Collectors;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(NoteTypeService.class);
	
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
	public JsonServiceResponse deleteNoteType(Long id) {
		// Make attempt to delete, due to constraints potentially on notetypes and
		// wanting to preserve history of those
		// constrained elements notetype will become archived instead
		try {
			noteTypeRepository.deleteById(id);
			logger.info("The NoteType with Id " + id + " was successfully deleted.");
		} catch (Exception e) { // TODO change to appropriate exception
			logger.info("The NoteType with id " + id + " was unable to be deleted. Attempting to archive instead");
			Optional<NoteType> tmpNoteType = noteTypeRepository.findById(id);
			if(tmpNoteType.isPresent()) {
				NoteType noteToBeArchived = tmpNoteType.get();
				noteToBeArchived.setArchived(Boolean.TRUE);
				noteTypeRepository.save(noteToBeArchived);
			}
			return new JsonServiceResponse(Status.SUCCESS_WITH_WARNING, "Failed To Delete NoteType, Archived NoteType Instead");
		}
		return new JsonServiceResponse(Status.SUCCESS, "Successfully Deleted NoteType", id);
	}

	@Override
	public JsonServiceResponse editNoteType(NoteTypeVo noteTypeVo) {
		NoteType updatedNoteType = NoteType.from(noteTypeVo);
		updatedNoteType.setId(noteTypeVo.getId());
		noteTypeRepository.save(updatedNoteType);
		return new JsonServiceResponse(Status.SUCCESS, "Successfully updated NoteType", noteTypeVo);
	}

	@Override
	public NoteType getDeletedNoteType() {
		return noteTypeRepository.findByType("DELETED").orElseThrow(RuntimeException::new);
	}

	@Override
	public List<NoteTypeVo> getAllNonArchivedNoteTypes() {
		List<NoteTypeVo> noteVos = noteTypeRepository.getNonArchivedNoteTypes().stream().map(n ->
				new NoteTypeVo().from(n)).collect(Collectors.toList());
		return noteVos;
	}
}
