package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.PresentationOrder;
import fr.eseo.tauri.service.PresentationOrderService;
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
@RequestMapping("/api/presentationOrders")
@Tag(name = "presentationOrders")
public class PresentationOrderController {

    private final PresentationOrderService presentationOrderService;
    private final ResponseMessage responseMessage = new ResponseMessage("presentationOrder");

    @GetMapping("/{id}")
    public ResponseEntity<PresentationOrder> getPresentationOrderById(@PathVariable Integer id) {
        PresentationOrder presentationOrder = presentationOrderService.getPresentationOrderById(id);
        return ResponseEntity.ok(presentationOrder);
    }

    @GetMapping
    public ResponseEntity<List<PresentationOrder>> getAllPresentationOrdersByProject(@RequestParam("projectId") Integer projectId) {
        List<PresentationOrder> presentationOrders = presentationOrderService.getAllPresentationOrdersByProject(projectId);
        return ResponseEntity.ok(presentationOrders);
    }

    @PostMapping
    public ResponseEntity<String> createPresentationOrder(@Validated(Create.class) @RequestBody PresentationOrder presentationOrder) {
        presentationOrderService.createPresentationOrder(presentationOrder);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePresentationOrder(@PathVariable Integer id, @Validated(Update.class) @RequestBody PresentationOrder updatedPresentationOrder) {
        presentationOrderService.updatePresentationOrder(id, updatedPresentationOrder);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePresentationOrder(@PathVariable Integer id) {
        presentationOrderService.deletePresentationOrder(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllPresentationOrdersByProject(@RequestParam("projectId") Integer projectId) {
        presentationOrderService.deleteAllPresentationOrdersByProject(projectId);
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }

}