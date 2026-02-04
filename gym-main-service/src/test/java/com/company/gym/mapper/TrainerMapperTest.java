package com.company.gym.mapper;

import com.company.gym.dto.response.*;
import com.company.gym.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerMapperTest {

    @Spy
    private TrainerMapper trainerMapper = org.mapstruct.factory.Mappers.getMapper(TrainerMapper.class);

    @Mock
    private TraineeMapper traineeMapper;

    private Trainee mockTrainee;
    private Trainer mockTrainer;
    private Training mockTraining;
    private User mockTrainerUser;
    private User mockTraineeUser;
    private TrainingType mockSpecialization;

    @BeforeEach
    void setUp() {
        trainerMapper.traineeMapper = traineeMapper;

        mockTrainerUser = new User();
        mockTrainerUser.setUsername("trainer.one");
        mockTrainerUser.setFirstName("Jane");
        mockTrainerUser.setLastName("Smith");
        mockTrainerUser.setIsActive(true);

        mockTraineeUser = new User();
        mockTraineeUser.setUsername("trainee.user");
        mockTraineeUser.setFirstName("John");
        mockTraineeUser.setLastName("Doe");

        mockSpecialization = new TrainingType("Fitness");
        mockSpecialization.setId(5L);

        mockTrainee = new Trainee();
        mockTrainee.setUser(mockTraineeUser);

        mockTrainer = new Trainer();
        mockTrainer.setUser(mockTrainerUser);
        mockTrainer.setSpecialization(mockSpecialization);
        mockTrainer.setTrainees(Set.of(mockTrainee));

        mockTraining = new Training();
        mockTraining.setTrainingName("Weights");
        mockTraining.setTrainingDate(new Date());
        mockTraining.setTrainingDuration(90);
        mockTraining.setTrainee(mockTrainee);
        mockTraining.setTrainingType(mockSpecialization);
    }

    @Test
    void toTrainerProfileResponse_MapsSuccessfully() {
        TraineeShortResponse mockShortTrainee = new TraineeShortResponse();
        when(traineeMapper.toTraineeShortResponse(any(Trainee.class))).thenReturn(mockShortTrainee);

        TrainerProfileResponse response = trainerMapper.toTrainerProfileResponse(mockTrainer);

        assertNotNull(response);
        assertEquals("trainer.one", response.getUsername());
    }

    @Test
    void toTrainerProfileResponse_HandlesNullInput() {
        TrainerProfileResponse response = trainerMapper.toTrainerProfileResponse(null);

        assertNull(response, "Should return null when the input Trainer entity is null.");
    }

    @Test
    void toTrainerShortResponse_MapsUserAndSpecialization() {
        TrainerShortResponse response = trainerMapper.toTrainerShortResponse(mockTrainer);

        assertNotNull(response);
        assertEquals("trainer.one", response.getUsername());
    }

    @Test
    void toTrainerShortResponse_HandlesNullInput() {
        TrainerShortResponse response = trainerMapper.toTrainerShortResponse(null);

        assertNull(response, "Should return null when the input Trainer entity is null.");
    }

    @Test
    void toTrainerShortResponseList_MapsNonEmptyList() {
        List<Trainer> trainers = List.of(mockTrainer, mockTrainer);

        List<TrainerShortResponse> result = trainerMapper.toTrainerShortResponseList(trainers);

        assertNotNull(result, "Should return a non-null list for non-null input.");
        assertEquals(2, result.size());
    }

    @Test
    void toTrainerShortResponseList_HandlesNullInput() {
        List<TrainerShortResponse> result = trainerMapper.toTrainerShortResponseList(null);

        assertNull(result, "Should return null when the input list of Trainers is null.");
    }

    @Test
    void toTrainingListResponse_MapsSuccessfully() {
        TrainingListResponse response = trainerMapper.toTrainingListResponse(mockTraining);

        assertNotNull(response);
        assertEquals("Weights", response.getTrainingName());
    }

    @Test
    void toTrainingListResponse_HandlesNullInput() {
        TrainingListResponse result = trainerMapper.toTrainingListResponse((Training) null);

        assertNull(result, "toTrainingListResponse (single) should return null for null input.");
    }

    @Test
    void toTrainingListResponseList_MapsNonEmptyList() {
        List<Training> trainings = List.of(mockTraining, mockTraining);

        List<TrainingListResponse> result = trainerMapper.toTrainingListResponse((List) trainings);

        assertNotNull(result, "Should return a non-null list for non-null input.");
        assertEquals(2, result.size());
    }

    @Test
    void toTrainingListResponseList_HandlesNullInput() {
        List<TrainingListResponse> result = trainerMapper.toTrainingListResponse((List) null);

        assertNull(result, "Should return null when the input list of trainings is null.");
    }

    @Test
    void toTrainingTypeResponse_HandlesNullInput() {
        assertNull(trainerMapper.toTrainingTypeResponse(null),
                "toTrainingTypeResponse should return null for null input.");
    }

    @Test
    void toTrainingTypeResponseList_MapsNonEmptyList() {
        List<TrainingType> types = List.of(mockSpecialization, new TrainingType("Karate"));

        List<TrainingTypeResponse> result = trainerMapper.toTrainingTypeResponseList(types);

        assertNotNull(result, "Should return a non-null list for non-null input.");
        assertEquals(2, result.size());
    }

    @Test
    void toTrainingTypeResponseList_HandlesNullInput() {
        List<TrainingTypeResponse> result = trainerMapper.toTrainingTypeResponseList(null);

        assertNull(result, "toTrainingTypeResponseList should return null for null input list.");
    }

    @Test
    void mapTraineesSetToShortList_MapsNonEmptySet() {
        TraineeShortResponse mockShortTrainee = new TraineeShortResponse();
        when(traineeMapper.toTraineeShortResponse(any(Trainee.class))).thenReturn(mockShortTrainee);
        List<TraineeShortResponse> result = trainerMapper.mapTraineesSetToShortList(Set.of(mockTrainee));
        assertFalse(result.isEmpty());
    }

    @Test
    void mapTraineesSetToShortList_HandlesNullSet() {
        List<TraineeShortResponse> result = trainerMapper.mapTraineesSetToShortList(null);
        assertTrue(result.isEmpty());
    }
}