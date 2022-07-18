package com.xiaoyv404.mirai.core

import java.io.*
import java.util.*
import java.util.jar.*


class NfClassFinder {
    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    private fun getClasses(packageName: String, jar: JarFile): Set<Class<*>> {

        // 第一个class类的集合
        //List<Class<?>> classes = new ArrayList<Class<?>>();
        var pn = packageName
        val classes: MutableSet<Class<*>> = HashSet()
        // 是否循环迭代
        val recursive = true
        // 获取包的名字 并进行替换
        val packageDirName = pn.replace('.', '/')

        // 从此jar包 得到一个枚举类
        val entries: Enumeration<JarEntry> = jar.entries()
        // 同样的进行循环迭代
        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            val entry: JarEntry = entries.nextElement()
            var name: String = entry.name
            // 如果是以/开头的
            if (name[0] == '/') {
                // 获取后面的字符串
                name = name.substring(1)
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                val idx = name.lastIndexOf('/')
                // 如果以"/"结尾 是一个包
                if (idx != -1) {
                    // 获取包名 把"/"替换成"."
                    pn = name.substring(0, idx).replace('/', '.')
                }
                // 如果可以迭代下去 并且是一个包
                // 如果是一个.class文件 而且不是目录
                if ((idx != -1 || recursive) && name.endsWith(".class") && !entry.isDirectory) {
                    // 去掉后面的".class" 获取真正的类名
                    val className = name.substring(pn.length + 1, name.length - 6)
                    // 添加到classes
                    classes.add(Class.forName("$pn.$className"))
                }
            }
        }
        return classes
    }


    fun getAnnotationClasses(
        packageName: String,
        annotationClass: Class<App>,
        jarFile: File
    ): Set<Class<*>> {

        //找用了annotationClass注解的类
        val controllers: MutableSet<Class<*>> = HashSet()
        val clsList = getClasses(packageName, JarFile(jarFile))
        if (clsList.isNotEmpty()) {
            for (cls in clsList) {
                if (cls.getAnnotation(annotationClass) != null) {
                    controllers.add(cls)
                }
            }
        }
        return controllers
    }
}