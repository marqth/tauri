package fr.eseo.tauri.service;

import fr.eseo.tauri.model.PresentationOrder;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.Student;
import fr.eseo.tauri.repository.PresentationOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresentationOrderService {

    private final PresentationOrderRepository presentationOrderRepository;

    public PresentationOrder getPresentationOrderById(Integer id) {
        return presentationOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("presentationOrder", id));
    }

    public List<PresentationOrder> getAllPresentationOrdersByProject(Integer projectId) {
        return presentationOrderRepository.findAllByProject(projectId);
    }

    public void createPresentationOrder(PresentationOrder presentationOrder) {
        presentationOrderRepository.save(presentationOrder);
    }

    public void updatePresentationOrder(Integer id, PresentationOrder updatedPresentationOrder) {
        PresentationOrder presentationOrder = getPresentationOrderById(id);

        if (updatedPresentationOrder.value() != null) presentationOrder.value(updatedPresentationOrder.value());

        presentationOrderRepository.save(presentationOrder);
    }

    public void deletePresentationOrder(Integer id) {
        getPresentationOrderById(id);
        presentationOrderRepository.deleteById(id);
    }

    public void deleteAllPresentationOrdersByProject(Integer projectId) {
        presentationOrderRepository.deleteAllByProject(projectId);
    }

    public List<PresentationOrder> getPresentationOrderByTeamIdAndSprintId(Integer teamId, Integer sprintId) {
        return presentationOrderRepository.findByTeamIdAndSprintId(teamId, sprintId);
    }

    public void updatePresentationOrderByTeamIdAndSprintId(Integer teamId, Integer sprintId, List<Student> students) {
        var orders = getPresentationOrderByTeamIdAndSprintId(teamId, sprintId);
		for (var order : orders) {
			Integer newValue = null;
			for (int j = 0; j < students.size(); j++) {
				if (students.get(j).id().equals(order.student().id())) {
					newValue = j + 1;
					break;
				}
			}
			if (newValue != null) {
                order.value(newValue);
				presentationOrderRepository.save(order);
			}
		}
    }

}