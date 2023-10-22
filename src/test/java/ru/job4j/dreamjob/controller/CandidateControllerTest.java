package ru.job4j.dreamjob.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    @Mock
    private CandidateService candidateService;

    @Mock
    private CityService cityService;

    @InjectMocks
    private CandidateController candidateController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        MockitoAnnotations.openMocks(this);
        testFile = new MockMultipartFile("testFile", new byte[] {1, 2});
    }

    @Test
    public void whenRequestCandidateListPageThenGetPageWithCandidates() {
        Candidate candidate1 = new Candidate(1, "test1", "desc1", now(), 1, 2);
        Candidate candidate2 = new Candidate(2, "test1", "desc1", now(), 1, 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        var model = new ConcurrentModel();

        when(cityService.findAll()).thenReturn(expectedCities);

        var view = candidateController.getCreationPage(model);
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        var model = new ConcurrentModel();

        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);

        var view = candidateController.create(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        var model = new ConcurrentModel();

        when(candidateService.save(any(), any())).thenThrow(expectedException);

        var view = candidateController.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenGetVacancyByIdThenReturnSameVacancyAndGetVacanciesOnePage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        ConcurrentModel model = new ConcurrentModel();

        when(candidateService.findById(anyInt())).thenReturn(Optional.of(candidate));
        when(cityService.findAll()).thenReturn(expectedCities);

        String view = candidateController.getById(candidate.getId(), model);
        Assertions.assertThat(view).isEqualTo("candidates/one");

        var actualCities = model.getAttribute("cities");
        assertThat(actualCities).isEqualTo(expectedCities);

        var actualVacancy = model.getAttribute("candidate");
        assertThat(actualVacancy).isEqualTo(candidate);
    }

    @Test
    public void whenUpdateVacancyThenReturnUpdatedVacancyAndRedirectToVacanciesPage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        ConcurrentModel model = new ConcurrentModel();

        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        String view = candidateController.update(candidate, testFile, model);
        assertThat(view).isEqualTo("redirect:/candidates");

        assertThat(candidateArgumentCaptor.getValue()).isEqualTo(candidate);
        assertThat(fileDtoArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenUpdateDoesNotExistVacancyThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        ConcurrentModel model = new ConcurrentModel();

        when(candidateService.update(any(), any())).thenReturn(false);

        String view = candidateController.update(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenDeleteBuIdVacancyThenReturnTrueAndRedirectToVacanciesPage() {
        ConcurrentModel model = new ConcurrentModel();
        when(candidateService.deleteById(anyInt())).thenReturn(true);

        String view = candidateController.delete(1, model);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteBuIdIfDoesNotExistVacancyThenReturnFalseAndGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        ConcurrentModel model = new ConcurrentModel();

        when(candidateService.deleteById(anyInt())).thenReturn(false);

        String view = candidateController.delete(1, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }
}