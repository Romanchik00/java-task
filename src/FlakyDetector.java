import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlakyDetector {
    record TestLog(String name, String status) {
        public String toString() {
            return name + "|" + status;
        }
    }

    List<List<String>> runs;

    public FlakyDetector() {
        try (var logs = Files.walk(Path.of("src/log"))) {
            runs = logs
                    .filter(
                            (x) -> Files.isRegularFile(x, LinkOption.NOFOLLOW_LINKS))
                    .map(
                            (x) -> {
                                List<String> lines = Collections.emptyList();
                                try {
                                    lines = Files.readAllLines(x);
                                    return lines;
                                } catch (Exception e) {
                                    System.out.println("#######--Inner Exception--######");
                                    System.out.println(e.toString());
                                    System.out.println("################################");
                                }

                                return lines;
                            })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("##########--Exception--##########");
            System.out.println(e.getMessage());
            System.out.println("################################");
        }
    }

    /* T1 */ Set<String> failed(List<String> run)// имена упавших тестов
    {
        var separator = Pattern.quote("|");
        return run.stream()
                .filter(
                        (x) -> x.split(separator)[1].equals("FAILED"))
                .map(
                        (x) -> x.split(separator)[0])
                .collect(Collectors.toSet());
    }

    /* T2 */ Set<String> passed(List<String> run)// имена прошедших тестов прогона
    {
        var separator = Pattern.quote("|");
        return run.stream()
                .filter(
                        (x) -> x.split(separator)[1].equals("PASSED"))
                .map(
                        (x) -> x.split(separator)[0])
                .collect(Collectors.toSet());
    }

    /* T3 */ List<String> flaky(List<String> run1, List<String> run2)// тесты, которые упали в первом прогоне и прошли во втором
    {
        var p = Pattern.quote("|");

        Map<String, TestLog> logs1, logs2;

        logs1 = run1.stream().map((x) -> x.split(p))
                .collect(Collectors.toMap(x -> x[0], y -> new TestLog(y[0], y[1])));

        logs2 = run2.stream().map((x) -> x.split(p))
                .collect(Collectors.toMap(x -> x[0], y -> new TestLog(y[0], y[1])));

        var a = logs1.keySet();
        a.retainAll(logs2.keySet());

        List<String> res = new ArrayList<>();

        a.forEach(
                x -> {
                    TestLog l1 = logs1.get(x);
                    TestLog l2 = logs2.get(x);
                    if (!l1.equals(l2))
                        res.add(l1.name);
                });

        return res;
    }

    /* T4 */ Map<String, Long> statusSummary(List<String> run)// сколько тестов в каждом статусе
    {
        List<TestLog> logs = run.stream()
                .map((x) -> x.split(Pattern.quote("|")))
                .map(x -> new TestLog(x[0], x[1]))
                .collect(Collectors.toList());

        Map<String, Long> summary = new HashMap<>();
        for (var test : logs) {
            summary.merge(test.status, 1l, (old, _new) -> (old == null) ? _new : ++old);
        }

        return summary;
    }

}