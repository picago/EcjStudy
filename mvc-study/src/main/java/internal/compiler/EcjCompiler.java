package internal.compiler;

import internal.classloader.SpyClassLoader;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Alex on 2014/9/20
 */
public class EcjCompiler {

    public static Class compileOne(String code) throws ClassNotFoundException, IOException {
        IProblemFactory problemFactory = new DefaultProblemFactory(Locale.ENGLISH);
        IErrorHandlingPolicy policy = DefaultErrorHandlingPolicies.exitOnFirstError();

        String sourceDir = CompilerHelper.getTargetFile().getAbsolutePath();
        String className = "Spy";
        File file = new File(sourceDir, className + ".java");
        FileUtils.writeStringToFile(file, code, "UTF-8");
        /**
         * The JDT compiler
         */
        org.eclipse.jdt.internal.compiler.Compiler jdtCompiler = new Compiler(
                new NameEnvironmentImpl(), policy, getCompilerOptions(), new CompilerRequestorImpl(),
                problemFactory);

        // Go !
        jdtCompiler.compile(new ICompilationUnit[]{new CompilationUnitImpl(file)});
        file.delete();
        return new SpyClassLoader().loadClass(className);

    }



    public static class CompilerRequestorImpl implements ICompilerRequestor {
        public void acceptResult(CompilationResult result) {
            // If error
            if (result.hasErrors()) {
                for (IProblem problem : result.getErrors()) {
                    String className = new String(problem.getOriginatingFileName()).replace("/", ".");
                    className = className.substring(0, className.length() - 5);
                    String message = problem.getMessage();
                    if (problem.getID() == IProblem.CannotImportPackage) {
                        // Non sense !
                        message = problem.getArguments()[0] + " cannot be resolved";
                    }
                    throw new RuntimeException(className + ":" + message);
                }
            }

            // Something has been compiled
            ClassFile[] clazzFiles = result.getClassFiles();
            for (int i = 0; i < clazzFiles.length; i++) {
                String clazzName = join(clazzFiles[i].getCompoundName());

                // save to disk as .class file
//                String clazzDir =  Thread.currentThread().getContextClassLoader().getResource("").getFile();
//                String clazzDir =
                File target = new File(CompilerHelper.getTargetFile(), clazzName.replace(".", "/") + ".class");
                try {
                    FileUtils.writeByteArrayToFile(target, clazzFiles[i].getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static CompilerOptions getCompilerOptions() {
        Map<String, String> settings = new HashMap();
        settings.put(CompilerOptions.OPTION_ReportMissingSerialVersion, CompilerOptions.IGNORE);
        settings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
        settings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
        settings.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
        settings.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
        settings.put(CompilerOptions.OPTION_Encoding, "UTF-8");
        settings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
        String javaVersion = CompilerOptions.VERSION_1_5;
        if (System.getProperty("java.version").startsWith("1.6")) {
            javaVersion = CompilerOptions.VERSION_1_6;
        } else if (System.getProperty("java.version").startsWith("1.7")) {
            javaVersion = CompilerOptions.VERSION_1_7;
        }
        settings.put(CompilerOptions.OPTION_Source, javaVersion);
        settings.put(CompilerOptions.OPTION_TargetPlatform, javaVersion);
        settings.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
        settings.put(CompilerOptions.OPTION_Compliance, javaVersion);
        return new CompilerOptions(settings);
    }

    private static String join(char[][] chars) {
        StringBuilder sb = new StringBuilder();
        for (char[] item : chars) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(item);
        }
        return sb.toString();
    }

}
