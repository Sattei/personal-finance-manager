package com.syfe.personalfinancemanager.dto.goal;

import java.util.List;

public class GoalListResponse {

    private List<GoalResponse> goals;

    public GoalListResponse(List<GoalResponse> goals) {
        this.goals = goals;
    }

    public List<GoalResponse> getGoals() {
        return goals;
    }
}