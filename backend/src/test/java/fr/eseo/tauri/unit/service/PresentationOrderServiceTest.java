package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.PresentationOrder;
import fr.eseo.tauri.model.Student;
import fr.eseo.tauri.repository.PresentationOrderRepository;
import fr.eseo.tauri.service.PresentationOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class PresentationOrderServiceTest {

    @Mock
    private PresentationOrderRepository presentationOrderRepository;


    @InjectMocks
    private PresentationOrderService presentationOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPresentationOrderByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(presentationOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> presentationOrderService.getPresentationOrderById(id));
    }

    @Test
    void getAllPresentationOrdersByProjectShouldReturnOrdersWhenAuthorizedAndProjectExists() {
        Integer projectId = 1;
        List<PresentationOrder> presentationOrders = Arrays.asList(new PresentationOrder(), new PresentationOrder());

        when(presentationOrderRepository.findAllByProject(projectId)).thenReturn(presentationOrders);

        List<PresentationOrder> result = presentationOrderService.getAllPresentationOrdersByProject(projectId);

        assertEquals(presentationOrders, result);
    }

    @Test
    void createPresentationOrderShouldSaveOrderWhenAuthorized() {
        PresentationOrder presentationOrder = new PresentationOrder();


        presentationOrderService.createPresentationOrder(presentationOrder);

        verify(presentationOrderRepository, times(1)).save(presentationOrder);
    }

    @Test
    void deleteAllPresentationOrdersByProjectShouldDeleteOrdersWhenAuthorizedAndProjectExists() {
        Integer projectId = 1;

        presentationOrderService.deleteAllPresentationOrdersByProject(projectId);

        verify(presentationOrderRepository, times(1)).deleteAllByProject(projectId);
    }

    @Test
    void updatePresentationOrderShouldUpdateValueWhenIdExistsAndValueIsProvided() {
        Integer id = 1;
        PresentationOrder existingPresentationOrder = new PresentationOrder();
        PresentationOrder updatedPresentationOrder = new PresentationOrder();
        updatedPresentationOrder.value(1);

        when(presentationOrderRepository.findById(id)).thenReturn(Optional.of(existingPresentationOrder));

        presentationOrderService.updatePresentationOrder(id, updatedPresentationOrder);

        assertEquals(updatedPresentationOrder.value(), existingPresentationOrder.value());
        verify(presentationOrderRepository, times(1)).save(existingPresentationOrder);
    }

    @Test
    void updatePresentationOrderShouldNotUpdateValueWhenIdExistsAndValueIsNotProvided() {
        Integer id = 1;
        PresentationOrder existingPresentationOrder = new PresentationOrder();
        existingPresentationOrder.value(1);
        PresentationOrder updatedPresentationOrder = new PresentationOrder();

        when(presentationOrderRepository.findById(id)).thenReturn(Optional.of(existingPresentationOrder));

        presentationOrderService.updatePresentationOrder(id, updatedPresentationOrder);

        assertEquals(1, existingPresentationOrder.value());
        verify(presentationOrderRepository, times(1)).save(existingPresentationOrder);
    }

    @Test
    void updatePresentationOrderShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;
        PresentationOrder updatedPresentationOrder = new PresentationOrder();

        when(presentationOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> presentationOrderService.updatePresentationOrder(id, updatedPresentationOrder));
    }

    @Test
    void deletePresentationOrderShouldDeleteOrderWhenIdExists() {
        Integer id = 1;
        PresentationOrder existingPresentationOrder = new PresentationOrder();

        when(presentationOrderRepository.findById(id)).thenReturn(Optional.of(existingPresentationOrder));

        presentationOrderService.deletePresentationOrder(id);

        verify(presentationOrderRepository, times(1)).deleteById(id);
    }

    @Test
    void deletePresentationOrderShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(presentationOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> presentationOrderService.deletePresentationOrder(id));
    }

    @Test
    void getPresentationOrderByTeamIdAndSprintIdShouldReturnOrdersWhenTeamIdAndSprintIdExist() {
        Integer teamId = 1;
        Integer sprintId = 1;
        List<PresentationOrder> presentationOrders = Arrays.asList(new PresentationOrder(), new PresentationOrder());

        when(presentationOrderRepository.findByTeamIdAndSprintId(teamId, sprintId)).thenReturn(presentationOrders);

        List<PresentationOrder> result = presentationOrderService.getPresentationOrderByTeamIdAndSprintId(teamId, sprintId);

        assertEquals(presentationOrders, result);
    }

    @Test
    void getPresentationOrderByTeamIdAndSprintIdShouldReturnEmptyListWhenNoOrdersExist() {
        Integer teamId = 1;
        Integer sprintId = 1;
        List<PresentationOrder> presentationOrders = Collections.emptyList();

        when(presentationOrderRepository.findByTeamIdAndSprintId(teamId, sprintId)).thenReturn(presentationOrders);

        List<PresentationOrder> result = presentationOrderService.getPresentationOrderByTeamIdAndSprintId(teamId, sprintId);

        assertTrue(result.isEmpty());
    }

    @Test
    void updatePresentationOrderByTeamIdAndSprintIdShouldUpdateOrderValuesWhenMatchingStudentsExist() {
        Integer teamId = 1;
        Integer sprintId = 1;
        Student student = new Student();
        student.id(1);
        PresentationOrder order = new PresentationOrder();
        order.student(student);
        List<PresentationOrder> orders = Collections.singletonList(order);
        List<Student> students = Collections.singletonList(student);

        when(presentationOrderService.getPresentationOrderByTeamIdAndSprintId(teamId, sprintId)).thenReturn(orders);

        presentationOrderService.updatePresentationOrderByTeamIdAndSprintId(teamId, sprintId, students);

        assertEquals(1, order.value());
        verify(presentationOrderRepository, times(1)).save(order);
    }

    @Test
    void updatePresentationOrderByTeamIdAndSprintIdShouldNotUpdateOrderValuesWhenNoMatchingStudentsExist() {
        Integer teamId = 1;
        Integer sprintId = 1;
        Student student1 = new Student();
        student1.id(1);
        Student student2 = new Student();
        student2.id(2);
        PresentationOrder order = new PresentationOrder();
        order.student(student1);
        List<PresentationOrder> orders = Collections.singletonList(order);
        List<Student> students = Collections.singletonList(student2);

        when(presentationOrderService.getPresentationOrderByTeamIdAndSprintId(teamId, sprintId)).thenReturn(orders);

        presentationOrderService.updatePresentationOrderByTeamIdAndSprintId(teamId, sprintId, students);

        assertNull(order.value());
        verify(presentationOrderRepository, times(0)).save(order);
    }

}
