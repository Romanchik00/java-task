import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ParallelRunner {
    ExecutorService execserv;

    ParallelRunner() {
        execserv = Executors.newFixedThreadPool(4);
    }

    public static Callable<TestResult> fakeTest(String name, long durationMs, Status status) {
        return () -> {
            Thread.sleep(durationMs);
            return new TestResult(name, status, durationMs);
        };
    }

    public List<Future<TestResult>> invokeAll(Collection<Callable<TestResult>> tasks) {
        try {
            return execserv.invokeAll(tasks);
        } catch (InterruptedException ie) {
            System.err.println("Interrupted exception");
            return Collections.emptyList();
        }
    }

    public void invokeAndCollectAllWithStatistic(Collection<Callable<TestResult>> tasks) {
        var tests = invokeAll(tasks);
        List<TestResult> result;
        //long before = System.nanoTime(),
        //        after;

        //result = ParallelRunner.collectResult(tests);
        //after = System.nanoTime() - before;
        Long total[] = new Long[1];
        result = ParallelRunner.collectResultWithTotalDuration(tests, total);

        //                                                                          херня, конечно, но пусть будет
        var statCount = result.stream().collect(Collectors.groupingBy(x -> (x.status().equals(Status.PASSED)? "Пройдено": "Упало"), Collectors.counting()));

        System.out.println("=== Statistics of performed tests ===");
        for(var row : statCount.entrySet()){
            System.out.print(row.getKey() + ": " + row.getValue() + "  ");
        }
        var sum = result.stream().collect( Collectors.summingLong(TestResult::durationMs));
        System.out.println("Суммарное время тестов: " + sum + " ms");
        System.out.println("Реальное время прогонов: " + total[0] / 1000 + " ms" );
        System.out.println("Параллельность сэкономила: " + (sum - total[0]) + " ms" );
        System.out.println("Пройденные тесты: ");
        System.out.println("--------------------------------------------------");
        result.forEach(x -> System.out.println(x.toString()));
        System.out.println("--------------------------------------------------");
    }

    public static List<TestResult> collectResult(List<Future<TestResult>> execs) {
        return execs.stream().map(exec -> {
            try {
                return exec.get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    public static List<TestResult> collectResultWithTotalDuration(List<Future<TestResult>> execs, Long[] outDuration) {
        var start = System.nanoTime();
        var res = execs.stream().map(exec -> {
            try {
                return exec.get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        var end = System.nanoTime() - start;
        outDuration[0] = end;
        return res;
    }

    public void close(){this.execserv.close();}
}
