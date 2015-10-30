/*
 * ��  ��  ����Main.java
 * ��        ����������Դ��ɾ��
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
 * ������Դ��ɾ��
 */
public class Main
{
    /**
     * ��ǰ��Ŀ��Ҫ��keyֵ
     */
    private static List<String> needKeys = new ArrayList<String>();

    /**
     * ����name
     */
    private static final String NAME_REGEX = "name=\"([\\s\\S]*?)\"";

    /**
     * ����name
     */
    private static final String REF_NAME = "@string/([\\s\\S]*?)</";

    /**
     * ����value
     */
    private static final String VALUE_REGEX = "\">([\\s\\S]*?)</";

    /**
     * ��Ŀ��Դkey
     */
    private static String projectKey = "";

    /**
     * ������Դ��ɾ��
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
     * ������Դ��ɾ��
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
     * ��ʼ��key����
     * 
     * @param srcPath Դ·��
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
     * ��ע�������
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
     * �����ļ��Ĵ���
     * 
     * @param stringsFile Ҫ������ļ�
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
     * ��ȡString.xml�е�����Դ��name
     * 
     * @param line ��������
     * @return ������Դ��name
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
     * ��ȡString.xml�е�����Դ��value
     * 
     * @param line ��������
     * @return ������Դ��value
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
     * ��ȡString.xml�е�����Դ��������Դname
     * 
     * @param line ��������
     * @return ������Դ��������Դname
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
