package internal.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Alex on 2014/9/20
 */
public class NameEnvironmentImpl implements INameEnvironment {
    File SOURCES_DIR = FileUtils.getTempDirectory();
    /**
     * @param compoundTypeName {{'j','a','v','a'}, {'l','a','n','g'}}
     */
    public NameEnvironmentAnswer findType(final char[][] compoundTypeName) {
        return findType(join(compoundTypeName));
    }

    public NameEnvironmentAnswer findType(final char[] typeName, final char[][] packageName) {
        return findType(join(packageName) + "." + new String(typeName));
    }

    /**
     * @param name like `aaa`,`aaa.BBB`,`java.lang`,`java.lang.String`
     */
    private NameEnvironmentAnswer findType(final String name) {
        System.out.println("### to find the type: " + name);
        // check data dir first
        File file = new File(SOURCES_DIR, name.replace('.', '/') + ".java");
        if (file.isFile()) {
            return new NameEnvironmentAnswer(new CompilationUnitImpl(file), null);
        }

        // find by system
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream(name.replace(".", "/") + ".class");
            if (input != null) {
                byte[] bytes = IOUtils.toByteArray(input);
                if (bytes != null) {
                    ClassFileReader classFileReader = new ClassFileReader(bytes, name.toCharArray(), true);
                    return new NameEnvironmentAnswer(classFileReader, null);
                }
            }
        } catch (ClassFormatException e) {
            // Something very very bad
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("### type not found: " + name);
        return null;
    }

    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        String name = new String(packageName);
        if (parentPackageName != null) {
            name = join(parentPackageName) + "." + name;
        }

        File target = new File(SOURCES_DIR, name.replace('.', '/'));

        // only return false if it's a file
        // return true even if it doesn't exist
        return !target.isFile();
    }

    public void cleanup() {
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
