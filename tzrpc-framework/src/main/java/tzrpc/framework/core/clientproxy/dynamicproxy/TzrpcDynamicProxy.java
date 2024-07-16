package tzrpc.framework.core.clientproxy.dynamicproxy;


import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TzrpcDynamicProxy {

    private static AtomicInteger proxySN = new AtomicInteger(0);    // 代理对象的序号
    private static final String proxyPrefix = "$TzrpcProxy";

    // 获取代理对象实例
    // 参数1: 需要被代理的接口们
    // 参数2: 代理接口实现类，提供 invoke 方法
    public static Object newProxyInstance(Class<?> interf) throws Exception {
        if(interf == null || !interf.isInterface()) {
            log.error("TzrpcDynamicProxy.newProxyInstance --> Tzrpc 动态代理只能代理 接口！; clazz = {};", interf);
            throw new TzrpcException("proxy fail", "Tzrpc 动态代理只能代理 接口！");
        }
        TzrpcClassLoader classLoader = new TzrpcClassLoader(interf);
        TzrpcProxyHandler handler = new TzrpcServiceCallProxyHandler(interf);
        int sn = getProxySN();     // 获取当前代理对象的序号

        // 1. 根据实际情况，生成对应的代理类的源代码
        String proxySrc = generateSrc(classLoader, sn, interf);
        // log.info("TzrpcDynamicProxy.newProxyInstance --> Tzrpc 动态代理生成的代理类文件内容: \n" + proxySrc);

        // 2. 将生成的源代码输出到磁盘，保存为 .java 文件
        // 输出到 ClassLoader 指定的类的目录下
        String filePath = classLoader.getResourceClass().getResource("").getPath();
        File file = new File(filePath + proxyPrefix + sn + ".java");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(proxySrc);
        fileWriter.flush();
        fileWriter.close();

        // 3. 编译源代码，并且生成 .class 文件
        // 下述编译代码，照搬就行
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();  // Java 自带的编译器
        StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> iterable = manager.getJavaFileObjects(file);
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, null, null, iterable);
        task.call();
        manager.close();

        // 4. 将 .class 文件中的内容动态加载到 JVM 中，该部分在自定义的 ClassLoader 中实现
        // 5. 返回被代理后的代理对象
        Class<?> proxyClass = classLoader.findClass(proxyPrefix + sn);  // 通过自定义的 ClassLoader 找到对应的代理类
        Constructor<?> constructor = proxyClass.getConstructor(TzrpcProxyHandler.class);
        Object result = constructor.newInstance(handler);   // 生成对应的代理类对象，返回给用户
        file.delete();      // 别忘了删除文件, 这里关闭的是 .java 文件
        return result;
    }

    // 生成代理类源代码
    private static String generateSrc(TzrpcClassLoader classLoader, int sn, Class<?> interf) {
        String ln = "\r\n";     // 换行符
        StringBuilder builder = new StringBuilder();        // 生成器

        builder.append("package " + classLoader.getResourceClass().getPackage().getName() + ";" + ln);     // 定义包
        builder.append(ln);

        // 导包
        builder.append("import java.lang.reflect.Method;" + ln);
        builder.append(ln);

        // 定义类
        builder.append("public class " + proxyPrefix + sn + " ");
        builder.append("implements ").append(interf.getName());
        builder.append(" { " + ln);
        builder.append(ln);

        // 定义字段
        builder.append("    private " + TzrpcProxyHandler.class.getName() +  " h;" + ln);
        builder.append(ln);

        // 构造函数
        builder.append("    public "+ proxyPrefix + "0(" + TzrpcProxyHandler.class.getName() + " h) {" + ln);
        builder.append("        this.h = h;" + ln);
        builder.append("    }" + ln);
        builder.append(ln);

        // 对于所有被代理的接口中的方法
        for (Method method : interf.getMethods()) {
            builder.append("    public " + method.getReturnType().getName() + " " + method.getName() + "(");
            Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                builder.append(params[i].getType().getName() + " " + params[i].getName());
                if(i < params.length - 1)
                    builder.append(", ");
            }
            builder.append(") {" + ln);
            builder.append("        try { " + ln);
            builder.append("            Method m = " + interf.getName() + ".class.getMethod(\"" + method.getName() + "\", new Class[]{");
            for (int i = 0; i < params.length; i++) {
                builder.append(params[i].getType().getName() + ".class");
                if(i < params.length - 1)
                    builder.append(", ");
            }
            builder.append("});" + ln);
            builder.append("            Object o = this.h.invoke(this, m, new Object[]{");
            for (int i = 0; i < params.length; i++) {
                builder.append(params[i].getName());
                if(i < params.length - 1)
                    builder.append(", ");
            }
            builder.append("});" + ln);
            if(!"void".equals(method.getReturnType().getName()))
                builder.append("            return (" + method.getReturnType().getName() + ") o;" + ln);
            builder.append("        } catch (Throwable t) { " + ln);
            builder.append("            t.printStackTrace();" + ln);
            builder.append("            throw new RuntimeException(t.getMessage());" + ln);
            builder.append("        } " + ln);

            builder.append("    }" + ln);
            builder.append(ln);
        }

        builder.append("}" + ln);

        return builder.toString();
    }

    // 获取当前代理类的序号，并发安全，保证不会有相同的代理类名
    private synchronized static int getProxySN() {
        return TzrpcDynamicProxy.proxySN.getAndIncrement();
    }
}
