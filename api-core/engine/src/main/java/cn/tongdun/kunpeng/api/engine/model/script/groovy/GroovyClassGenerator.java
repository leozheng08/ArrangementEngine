package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;


public class GroovyClassGenerator {

    private StringBuilder source            = new StringBuilder(); // 存储构建源代码
    private String        className;
    private static String GROOVY_CLASS_TAIL = "\n}";

    private static String IMPORT;
    static {
        StringBuilder builder = new StringBuilder();
        builder.append("import ").append(Map.class.getName()).append("\n");
        builder.append("import ").append(SimpleDateFormat.class.getName()).append("\n");
        builder.append("import ").append(StaticObjects.class.getName()).append("\n");

        IMPORT = builder.toString();
    }
    
    public StringBuilder getSource() {
        return source;
    }

    public void setSource(StringBuilder source) {
        this.source = source;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = "Groovy" + className;
    }

    public GroovyClassGenerator(String className){
        this.className = "Groovy" + className;
    }

    /**
     * 编译源码成groovy class并返回其实例
     *
     * @return
     * @throws CompilationFailedException
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public GroovyObject compileGroovySource() throws CompilationFailedException, IOException, InstantiationException,
                                             IllegalAccessException {
        GroovyClassLoader classLoader = new GroovyClassLoader();
        GroovyObject instance = null;
        try {
            @SuppressWarnings("rawtypes")
            Class groovyClass = classLoader.parseClass(source.toString());
            instance = (GroovyObject) groovyClass.newInstance();
        } catch (Exception e) {
            // 抛出去让上层处理
            throw e;
        } finally {
            classLoader.close();
        }
        return instance;
    }

    /**
     * 编译方法
     */
    public static boolean compileMethod(String methodName, String methodBody) {
        GroovyClassGenerator genrator = new GroovyClassGenerator("CompileTest");
        genrator.init();
        genrator.appendMethod(methodName, methodBody);
        try {
            genrator.compileGroovySource();
        } catch (CompilationFailedException | InstantiationException | IllegalAccessException | IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 编译方法
     * 编译检查用，异常抛给外部处理，用于确认排查问题
     */
    public static void compileMethod(Long id, String methodBody) throws Exception {
        String className = "compile_test_groovy_"+ id;
        GroovyClassGenerator generator = new GroovyClassGenerator(className);
        generator.init();
        generator.appendMethod("func_" + id, methodBody);
        try {
            generator.compileGroovySource();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 追加方法体
     * @param methodName
     * @param methodBody
     * @return 追加方法代码后的完整代码
     */
    public String appendMethod(String methodName, String methodBody) {
        int insertPos = source.lastIndexOf(GROOVY_CLASS_TAIL);
        StringBuilder method = new StringBuilder();
        method.append("static ").append(methodName)
                .append(" (context")
                //.append(",mobileQueryService,idService,districtService,binService,modelService,gaeaCallerService,cardBinNewService,elfinBaseDataService")
                .append(") {\n")
                .append(methodBody)
                .append("\n}\n");
        source.insert(insertPos, method);
        return source.toString();
    }

    /**
     * 初始化新的类体
     */
    public void init() {
        source.append(IMPORT).append("class ").append(className).append(" {\n\t").append(GROOVY_CLASS_TAIL).toString();
    }
}
