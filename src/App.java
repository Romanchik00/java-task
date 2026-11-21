public class App {

    public class InnerApp {
        public String text(){ return "Text"; }
    }
    public static void main(String[] args) throws Exception {

        TestPlanScanner.buildPlan(CheckList.class);
        TestPlanScanner.buildPlan(InnerApp.class);

    }
}
