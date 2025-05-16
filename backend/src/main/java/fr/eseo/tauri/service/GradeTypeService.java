package fr.eseo.tauri.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import fr.eseo.tauri.exception.EmptyResourceException;
import fr.eseo.tauri.model.GradeType;
import fr.eseo.tauri.model.enumeration.GradeTypeName;
import fr.eseo.tauri.repository.GradeTypeRepository;
import fr.eseo.tauri.util.CustomLogger;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GradeTypeService {

    private final GradeTypeRepository gradeTypeRepository;
    private final GradeService gradeService;
    private final ProjectService projectService;

    public GradeType getGradeTypeById(Integer id) {
        return gradeTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("gradeType", id));
    }

    public List<GradeType> getAllImportedGradeTypes(Integer projectId) {
        return gradeTypeRepository.findAllImported(projectId);
    }

    public List<GradeType> getAllUnimportedGradeTypes(Integer projectId) {
        return gradeTypeRepository.findAllUnimported(projectId);
    }

    public void createGradeType(GradeType gradeType) {
        gradeTypeRepository.save(gradeType);
    }

    public void updateGradeType(Integer id, GradeType updatedGradeType, Integer projectId) {

        GradeType gradeType = getGradeTypeById(id);
        CustomLogger.info("Updating GradeType with id " + gradeType);

        if (updatedGradeType.name() != null) gradeType.name(updatedGradeType.name());
        if (updatedGradeType.factor() != null) {
            gradeType.factor(updatedGradeType.factor());
            gradeService.updateImportedMean(projectId);
        }
        if (updatedGradeType.forGroup() != null) gradeType.forGroup(updatedGradeType.forGroup());
        if (updatedGradeType.imported() != null) gradeType.imported(updatedGradeType.imported());
        if (updatedGradeType.scaleTXTBlob() != null) gradeType.scaleTXTBlob(updatedGradeType.scaleTXTBlob());
        if (updatedGradeType.project() != null) gradeType.project(updatedGradeType.project());

        gradeTypeRepository.save(gradeType);
    }

    public void deleteGradeTypeById(Integer id) {
        getGradeTypeById(id);
        gradeTypeRepository.deleteById(id);
    }

    public void deleteAllImportedGradeTypes() {
        gradeTypeRepository.deleteAllImported();
    }

    public void deleteAllUnimportedGradeTypes() {
        gradeTypeRepository.deleteAllUnimported();
    }

    /**
     * This method is used to create a list of GradeType objects from the provided coefficients and names.
     *
     * @param coefficients the list of coefficients for the GradeType objects
     * @param names      the list of names for the GradeType objects
     * @return a list of GradeType objects created from the provided coefficients and names
     */
    public List<GradeType> generateImportedGradeTypes(List<String> coefficients, List<String> names) {
        if (coefficients == null || coefficients.isEmpty()) {
            CustomLogger.warn("The list of coefficients is null or empty");
            throw new EmptyResourceException("list of coefficients");
        }

        if(names == null || names.isEmpty()){
            CustomLogger.warn("The list of names is null or empty");
            throw new EmptyResourceException("list of names");
        }

        List<GradeType> importedGradeTypes = new ArrayList<>();

        importedGradeTypes.add(createImportedGradeType(GradeTypeName.AVERAGE.displayName(), (float) 0));

        for (int i = 0; i < coefficients.size(); i++) {
            importedGradeTypes.add(createImportedGradeType(names.get(i), Float.parseFloat(coefficients.get(i))));
        }

        CustomLogger.info("Successfully created GradeType objects from the provided coefficients and names.");
        return importedGradeTypes;
    }

    public GradeType createImportedGradeType(String name, Float factor) {
        GradeType gradeType = new GradeType();
        gradeType.name(name);
        gradeType.factor(factor);
        gradeType.forGroup(false);
        gradeType.imported(true);
        gradeType.project(projectService.getActualProject());
        return(gradeTypeRepository.save(gradeType));
    }

    /**
     * This method is used to create a list of GradeType objects from a CSV file.
     *
     * @param inputStream the InputStream from which the CSV file is read
     * @return a list of GradeType objects created from the coefficients and names in the CSV file
     */
    public List<GradeType> createGradeTypesFromCSV(InputStream inputStream) throws CsvValidationException, IOException {
        List<String> coefficients = new ArrayList<>();
        List<String> names = new ArrayList<>();
        boolean coefficientsStarted = false;
        int startingCoefficients = 1;
        int lineBrowsed = 0;

        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            lineBrowsed++;
            if (!coefficientsStarted) {
                startingCoefficients = processLineForCoefficients(nextLine, coefficients);
                coefficientsStarted = true;
            } else if (lineBrowsed == 2) {
                processLineForNames(nextLine, names, startingCoefficients);
            }
        }

        return generateImportedGradeTypes(coefficients, names);
    }

    /**
     * <b>HELPER METHOD</b>
     * This method is used to process a line from a CSV file and extract the coefficients.
     *
     * @param nextLine     the line from the CSV file as an array of strings
     * @param coefficients the list of coefficients to which the extracted coefficients are added
     * @return the starting index of the coefficients in the line
     */
    public int processLineForCoefficients(String[] nextLine, List<String> coefficients) {
        int startingCoefficients = 1;
        for (String part : nextLine) {
            String trimmedPart = part.trim();
            try {
                Float.parseFloat(trimmedPart); // Check if it's a coefficient
                coefficients.add(trimmedPart);
            } catch (NumberFormatException ignored) {
                startingCoefficients++;
            }
        }
        return startingCoefficients;
    }

    /**
     * <b>HELPER METHOD</b>
     * This method is used to process a line from a CSV file and extract the names.
     *
     * @param nextLine             the line from the CSV file as an array of strings
     * @param names              the list of names to which the extracted names are added
     * @param startingCoefficients the starting index of the coefficients in the line
     */
    public void processLineForNames(String[] nextLine, List<String> names, int startingCoefficients) {
        int index = 0;
        for (String part : nextLine) {
            index++;
            if (index >= startingCoefficients) {
                String trimmedPart = part.trim();
                names.add(trimmedPart);
            }
        }
    }


    public GradeType findByName(String name, Integer projectId) {
        return gradeTypeRepository.findByNameAndProjectId(name, projectId);
    }

    public void saveGradeScale(Integer id, MultipartFile file) throws IOException {
        if (!Objects.equals(file.getContentType(), "text/plain")) {
            CustomLogger.info("File type: " + file.getContentType());
            throw new IllegalArgumentException("Only TXT files are allowed");
        }
        if (file.getSize() > 65 * 1024) { // 65 KB in bytes
            throw new IllegalArgumentException("File size should not exceed 65 KB");
        }
            GradeType gradeType = gradeTypeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("GradeType not found"));

            byte[] txtBytes = file.getBytes();
            gradeType.scaleTXTBlob(txtBytes);
            gradeTypeRepository.save(gradeType);
    }

    public byte[] getBLOBScale(int id) {
        GradeType gradeType = gradeTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GradeType not found"));
        CustomLogger.info("Size of the PDF: " + gradeType.scaleTXTBlob().length);
        return gradeType.scaleTXTBlob();
    }

    public void deleteGradeScale(Integer id) {
        var gradeType = gradeTypeRepository.findById(id).orElseThrow();
        gradeType.scaleTXTBlob(null);
        gradeTypeRepository.save(gradeType);
    }
}