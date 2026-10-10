package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.common.MessageResponse;
import com.syfe.personalfinancemanager.dto.goal.CreateGoalRequest;
import com.syfe.personalfinancemanager.dto.goal.GoalListResponse;
import com.syfe.personalfinancemanager.dto.goal.GoalResponse;
import com.syfe.personalfinancemanager.dto.goal.UpdateGoalRequest;
import com.syfe.personalfinancemanager.service.SavingsGoalService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavingsGoalControllerTest {

    @Mock
    private SavingsGoalService savingsGoalService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SavingsGoalController savingsGoalController;

    private GoalResponse createResponse() {

        return new GoalResponse(
                1L,
                "Emergency Fund",
                new BigDecimal("10000"),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                new BigDecimal("4000"),
                new BigDecimal("40.0"),
                new BigDecimal("6000")
        );
    }

    @Test
    void createGoalSuccess() {

        CreateGoalRequest request =
                new CreateGoalRequest();

        GoalResponse serviceResponse =
                createResponse();

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(savingsGoalService.createGoal(
                request,
                "user@example.com"
        )).thenReturn(serviceResponse);

        ResponseEntity<GoalResponse> response =
                savingsGoalController.createGoal(
                        request,
                        authentication
                );

        assertEquals(201, response.getStatusCode().value());

        assertEquals(
                "Emergency Fund",
                response.getBody().getGoalName()
        );
    }

    @Test
    void getGoalsSuccess() {

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(savingsGoalService.getGoals(
                "user@example.com"
        )).thenReturn(
                List.of(createResponse())
        );

        ResponseEntity<GoalListResponse> response =
                savingsGoalController.getGoals(
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                1,
                response.getBody()
                        .getGoals()
                        .size()
        );
    }

    @Test
    void getGoalSuccess() {

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(savingsGoalService.getGoal(
                1L,
                "user@example.com"
        )).thenReturn(createResponse());

        ResponseEntity<GoalResponse> response =
                savingsGoalController.getGoal(
                        1L,
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                "Emergency Fund",
                response.getBody().getGoalName()
        );
    }

    @Test
    void updateGoalSuccess() {

        UpdateGoalRequest request =
                new UpdateGoalRequest();

        request.setTargetAmount(
                new BigDecimal("12000")
        );

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(savingsGoalService.updateGoal(
                1L,
                request,
                "user@example.com"
        )).thenReturn(createResponse());

        ResponseEntity<GoalResponse> response =
                savingsGoalController.updateGoal(
                        1L,
                        request,
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteGoalSuccess() {

        when(authentication.getName())
                .thenReturn("user@example.com");

        ResponseEntity<MessageResponse> response =
                savingsGoalController.deleteGoal(
                        1L,
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                "Goal deleted successfully",
                response.getBody().getMessage()
        );

        verify(savingsGoalService)
                .deleteGoal(
                        1L,
                        "user@example.com"
                );
    }
}