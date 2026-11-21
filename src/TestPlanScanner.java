import java.util.ArrayList;

record TestStep(String id, String methodName, int order) {
    public String toString() {
        return id + " -> " + methodName;
    }
}

public class TestPlanScanner {
    public static void buildPlan(Class<?> class1) {
        var meths = class1.getDeclaredMethods();
        var steps = new ArrayList<TestStep>();

        if (meths.length > 0) {
            for (var meth : meths) {
                if (meth.isAnnotationPresent(TestId.class))
                    steps.add(
                            new TestStep(
                                    meth.getAnnotation(TestId.class).value(),
                                    meth.getName(),
                                    meth.getAnnotation(TestId.class).order()));
            }

            steps.sort((x, y) -> Integer.compare(x.order(), y.order()));
        }

        System.out.println(String.format("План прогона для %s:", class1.getSimpleName()));
        if (steps.size() == 0) {
            System.out.println("\tПусто...");
        } else {
            int i = 1;
            for (TestStep step : steps) {
                System.out.println("\t" + i + ". " + step);
            }
        }

    }
}
