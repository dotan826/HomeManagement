package homemanagement;

/**
 * This is the Main Class which initiate the program.
 * 
 */
public class HomeManagement {
    private HelpMethods helpM;     // All program methods in one place (WITHOUT GUI!!!)
    private WindowFirst window1;   // Main Window
    
    /**
     * Run Application Method
     */
    public void runHome(){
        helpM = new HelpMethods();
        helpM.changeLookAndFeelWindow10();
        window1 = new WindowFirst(helpM);
        window1.setVisible(true);
    }

    public HelpMethods getHelpMethods() {
        return helpM;
    }

    public WindowFirst getWindowFirst() {
        return window1;
    }

    public void setHelpMethods(HelpMethods helpM) {
        this.helpM = helpM;
    }

    public void setWindowFirst(WindowFirst window1) {
        this.window1 = window1;
    }
    
    public static void main(String[] args) {
        new HomeManagement().runHome();  // run application
    }
    
} // end of class
