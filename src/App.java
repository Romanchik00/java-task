import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.out;

public class App {

    enum Status {
        PASSED, FAILED, SKIPPED
    }

    record TestResult(String name, Status status, long durationMs) {
    }

    static List<TestResult> run;

    ///////////////////////////////////////////////////////////

    /*
     * Печать сводки
     *
     * === Сводка прогона ===
     * PASSED (4): LoginTest, CreateTaskTest, CommentTest, LogoutTest
     * FAILED (2): UpdateStatusTest, DeleteTaskTest
     * SKIPPED (1): StatsTest
     * Топ-3 долгих: UpdateStatusTest, DeleteTaskTest, CreateTaskTest
     * Среднее время прошедших: 382.5 ms
     */
    static void printSummary(List<TestResult> run) {
        out.println("=== Сводка прогона ===");
        testsByStatus(run).forEach((key, val) -> {
            out.printf("%s (%d): ", key, val.size());
            out.println(
                    val.stream()
                            .collect(
                                    Collectors.joining(", ", "[", "]")));
        });
        out.print("Топ-3 самых долгих: ");
        out.println(
                longestTests(run).stream()
                        .collect(Collectors.joining(", ")));
        out.print("Среднее время пройденных: ");
        out.printf("%.3f", averageTimeOfCompleted(run));
        out.println(" ms");
    }

    // Test names by status -- Map<Status, List<String>> -- PASSED → [LoginTest,
    // ...], FAILED → [...]
    static Map<Status, List<String>> testsByStatus(List<TestResult> run) {
        return run.stream()
                .collect(
                        Collectors.groupingBy(
                                TestResult::status, Collectors.mapping(
                                        TestResult::name, Collectors.toList())));
    }

    // Three longest tests -- список имён по убыванию времени, максимум три
    static List<String> longestTests(List<TestResult> run) {
        return run.stream()
                .sorted((x, y) -> Long.compare(y.durationMs(), x.durationMs()))
                .map(TestResult::name)
                .limit(3)
                .collect(Collectors.toList());
    }

    // Average time of completed tests -- double, только PASSED
    static Double averageTimeOfCompleted(List<TestResult> run) {
        return run.stream()
                .collect(
                        Collectors.averagingDouble(
                                TestResult::durationMs));
    }

    /*
     * № Требование
     * 1 T1 - через Collectors.groupingBy, при этом в значениях - имена тестов, а не
     * записи (понадобится mapping в downstream).
     * 2 T2 - сортировка стримом, без Collections.sort и без массивов.
     * 3 T3 - одним стримом: averagingLong или summaryStatistics.
     * 4 Во всём решении ни одного цикла for и while.
     */
    public static void main(String[] args) throws Exception {

        run = List.of(
                new TestResult("LoginTest", Status.PASSED, 120),
                new TestResult("CreateTaskTest", Status.PASSED, 940),
                new TestResult("UpdateStatusTest", Status.FAILED, 2100),
                new TestResult("CommentTest", Status.PASSED, 380),
                new TestResult("DeleteTaskTest", Status.FAILED, 760),
                new TestResult("StatsTest", Status.SKIPPED, 0),
                new TestResult("LogoutTest", Status.PASSED, 90));

        printSummary(run);
    }
}
