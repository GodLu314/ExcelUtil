package com.godlu.excelutil.utils;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

/**
 * @author: GodLu
 * @create: 2024-02-27 11:38
 * @description: 封装日志操作
 */
public class LogUtil {
    private static String className;//打印日志的类名
    private static String methodName;//打印日志的方法名
    private static int lineNumber;//日志调用的行数

    /**
    * @Author: GodLu
    * @Date: 2024/2/27 11:40
    * @Description: 获取栈中类信息
    * @return: LocationAwareLogger
    */
    private static Logger getLogger() {
        //通过堆栈信息获取调用当前方法的类名和方法名
        JavaLangAccess access = SharedSecrets.getJavaLangAccess();
        Throwable throwable = new Throwable();
        StackTraceElement frame = access.getStackTraceElement(throwable, 2);
        className = frame.getClassName();
        methodName = frame.getMethodName();
        lineNumber = frame.getLineNumber();
        return (Logger) LoggerFactory.getLogger(frame.getClassName());
    }

    /**
    * @Author: GodLu
    * @Date: 2024/2/27 11:41
    * @Description: 封装Debug级别日志
    * @param msg 日志信息
    * @param arguments 日志参数
    * @return: void
    */
    public static void debug(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        getLogger().debug("[" + className + " --> " + methodName + ":" + lineNumber +"] : " + msg, arguments);
    }

    /**
     * @Author: GodLu
     * @Date: 2024/2/27 11:41
     * @Description: 封装Info级别日志
     * @param msg 日志信息
     * @param arguments 日志参数
     * @return: void
     */
    public static void info(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        getLogger().info("[" + className + " --> " + methodName + ":" + lineNumber +"] : " + msg, arguments);
    }

    /**
     * @Author: GodLu
     * @Date: 2024/2/27 11:41
     * @Description: 封装Warn级别日志
     * @param msg 日志信息
     * @param arguments 日志参数
     * @return: void
     */
    public static void warn(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        getLogger().warn("[" + className + " --> " + methodName + ":" + lineNumber +"] : " + msg, arguments);
    }

    /**
     * @Author: GodLu
     * @Date: 2024/2/27 11:41
     * @Description: 封装Error级别日志
     * @param msg 日志信息
     * @param arguments 日志参数
     * @return: void
     */
    public static void error(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        getLogger().error("[" + className + " --> " + methodName + ":" + lineNumber +"] : " + msg, arguments);
    }

    /**
     * @Author: GodLu
     * @Date: 2024/2/27 11:41
     * @Description: 异常堆栈转字符串
     * @param e 异常信息
     * @return: void
     */
    public static String ExceptionToString(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            if (e == null) {
                return "无具体异常信息";
            }
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } catch (Exception ex) {
            LogUtil.error("异常堆栈转字符串异常", ex);
            return "";
        } finally {
            assert sw != null;
            sw.flush();
            assert pw != null;
            pw.flush();
            pw.close();
        }

    }
}
