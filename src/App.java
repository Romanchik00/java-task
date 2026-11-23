public class App {
    public static void main(String[] args) throws Exception {

        var fd = new FlakyDetector();

        System.out.println("Run #1:");
        System.out.println("Passed:");
        fd.passed(fd.runs.get(0)).forEach((x)-> System.out.println("\t"+x));
        System.out.println("Failed:");
        fd.failed(fd.runs.get(0)).forEach((x)-> System.out.println("\t"+x));

        System.out.println("-----------------------------------");

        System.out.println("Run #2:");
        System.out.println("Passed:");
        fd.passed(fd.runs.get(1)).forEach((x)-> System.out.println("\t"+x));
        System.out.println("Failed:");
        fd.failed(fd.runs.get(1)).forEach((x)-> System.out.println("\t"+x));

        System.out.println("-----------------------------------");

        System.out.println("Flaky-кандидат(ы):");
        fd.flaky(fd.runs.get(0),fd.runs.get(1))
            .forEach((x)->System.out.println("\t" + x));
        
        System.out.println("Статистика статусов:");
        System.out.println("  Прогон 1:");
        fd.statusSummary(fd.runs.get(0)).forEach(
            (x,y) -> {
                System.out.println("\t"+x +" : "+ y);
            }
        );

         System.out.println("  Прогон 2:");
        fd.statusSummary(fd.runs.get(1)).forEach(
            (x,y) -> {
                System.out.println("\t"+x +" : "+ y);
            }
        );
    }
}