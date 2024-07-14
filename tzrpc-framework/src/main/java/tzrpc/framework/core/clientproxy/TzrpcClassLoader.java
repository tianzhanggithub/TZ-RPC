package tzrpc.framework.core.clientproxy;


import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class TzrpcClassLoader extends ClassLoader{

    private File baseDir;       // 基准目录，构造函数中传入的 Class 的所在目录
    private Class clazz;        // 构造函数传入的 Class

    public TzrpcClassLoader(Class clazz) {
        this.clazz = clazz;
        String basePath = clazz.getResource("").getPath();    // 获取基准目录
        this.baseDir = new File(basePath);
    }

    public Class getResourceClass() {  return this.clazz;  }

    // 重写，寻找类的方法
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if(baseDir != null) {
            String className = clazz.getPackage().getName() + "." + name;  // 所在的包名 + 需要寻找的类名
            File classFile = new File(baseDir, name + ".class");  // 在基准目录下，寻找，需要寻找的类名的 .class 文件
            if(classFile.exists()) {        // 如果找到了这个 .class 文件，则将其动态加载到 JVM 中
                try(FileInputStream fis = new FileInputStream(classFile);        // 加载类的 .class 文件
                    ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                    byte[] buff = new byte[1024];       // 字节数据分组
                    int len = 0;
                    while ((len = fis.read(buff)) != -1) {
                        out.write(buff, 0, len);        // 全部读取到输出缓冲区
                    }
                    return defineClass(className, out.toByteArray(), 0, out.size());    // 动态加载到 JVM 中
                } catch (Exception e) {
                    log.error("TzrpcClassLoader.findClass --> 动态加载类到 JVM 中时发生异常; ", e);
                } finally {     // 注意关闭所有文件和流
                    boolean deleted = classFile.delete();   // 这里删除的是 .class 文件
                    if(!deleted)
                        log.error("TzrpcClassLoader.findClass --> 动态加载类到 JVM 中后，删除临时代理的 .class 文件失败;");
                }
            }
        }
        return null;
    }
}
