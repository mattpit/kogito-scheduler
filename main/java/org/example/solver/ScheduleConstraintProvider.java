package org.example.solver;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.example.domain.Job;
import org.example.domain.Strategy;

public class ScheduleConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
            dependencyOrder(factory),
            meetDeadline(factory),
            prioritizeEarlyFinish(factory),
            smoothResourceConsumption(factory)
        };
    }

    private Constraint dependencyOrder(ConstraintFactory factory) {
        return factory.fromUniquePair(Job.class,
                Joiners.filtering((a,b) -> a.dependsOn(b)
                    && a.getTimeslot() != null && b.getTimeslot() != null
                    && parseHour(a.getTimeslot().getStart()) < parseHour(b.getTimeslot().getEnd())))
            .penalize("Dependency order", HardSoftScore.ONE_HARD);
    }

    private Constraint meetDeadline(ConstraintFactory factory) {
        return factory.from(Job.class)
            .filter(j -> j.getTimeslot() != null && parseHour(j.getTimeslot().getEnd()) > j.getDeadlineHour())
            .penalize("Misses deadline", HardSoftScore.ONE_HARD);
    }

    private Constraint prioritizeEarlyFinish(ConstraintFactory factory) {
        return factory.from(Job.class).filter(j -> j.getTimeslot() != null)
            .join(factory.from(Strategy.class))
            .reward("Finish earlier", HardSoftScore.ONE_SOFT,
                (j, s) -> s.getSpeedWeight() * (12 - parseHour(j.getTimeslot().getEnd())));
    }

    private Constraint smoothResourceConsumption(ConstraintFactory factory) {
        return factory.fromUniquePair(Job.class, Joiners.equal(Job::getTimeslot))
            .join(factory.from(Strategy.class))
            .penalize("Resource burst", HardSoftScore.ONE_SOFT,
                (j1, j2, s) -> s.getSmoothnessWeight() * ((j1.getCpu()+j2.getCpu()) + (j1.getMemoryGb()+j2.getMemoryGb())));
    }

    private int parseHour(String hhmm) { return Integer.parseInt(hhmm.split(":")[0]); }
}
