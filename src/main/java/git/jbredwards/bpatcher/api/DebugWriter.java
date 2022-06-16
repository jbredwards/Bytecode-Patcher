package git.jbredwards.bpatcher.api;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import javax.annotation.Nonnull;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Creates a file containing the provided patcher, useful for debugging asm transformations in general.
 * See {@link DebugOutputType} for file output types.
 * @author jbred
 *
 */
public final class DebugWriter
{
    @Nonnull static final Logger LOGGER = LogManager.getFormatterLogger("Bytecode Patcher");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void write(@Nonnull byte[] basicClass, @Nonnull String path, byte outputType) {
        if(outputType != DebugOutputType.NONE) {
            path = "bpatcher/" + path + '_' + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));

            try {
                //class
                if((outputType & DebugOutputType.CLASS_FILE) != 0) {
                    final File file = new File(path + ".class");
                    if(file.exists()) { file.delete(); }

                    final File dir = file.getParentFile();
                    if(!dir.exists()) { dir.mkdirs(); }

                    final FileOutputStream output = new FileOutputStream(file);
                    output.write(basicClass);
                    IOUtils.closeQuietly(output);
                }
                //bytecode
                if((outputType & DebugOutputType.BYTECODE_TXT) != 0) {
                    final File file = new File(path + "_bytecode.txt");
                    if(file.exists()) { file.delete(); }

                    final File dir = file.getParentFile();
                    if(!dir.exists()) { dir.mkdirs(); }

                    final PrintWriter output = new PrintWriter(new FileWriter(file), true);
                    new ClassReader(basicClass).accept(new TraceClassVisitor(null, new Textifier(), output), 0);
                    IOUtils.closeQuietly(output);
                }
                //asm
                if((outputType & DebugOutputType.ASMIFIED_TXT) != 0) {
                    final File file = new File(path + "_asmified.txt");
                    if(file.exists()) { file.delete(); }

                    final File dir = file.getParentFile();
                    if(!dir.exists()) { dir.mkdirs(); }

                    final PrintWriter output = new PrintWriter(new FileWriter(file), true);
                    new ClassReader(basicClass).accept(new TraceClassVisitor(null, new ASMifier(), output), 0);
                    IOUtils.closeQuietly(output);
                }
            }
            //oops
            catch(IOException e) {
                e.printStackTrace();
                LOGGER.warn("Could not save file: " + path);
            }
        }
    }
}
