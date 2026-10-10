package com.syfe.personalfinancemanager.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String goalName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false)
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public SavingsGoal() {
    }

    public SavingsGoal(
            String goalName,
            BigDecimal targetAmount,
            LocalDate targetDate,
            LocalDate startDate,
            User user) {

        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.startDate = startDate;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getGoalName() {
        return goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public User getUser() {
        return user;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}