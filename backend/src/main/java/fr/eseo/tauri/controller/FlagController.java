package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Flag;
import fr.eseo.tauri.model.enumeration.FlagType;
import fr.eseo.tauri.service.FlagService;
import fr.eseo.tauri.util.CustomLogger;
import fr.eseo.tauri.util.ResponseMessage;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flags")
@Tag(name = "flags")
public class FlagController {

    private final FlagService flagService;
    private final ResponseMessage responseMessage = new ResponseMessage("flag");

    @GetMapping("/{id}")
    public ResponseEntity<Flag> getFlagById(@PathVariable Integer id) {
        Flag flag = flagService.getFlagById(id);
        return ResponseEntity.ok(flag);
    }

    @GetMapping
    public ResponseEntity<List<Flag>> getAllFlagsByProject(@RequestParam("projectId") Integer projectId) {
        List<Flag> flags = flagService.getAllFlagsByProject(projectId);
        return ResponseEntity.ok(flags);
    }

    @PostMapping
    public ResponseEntity<String> createFlag(@Validated(Create.class) @RequestBody Flag flag) {
        flagService.createFlag(flag);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateFlag(@PathVariable Integer id, @Validated(Update.class)@RequestBody Flag updatedFlag) {
        flagService.updateFlag(id, updatedFlag);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFlag(@PathVariable Integer id) {
        flagService.deleteFlag(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllFlagsByProject(@RequestParam("projectId") Integer projectId) {
        flagService.deleteAllFlagsByProject(projectId);
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }

	@GetMapping("/author/{authorId}/type/{type}")
	public ResponseEntity<List<Flag>> getFlagsByAuthorAndType(@PathVariable Integer authorId, @PathVariable FlagType type){
		List<Flag> flags = flagService.getFlagsByAuthorAndType(authorId, type);
        return ResponseEntity.ok(flags);
	}

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Flag>> getFlagsByConcernedTeamId(@PathVariable Integer teamId) {
        List<Flag> flags = flagService.getFlagsByConcernedTeamId(teamId);
        return ResponseEntity.ok(flags);
    }
}