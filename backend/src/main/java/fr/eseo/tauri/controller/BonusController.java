package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Bonus;
import fr.eseo.tauri.model.ValidationBonus;
import fr.eseo.tauri.service.BonusService;
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
@RequestMapping("/api/bonuses")
@Tag(name = "bonuses")
public class BonusController {

    private final BonusService bonusService;
    private final ResponseMessage responseMessage = new ResponseMessage("bonus");

    /**
     * Get a bonus by its id
     * @param id the id of the bonus
     * @return the bonus
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bonus> getBonusById(@PathVariable Integer id) {
        Bonus bonus = bonusService.getBonusById(id);
        return ResponseEntity.ok(bonus);
    }

    /**
     * Get all bonuses by project
     * @param projectId the id of the project
     * @return the list of bonuses
     */
    @GetMapping
    public ResponseEntity<List<Bonus>> getAllBonusesByProject(@RequestParam("projectId") Integer projectId) {
        List<Bonus> bonuses = bonusService.getAllBonusesByProject(projectId);
        return ResponseEntity.ok(bonuses);
    }

    /**
     * Create a bonus
     * @param bonus the bonus to create
     * @return a message
     */
    @PostMapping
    public ResponseEntity<String> createBonus(@Validated(Create.class) @RequestBody Bonus bonus) {
        bonusService.createBonus(bonus);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    /**
     * Update a bonus
     * @param id the id of the bonus
     * @param updatedBonus the bonus to update
     * @return a message
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateBonus(@PathVariable Integer id, @Validated(Update.class)@RequestBody Bonus updatedBonus) {
        bonusService.updateBonus(id, updatedBonus);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    /**
     * Delete a bonus
     * @param id the id of the bonus
     * @return a message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBonus(@PathVariable Integer id) {
        bonusService.deleteBonus(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    /**
     * Delete all bonuses
     * @param projectId the id of the project
     * @return a message
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAllBonusesByProject(@RequestParam("projectId") Integer projectId) {
        bonusService.deleteAllBonusesByProject(projectId);
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }


    @GetMapping("/teams/{teamId}")
    public ResponseEntity<List<ValidationBonus>> getValidationBonusesByTeam(@PathVariable Integer teamId) {
        List<ValidationBonus> bonuses = bonusService.getValidationBonusesByTeam(teamId);
        return ResponseEntity.ok(bonuses);
    }

    @PatchMapping("/teams/{teamId}/{sprintId}/{userId}")
    public ResponseEntity<String> setValidationBonusesByTeam(@PathVariable Integer teamId, @PathVariable Integer sprintId, @PathVariable Integer userId) {
        bonusService.setValidationBonusesByTeam(teamId, sprintId, userId);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

}
