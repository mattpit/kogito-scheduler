package org.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.domain.Job;
import org.example.domain.Timeslot;
import org.example.domain.Strategy;
import org.example.solver.ScheduleSolution;
import org.optaplanner.core.api.solver.SolverManager;

@Path("/api/schedule")
public class Application {

    @Inject
    SolverManager<ScheduleSolution, Long> solverManager;

    // In-memory cache holding the latest best solution per problemId
    private static final Map<Long, ScheduleSolution> RESULT_CACHE = new ConcurrentHashMap<>();

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }

    /**
     * Start solving asynchronously and return a problemId immediately.
     * Poll /api/schedule/result?problemId=... to get the current best solution as HTML.
     */
    @POST
    @Path("/solve")
    @Produces(MediaType.TEXT_PLAIN)
    public String solve(@QueryParam("directive") String directive) {

        // --- Build inputs (sample data) ---
        List<Timeslot> slots = List.of(
                new Timeslot("08:00", "09:00"),
                new Timeslot("09:00", "10:00"),
                new Timeslot("10:00", "11:00"),
                new Timeslot("11:00", "12:00")
        );

        List<Job> jobs = new ArrayList<>();
        Job etl = new Job(1L, "ETL-A", 60, 2, 4, 11);
        Job train = new Job(2L, "ML-Train", 120, 6, 16, 13);
        Job report = new Job(3L, "Report", 30, 1, 2, 11);
        report.setDependencyIds(List.of(1L)); // Report depends on ETL-A
        jobs.add(etl);
        jobs.add(train);
        jobs.add(report);

        Strategy strategy = Strategy.fromDirective(directive);

        // --- Create problem instance ---
        long problemId = System.currentTimeMillis();

        solverManager.solveAndListen(
                problemId,
                // Problem supplier (Function<Long, ScheduleSolution>)
                id -> {
                    ScheduleSolution problem = new ScheduleSolution();
                    problem.setTimeslotList(slots);
                    problem.setJobList(jobs);
                    problem.setStrategy(strategy);
                    return problem;
                },
                // Best solution consumer (BiConsumer<Long, ScheduleSolution>)
                (id, bestSolution) -> RESULT_CACHE.put(id, bestSolution)
        );

        // Return the problemId immediately; client can poll /result
        return String.valueOf(problemId);
    }

    /**
     * Retrieve current best solution (HTML). If not yet available, returns a simple "Solving..." page.
     */
    @GET
    @Path("/result")
    @Produces(MediaType.TEXT_HTML)
    public String result(@QueryParam("problemId") long problemId) {
        ScheduleSolution solution = RESULT_CACHE.get(problemId);
        if (solution == null) {
            return "<html><body><h3>Solving...</h3><p>No solution available yet for problemId="
                    + problemId + ".</p></body></html>";
        }
        return generateHtmlReport(solution);
    }

    // --------------------------
    // HTML Report Generator
    // --------------------------
    private String generateHtmlReport(ScheduleSolution solution) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Schedule Plan</title>")
                .append("<style>")
                .append("body{font-family:Arial, sans-serif;margin:20px;} table{border-collapse:collapse;}")
                .append("th,td{border:1px solid #ccc;padding:6px 10px;} th{background:#f7f7f7;}")
                .append("</style></head><body>");
        sb.append("<h1>Daily Scheduling Plan</h1>");
        sb.append("<table><tr>")
                .append("<th>Job</th><th>Timeslot</th><th>CPU</th><th>Memory</th><th>Deadline</th>")
                .append("</tr>");

        for (Job job : solution.getJobList()) {
            sb.append("<tr>");
            sb.append("<td>").append(escape(job.getName())).append("</td>");
            sb.append("<td>").append(job.getTimeslot() != null ? escape(job.getTimeslot().toString()) : "Unassigned").append("</td>");
            sb.append("<td>").append(job.getCpu()).append("</td>");
            sb.append("<td>").append(job.getMemoryGb()).append("</td>");
            sb.append("<td>").append(job.getDeadlineHour()).append(":00</td>");
            sb.append("</tr>");
        }

        sb.append("</table>");
        sb.append("<p><strong>Score:</strong> ").append(solution.getScore()).append("</p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
