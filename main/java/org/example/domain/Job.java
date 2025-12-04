package org.example.domain;

import java.util.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@PlanningEntity
public class Job {
    @PlanningId
    private Long id;
    private String name;
    private int durationMinutes;
    private int cpu;
    private int memoryGb;
    private int deadlineHour;
    private String status = "READY";
    private int priority = 0;

    private List<Long> dependencyIds = new ArrayList<>();
    private Timeslot timeslot;

    public Job() {}
    public Job(Long id, String name, int durationMinutes, int cpu, int memoryGb, int deadlineHour) {
        this.id = id; this.name = name; this.durationMinutes = durationMinutes;
        this.cpu = cpu; this.memoryGb = memoryGb; this.deadlineHour = deadlineHour;
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    public Timeslot getTimeslot() { return timeslot; }
    public void setTimeslot(Timeslot t) { this.timeslot = t; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getCpu() { return cpu; }
    public int getMemoryGb() { return memoryGb; }
    public int getDeadlineHour() { return deadlineHour; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public int getPriority() { return priority; }
    public void setPriority(int p) { this.priority = p; }

    public List<Long> getDependencyIds() { return dependencyIds; }
    public void setDependencyIds(List<Long> ids) { this.dependencyIds = new ArrayList<>(ids); }

    public boolean dependsOn(Job other) {
        return other != null && dependencyIds != null && dependencyIds.contains(other.getId());
    }
}
