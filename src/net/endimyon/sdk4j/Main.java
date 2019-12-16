package net.endimyon.sdk4j;

//Designed to have some other functions, but then... removed, remaining the main method.
public class Main {
    public static void main(String[] args) {
        SDKLogger.log.info("This is a small free application which can solve simple Sudokus.");
        try{
            new SDKIterator().run();
        } catch (Exception e) {
            SDKLogger.log.error(e, "A problem has been captured and the process cannot continue.");
            System.exit(1);
        }
    }
}
