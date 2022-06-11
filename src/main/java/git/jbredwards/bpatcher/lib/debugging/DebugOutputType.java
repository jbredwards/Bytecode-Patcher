package git.jbredwards.bpatcher.lib.debugging;

/**
 * This is usually reserved for debugging, and isn't recommended for use outside development.
 * @author jbred
 *
 */
public interface DebugOutputType
{
    /**
     * Creates a class file.
     */
    byte CLASS_FILE = 1;
    /**
     * Creates an asmified text file.
     */
    byte ASMIFIED_TXT = 2;
    /**
     * Creates both a class file and an asmified text file.
     */
    byte CLASS_AND_ASM = CLASS_FILE | ASMIFIED_TXT;
}
