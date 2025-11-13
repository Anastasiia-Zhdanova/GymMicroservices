package com.company.gym.mapper;

import com.company.gym.dto.response.TraineeProfileResponse;
import com.company.gym.dto.response.TraineeShortResponse;
import com.company.gym.dto.response.TrainerShortResponse;
import com.company.gym.dto.response.TrainingListResponse;
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
public class TraineeMapperTest {

    @Spy
    private TraineeMapper traineeMapper = org.mapstruct.factory.Mappers.getMapper(TraineeMapper.class);

    @Mock
    private TrainerMapper trainerMapper;

    private Trainee mockTrainee;
    private Trainer mockTrainer;
    private Training mockTraining;
    private User mockTraineeUser;
    private User mockTrainerUser;

    @BeforeEach
    void setUp() {
        traineeMapper.trainerMapper = trainerMapper;

        mockTraineeUser = new User();
        mockTraineeUser.setUsername("trainee.user");
        mockTraineeUser.setFirstName("John");
        mockTraineeUser.setLastName("Doe");
        mockTraineeUser.setIsActive(true);

        mockTrainerUser = new User();
        mockTrainerUser.setUsername("trainer.one");
        mockTrainerUser.setFirstName("Jane");
        mockTrainerUser.setLastName("Smith");

        mockTrainer = new Trainer();
        mockTrainer.setUser(mockTrainerUser);

        mockTrainee = new Trainee();
        mockTrainee.setUser(mockTraineeUser);
        mockTrainee.setTrainers(Set.of(mockTrainer));

        mockTraining = new Training();
        mockTraining.setTrainingName("Cardio");
        mockTraining.setTrainingDate(new Date());
        mockTraining.setTrainingDuration(60);
        mockTraining.setTrainer(mockTrainer);
        mockTraining.setTrainee(mockTrainee);
        mockTraining.setTrainingType(new TrainingType("Fitness"));
    }

    @Test
    void toTraineeProfileResponse_MapsSuccessfully() {
        TrainerShortResponse mockShortTrainer = new TrainerShortResponse();
        when(trainerMapper.toTrainerShortResponse(any(Trainer.class))).thenReturn(mockShortTrainer);

        TraineeProfileResponse response = traineeMapper.toTraineeProfileResponse(mockTrainee);

        assertNotNull(response);
        assertEquals("trainee.user", response.getUsername());
    }

    @Test
    void toTraineeProfileResponse_HandlesNullInput() {
        TraineeProfileResponse result = traineeMapper.toTraineeProfileResponse(null);

        assertNull(result, "Should return null when the input Trainee entity is null.");
    }

    @Test
    void toTraineeShortResponseList_MapsNonEmptyList() {
        List<Trainee> trainees = List.of(mockTrainee, mockTrainee);

        List<TraineeShortResponse> result = traineeMapper.toTraineeShortResponseList(trainees);

        assertNotNull(result, "Should return a non-null list for non-null input.");
        assertEquals(2, result.size());
    }

    @Test
    void toTraineeShortResponseList_HandlesNullInput() {
        List<TraineeShortResponse> result = traineeMapper.toTraineeShortResponseList(null);

        assertNull(result, "Should return null when the input list of Trainees is null.");
    }

    @Test
    void mapTrainersSetToShortList_MapsNonEmptySet() {
        TrainerShortResponse mockShortTrainer = new TrainerShortResponse();
        when(trainerMapper.toTrainerShortResponse(any(Trainer.class))).thenReturn(mockShortTrainer);

        List<TrainerShortResponse> result = traineeMapper.mapTrainersSetToShortList(Set.of(mockTrainer));

        assertFalse(result.isEmpty());
    }

    @Test
    void mapTrainersSetToShortList_HandlesNullSet() {
        List<TrainerShortResponse> result = traineeMapper.mapTrainersSetToShortList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toTrainingListResponseList_MapsNonEmptyList() {
        List<Training> trainings = List.of(mockTraining, mockTraining);

        List<TrainingListResponse> result = traineeMapper.toTrainingListResponse((List) trainings);

        assertNotNull(result, "Should return a non-null list for non-null input.");
        assertEquals(2, result.size(), "The size of the output list must match the input size.");
    }

    @Test
    void toTrainingListResponseList_HandlesNullInput() {
        List<TrainingListResponse> result = traineeMapper.toTrainingListResponse((List) null);

        assertNull(result, "Should return null when the input list of trainings is null.");
    }

    @Test
    void toTraineeShortResponse_MapsUserFields() {
        TraineeShortResponse response = traineeMapper.toTraineeShortResponse(mockTrainee);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
    }

    @Test
    void toTrainingListResponse_MapsAndConcatenatesTrainerName() {
        TrainingListResponse response = traineeMapper.toTrainingListResponse(mockTraining);

        assertEquals("Jane Smith", response.getAssociatedUserName());
    }
}