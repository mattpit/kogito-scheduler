package org.example.solver;

import java.util.List;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.example.domain.Job;
import org.example.domain.Timeslot;
import org.example.domain.Strategy;

@PlanningSolution
public class ScheduleSolution {
    private List<Timeslot> timeslotList;
    private List<Job> jobList;
    private Strategy strategy;
    private HardSoftScore score;

    @ValueRangeProvider(id = "timeslotRange")
    @ProblemFactCollectionProperty
    public List<Timeslot> getTimeslotList() { return timeslotList; }
    public void setTimeslotList(List<Timeslot> l) { this.timeslotList = l; }

    @PlanningEntityCollectionProperty
    public List<Job> getJobList() { return jobList; }
    public void setJobList(List<Job> l) { this.jobList = l; }

    @ProblemFactProperty
    public Strategy getStrategy() { return strategy; }
    public void setStrategy(Strategy s) { this.strategy = s; }

    @PlanningScore
    public HardSoftScore getScore() { return score; }
    public void setScore(HardSoftScore s) { this.score = s; }
}
