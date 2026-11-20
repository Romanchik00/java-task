import java.io.FileReader;
import java.util.List;


public class App {
    public static void main(String[] args) throws Exception {
        Printable p = System.out::println;
        p.print("Hello, Testers!");

        TestLogParser parser;
        List<TestResult> results;
        List<TestError> errors;
        try(var file = new FileReader("src/run.log"))
        {
            parser = new TestLogParser();
            results = parser.parse(file.readAllLines());
            errors = parser.getErrors();
        }

        p.print("Результаты:");
        for(var res : results)
            p.print("\t" + res.toString());
        p.print("Битые строки:");
        for(var err : errors)
            p.print("\t" + err.toString());
        p.print("Первый упавший:");
        parser.firstFailed(results).ifPresentOrElse(
            (x)->{
                p.print("\t" + x.toString());
            },
            ()->{
                p.print("\tУпавших нет");
            });
    }
}

@FunctionalInterface
interface Printable {
    public void print(String s);
}