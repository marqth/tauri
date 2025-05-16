package fr.eseo.tauri.service;

import fr.eseo.tauri.model.ValidationBonus;
import fr.eseo.tauri.repository.ValidationBonusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidationBonusService {

    private final ValidationBonusRepository validationBonusRepository;
    private final UserService userService;
    @Lazy
    private final BonusService bonusService;

    public ValidationBonus getValidationBonusByAuthorId(Integer bonusId, Integer authorId) {
        return validationBonusRepository.findByAuthorIdAndBonusId(bonusId, authorId);
    }

    public List<ValidationBonus> getValidationByAuthorId(Integer authorId) {
        return validationBonusRepository.getValidationByAuthorId(authorId);
    }

    public List<ValidationBonus> getAllValidationBonuses(Integer bonusId) {
        return validationBonusRepository.findAllByBonusId(bonusId);
    }

    public void createValidationBonus(ValidationBonus validationBonus) {
        validationBonus.bonus(bonusService.getBonusById(validationBonus.bonusId()));
        validationBonus.author(userService.getUserById(validationBonus.authorId()));

        validationBonusRepository.save(validationBonus);
    }

    public void deleteAllValidationBonuses(Integer bonusId) {
        validationBonusRepository.deleteAllByBonusId(bonusId);

    }

}