import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

public class App {
        public static void main(String[] args) throws Exception {
                Random rand = new Random();
                String[] names = { "Login", "Access", "GetUser", "Marihuana", "Upload", "Shutdown",
                                "H.P.Lovecraft", "SexySnowman" };
                var pr = new ParallelRunner();
                var tasks = new ArrayList<Callable<TestResult>>();
                for (int i = 0; i < 8; i++)
                        tasks.add(ParallelRunner.fakeTest(
                                        names[rand.nextInt(names.length)],
                                        rand.nextLong(300, 500),
                                        Status.values()[rand.nextInt(Status.values().length)]));

                pr.invokeAndCollectAllWithStatistic(tasks);
                // Я попробовал считать время, как снаружи, так и изнутри.
                // Увы, получилось как-то неочень :( .
                // Но с остальным справился.
                pr.close();
        }
}