package git.jbredwards.bpatcher.api.debugging;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import javax.annotation.Nonnull;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author jbred
 *
 */
public final class DebugPrinter
{
    /**
     * Creates a file containing the provided bytecode, useful for debugging asm transformations in general.
     * See {@link DebugOutputType} for file output types.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void print(@Nonnull byte[] basicClass, @Nonnull String pathName, @Nonnull String fileName, byte outputType) {
        if(outputType != DebugOutputType.NONE && !fileName.isEmpty()) {
            final String className = pathName;
            pathName = "bpatcher" + File.separatorChar + pathName.replace('.', File.separatorChar);
            fileName += '_' + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));

            try {
                //class
                if((outputType & DebugOutputType.CLASS_FILE) != 0) {
                    final File file = new File(pathName, fileName + ".class");
                    if(file.exists()) { file.delete(); }

                    final File dir = file.getParentFile();
                    if(!dir.exists()) { dir.mkdirs(); }

                    final FileOutputStream output = new FileOutputStream(file);
                    output.write(basicClass);
                    output.close();
                }
                //bytecode
                if((outputType & DebugOutputType.BYTECODE_TXT) != 0) {
                    final File file = new File(pathName, fileName + "_bytecode.txt");
                    if(file.exists()) { file.delete(); }

                    final File dir = file.getParentFile();
                    if(!dir.exists()) { dir.mkdirs(); }

                    final PrintWriter output = new PrintWriter(new FileWriter(file), true);
                    new ClassReader(basicClass).accept(new TraceClassVisitor(null, new Textifier(), output), 0);
                    output.close();
                }
                //asm
                if((outputType & DebugOutputType.ASMIFIED_TXT) != 0) {
                    final File file = new File(pathName, fileName + "_asmified.txt");
                    if(file.exists()) { file.delete(); }

                    final File dir = file.getParentFile();
                    if(!dir.exists()) { dir.mkdirs(); }

                    final PrintWriter output = new PrintWriter(new FileWriter(file), true);
                    new ClassReader(basicClass).accept(new TraceClassVisitor(null, new ASMifier(), output), 0);
                    output.close();
                }
            }
            //oops
            catch(IOException e) {
                e.printStackTrace();
                System.err.println("Could not save file: " + className);
            }
        }
    }
}
