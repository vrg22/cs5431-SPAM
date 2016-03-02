/**
 * Client application
 */
public abstract class Client {
    private CommClient comm;

    protected static void printWelcome() {
        System.out.println("\n"
            + "                   Welcome to\n"
            + " .oooooo..o\n"
            + "d8P'    `Y8\n"
            + "Y88bo.      oo.ooooo.   .oooo.   ooo. .oo.  .oo.\n"
            + " `\"Y8888o.   888' `88b `P  )88b  `888P\"Y88bP\"Y88b\n"
            + "     `\"Y88b  888   888  .oP\"888   888   888   888\n"
            + "oo     .d8P  888   888 d8(  888   888   888   888\n"
            + "8\"\"88888P'   888bod8P' `Y888\"\"8o o888o o888o o888o\n"
            + "             888\n"
            + "            o888o\n");
    }
}
