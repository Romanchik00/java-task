import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

enum Status { PASSED, FAILED, SKIPPED }

record TestResult(String name, Status status, long durationMs)
{
    @Override
    public String toString() { return name + " | " + status + " | " + durationMs; }
}
record TestError(String row, String error)
{
    @Override
    public String toString() { return row + " | " + error; }
}


public class TestLogParser {

    private HashMap<String, TestResult> parsed_rows;
    private ArrayList<TestError> errors;

    public TestLogParser()
    {
        parsed_rows = new HashMap<>();
        errors = new ArrayList<>();
    }

    public ArrayList<TestError> getErrors()
    {
        ArrayList<TestError> errs = new ArrayList<>(errors.size());
        for(var er : errors)
            errs.add(new TestError(er.row(), er.error()));
        return errs;
    }

    private void parseRow(String row)
    {
        String[] strings = row.split(Pattern.quote("|"));

        if(strings.length < 3)
        {
            errors.add(new TestError(row, "Секции слиплись"));
            return;
        }
        if(strings.length > 3)
        {
            errors.add(new TestError(row, "Лишние секции"));
            return;
        }

        Status s = Status.FAILED;
        int i = -99;

        try
        {
            s = Status.valueOf(strings[1]);
            i = Integer.parseInt(strings[2]);

            if (i < 0)
                throw new Exception("");
        }
        catch(NumberFormatException n)
        {
            errors.add(new TestError(row, "Секция длительности - не число"));
            return ;
        }
        catch(IllegalArgumentException ex)
        {
            errors.add(new TestError(row, "Неизвестный статус"));
            return ;
        }
        catch(Exception ex)
        {
            errors.add(new TestError(row, "Секция длительности - отрицательное значение"));
            return ;
        }

        parsed_rows.put(strings[0], new TestResult(strings[0], s, i));
    }

    public List<TestResult> parse(List<String> lines)
    {
        for(String line : lines)
        {
            parseRow(line);
        }

        return new ArrayList<>(parsed_rows.values());
    }

    public Optional<TestResult> firstFailed(List<TestResult> results)
    {
        for(var row : parsed_rows.values())
        {
            if(row.status() == Status.FAILED)
            {
                return Optional.of(row);
            }
        }

        return Optional.ofNullable(null);
    }
}
