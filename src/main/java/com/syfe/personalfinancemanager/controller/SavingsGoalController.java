package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.common.MessageResponse;
import com.syfe.personalfinancemanager.dto.goal.CreateGoalRequest;
import com.syfe.personalfinancemanager.dto.goal.GoalListResponse;
import com.syfe.personalfinancemanager.dto.goal.GoalResponse;
import com.syfe.personalfinancemanager.dto.goal.UpdateGoalRequest;
import com.syfe.personalfinancemanager.service.SavingsGoalService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes savings-goal operations for authenticated users.
 */
@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    /**
     * Creates a savings goal for the authenticated user.
     *
     * @param request validated goal data
     * @param authentication current authenticated user
     * @return the created goal with its calculated progress
     */
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid@RequestBody CreateGoalRequest request,
            Authentication authentication) {

        GoalResponse response =
                savingsGoalService.createGoal(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.status(201).body(response);
    }

    /**
     * Lists all savings goals owned by the authenticated user.
     *
     * @param authentication current authenticated user
     * @return the user's goals with calculated progress
     */
    @GetMapping
    public ResponseEntity<GoalListResponse> getGoals(
            Authentication authentication) {

        List<GoalResponse> goals =
                savingsGoalService.getGoals(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                new GoalListResponse(goals)
        );
    }

    /**
     * Retrieves one savings goal owned by the authenticated user.
     *
     * @param id goal identifier
     * @param authentication current authenticated user
     * @return the requested goal with calculated progress
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(
            @PathVariable Long id,
            Authentication authentication) {

        GoalResponse response =
                savingsGoalService.getGoal(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Updates a savings goal owned by the authenticated user.
     *
     * @param id goal identifier
     * @param request validated goal updates
     * @param authentication current authenticated user
     * @return the updated goal with calculated progress
     */
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid@RequestBody UpdateGoalRequest request,
            Authentication authentication) {

        GoalResponse response =
                savingsGoalService.updateGoal(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a savings goal owned by the authenticated user.
     *
     * @param id goal identifier
     * @param authentication current authenticated user
     * @return a successful deletion response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGoal(
            @PathVariable Long id,
            Authentication authentication) {

        savingsGoalService.deleteGoal(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                new MessageResponse(
                        "Goal deleted successfully"
                )
        );
    }
}
