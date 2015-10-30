/*
 * 文  件  名：Main.java
 * 描        述：冗余资源的删除
 */
package com.res;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * 冗余资源的删除
 */
public class Main
{
    /**
     * 当前项目需要的key值
     */
    private static List<String> needKeys = new ArrayList<String>();

    /**
     * 解析name
     */
    private static final String NAME_REGEX = "name=\"([\\s\\S]*?)\"";

    /**
     * 解析name
     */
    private static final String REF_NAME = "@string/([\\s\\S]*?)</";

    /**
     * 解析value
     */
    private static final String VALUE_REGEX = "\">([\\s\\S]*?)</";

    /**
     * 项目资源key
     */
    private static String projectKey = "";

    /**
     * 冗余资源的删除
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        if (args != null && args.length >= 3)
        {
            projectKey = args[2];
            initKeys(args[0]);
            processDir(args[1]);
        }
    }

    /**
     * 冗余资源的删除
     * 
     * @param args
     * @throws Exception
     */
    public static void main0(String[] args) throws Exception
    {
        projectKey = "A001";
        initKeys("ResTest\\res\\values");
        processDir("ResProject\\res\\");
    }

    /**
     * 初始化key集合
     * 
     * @param srcPath 源路径
     */
    private static void initKeys(String srcPath) throws Exception
    {
        needKeys.clear();
        String srcFile = srcPath + "\\strings.xml";

        List<String> allLines = FileUtils.readLines(new File(srcFile), "utf-8");
        Iterator<String> iterator = allLines.iterator();
        while (iterator.hasNext())
        {
            String tmp = iterator.next();
            String name = getRefName(tmp);
            if (!"".equals(name))
            {
                needKeys.add(name);
            }
        }
    }

    /**
     * 关注代码过滤
     */
    private static void processDir(String srcDir) throws Exception
    {
        File dir = new File(srcDir);
        if (dir.isDirectory())
        {
            String[] allFiles = dir.list();
            if (allFiles != null && allFiles.length > 0)
            {
                for (String tmp : allFiles)
                {
                    if (tmp.startsWith("values"))
                    {
                        File stringsFile = new File(srcDir + "\\" + tmp + "\\strings.xml");
                        if (stringsFile.exists())
                        {
                            processFile(stringsFile);
                        }
                    }
                }
            }
        }
    }

    /**
     * 单个文件的处理
     * 
     * @param stringsFile 要处理的文件
     */
    private static void processFile(File stringsFile) throws Exception
    {
        List<String> allLines = FileUtils.readLines(stringsFile, "utf-8");
        Iterator<String> iterator = allLines.iterator();
        String value = "";
        while (iterator.hasNext())
        {
            String tmp = iterator.next();
            String name = getName(tmp);
            if (!"".equals(name) && !needKeys.contains(name))
            {
                if (projectKey.equals(name))
                {
                    value = getValue(tmp);
                }
                iterator.remove();
            }
        }
        List<String> newAllLines = new ArrayList<String>();
        iterator = allLines.iterator();
        while (iterator.hasNext())
        {
            String tmp = iterator.next();
            if (!"".equals(tmp) && tmp.contains("appname") && tmp.contains("ENTITY"))
            {
                newAllLines.add("  <!ENTITY appname \"" + value + "\">");
            }
            else
            {
                newAllLines.add(tmp);
            }
        }
        FileUtils.writeLines(stringsFile, "utf-8", newAllLines);
    }

    /**
     * 获取String.xml中单条资源的name
     * 
     * @param line 单行内容
     * @return 单条资源的name
     */
    private static String getName(String line)
    {
        Pattern pattern = Pattern.compile(NAME_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher localMatcher = pattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (localMatcher.find())
        {
            sb.append(localMatcher.group(1).trim());
        }
        return sb.toString();
    }

    /**
     * 获取String.xml中单条资源的value
     * 
     * @param line 单行内容
     * @return 单条资源的value
     */
    private static String getValue(String line)
    {
        Pattern pattern = Pattern.compile(VALUE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher localMatcher = pattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (localMatcher.find())
        {
            sb.append(localMatcher.group(1).trim());
        }
        return sb.toString();
    }

    /**
     * 获取String.xml中单条资源的引用资源name
     * 
     * @param line 单行内容
     * @return 单条资源的引用资源name
     */
    private static String getRefName(String line)
    {
        Pattern pattern = Pattern.compile(REF_NAME, Pattern.CASE_INSENSITIVE);
        Matcher localMatcher = pattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (localMatcher.find())
        {
            sb.append(localMatcher.group(1).trim());
        }
        return sb.toString();
    }
}
