package com.company.gym.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "trainer")
public class Trainer {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @ManyToMany(mappedBy = "trainers", fetch = FetchType.LAZY)
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Training> trainings = new HashSet<>();

    public Trainer() {}

    public void addTrainee(Trainee trainee) {
        if (this.trainees == null) this.trainees = new HashSet<>();
        if (trainee.getTrainers() == null) trainee.setTrainers(new HashSet<>());
        this.trainees.add(trainee);
        trainee.getTrainers().add(this);
    }

    public void removeTrainee(Trainee trainee) {
        if (this.trainees != null) {
            this.trainees.remove(trainee);
        }
        if (trainee.getTrainers() != null) {
            trainee.getTrainers().remove(this);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public TrainingType getSpecialization() { return specialization; }
    public void setSpecialization(TrainingType specialization) { this.specialization = specialization; }
    public Set<Trainee> getTrainees() { return trainees; }
    public void setTrainees(Set<Trainee> trainees) { this.trainees = trainees; }
    public Set<Training> getTrainings() { return trainings; }
    public void setTrainings(Set<Training> trainings) { this.trainings = trainings; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(id, trainer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}