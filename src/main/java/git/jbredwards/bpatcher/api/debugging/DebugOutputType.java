package git.jbredwards.bpatcher.api.debugging;

/**
 * This is usually reserved for debugging, and isn't recommended for use outside development.
 * @author jbred
 *
 */
public interface DebugOutputType
{
    /**
     * Does nothing.
     */
    byte NONE = 0;

    /**
     * Creates a class file.
     */
    byte CLASS_FILE = 1;

    /**
     * Creates a text file containing the class's bytecode.
     */
    byte BYTECODE_TXT = 2;

    /**
     * Creates a text file containing the class's asmified bytecode.
     */
    byte ASMIFIED_TXT = 4;

    /**
     * Creates both a class file and an asmified text file.
     */
    byte CLASS_AND_ASM = CLASS_FILE | ASMIFIED_TXT;
}
