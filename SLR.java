package org.example.experiment4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SLR {
    private static List<Integer> status = new ArrayList<>();
    private static List<Character> sign = new ArrayList<>();
    // 定义输入的字符串
    private static List<Character> inputStr = new ArrayList<>();
    // 记录输入的字符串
    private static String inputVal;

    public static class Grammar {
        public int grammarNum;
        public String[] formula = {" ", "E->E+T", "E->T", "T->T*F", "T->F", "F->(E)", "F->i"};

        public Grammar() {
            grammarNum = 6;
        }
    }

    public static class LRAnalyseTable {
        public char[] terminalChar = {'i', '+', '*', '(', ')', '#'};
        // 定义终结符的个数
        public int terNum = 6;
        public char[] nonTerminalChar = {'E', 'T', 'F'};
        // 定义非终结符的个数
        public int nonTerNum = 3;
        // 定义状态数
        public int statusNum = 12;
        public String[][] action = {
                {"s5", "", "", "s4", "", ""},
                {"", "s6", "", "", "", "acc"},
                {"", "r2", "s7", "", "r2", "r2"},
                {"", "r4", "r4", "", "r4", "r4"},
                {"s5", "", "", "s4", "", ""},
                {"", "r6", "r6", "", "r6", "r6"},
                {"s5", "", "", "s4", "", ""},
                {"s5", "", "", "s4", "", ""},
                {"", "s6", "", "", "s11", ""},
                {"", "r1", "s7", "", "r1", "r1"},
                {"", "r3", "r3", "", "r3", "r3"},
                {"", "r5", "r5", "", "r5", "r5"}
        };
        public int[][] goTo = {
                {1, 2, 3},
                {-1, -1, -1},
                {-1, -1, -1},
                {-1, -1, -1},
                {8, 2, 3},
                {-1, -1, -1},
                {-1, 9, 3},
                {-1, -1, 10},
                {-1, -1, -1},
                {-1, -1, -1},
                {-1, -1, -1},
                {-1, -1, -1}
        };

        // 获取终结符的索引
        public int getTerminalIndex(char var) {
            for (int i = 0; i < terNum; i++) {
                if (terminalChar[i] == var) {
                    return i;
                }
            }
            return -1;
        }

        // 获取非终结符的索引
        public int getNonTerminalIndex(char var) {
            for (int i = 0; i < nonTerNum; i++) {
                if (nonTerminalChar[i] == var) {
                    return i;
                }
            }
            return -1;
        }
    }


    // 读取输入的字符串
    private static void readStr() {
        System.out.println("LR(1)分析程序");
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入表达式");
        inputVal = sc.nextLine();
        char[] chars = inputVal.toCharArray();
        for (char ch : chars) {
            inputStr.add(ch);
        }
        // 把#加入容器
        inputStr.add('#');
    }


    public static void LRAnalyse() {
        LRAnalyseTable analyseTable = new LRAnalyseTable();
        Grammar grammar = new Grammar();
        // 步骤
        int step = 1;
        // 把状态0入栈
        status.add(0);
        // 把#加入符号栈
        sign.add('#');

        System.out.println(String.format("%s\t\t%s\t\t%s\t\t%s\t\t%s", "步骤", "状态栈", "符号栈", "输入串", "动作说明"));

        int s = 0;

        int oldStatus;
        char ch = inputStr.get(0);
        while (!analyseTable.action[s][analyseTable.getTerminalIndex(ch)].equals("acc")) {
            // 获取字符串
            String str = analyseTable.action[s][analyseTable.getTerminalIndex(ch)];
            // 如果str为空，报错并返回
            if (str.length() == 0) {
                System.out.println("出错");
                System.out.println(inputVal + "为非法符号串");
                return;
            }
            // 获取r或s后面的数字
            int index = Integer.parseInt(str.substring(1));
            s = index;
            // 如果是移进
            if (str.startsWith("s")) {
                System.out.println(String.format("%s\t\t%s\t\t\t%s\t\t\t%s\t\t%s", step, vectTrancStr(0), vectTrancStr(1), vectTrancStr(2), "ACTION[" + status.get(status.size() - 1) + "," + ch + "]=S" + s + ", 状态" + s + "入栈"));
                // 输入符号入栈
                sign.add(ch);
                inputStr.remove(0);
                // 将状态数字入栈
                status.add(s);
            }
            // 如果是归约
            else if (str.startsWith("r")) {
                // 获取第S个产生式
                String formu = grammar.formula[s];
                int strSize = formu.length();
                // 获取产生式的首字符
                char nonTerCh = formu.charAt(0);
                // 获取符号栈的出栈次数
                int popCount = strSize - 3;
                // 反向迭代
                oldStatus = status.get(status.size() - popCount - 1);
                s = analyseTable.goTo[oldStatus][analyseTable.getNonTerminalIndex(nonTerCh)];
                System.out.println(String.format("%s\t\t%s\t\t\t%s\t\t\t%s\t\t%s", step, vectTrancStr(0), vectTrancStr(1), vectTrancStr(2), "r" + index + ":" + grammar.formula[index] + "归约, GOTO{" + oldStatus + "," + nonTerCh + ")=" + s + "入栈"));
                // 对符号栈进行出栈和状态栈进行出栈
                for (int i = 0; i < popCount; i++) {
                    sign.remove(sign.size() - 1);
                    status.remove(status.size() - 1);
                }
                // 再对产生式的开始符号入栈
                sign.add(nonTerCh);
                // 再把新的状态入栈
                status.add(s);
            } else {
                // 什么都不处理
            }
            // 步骤数加1
            step++;
            // 获取栈顶状态
            s = status.get(status.size() - 1);
            // 获取输入的字符
            ch = inputStr.get(0);
        }
        System.out.println(String.format("%s\t\t%s\t\t\t\t%s\t\t\t%s\t\t%s", step, vectTrancStr(0), vectTrancStr(1), vectTrancStr(2), "acc:分析成功"));
        System.out.println(inputVal + "为合法符号串");
    }

    private static String vectTrancStr(int i) {
        StringBuilder sb = new StringBuilder();
        if (i == 0) {
            for (int num : status) {
                sb.append(num);
            }
        } else if (i == 1) {
            for (char c : sign) {
                sb.append(c);
            }
        } else {
            for (char c : inputStr) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        readStr();
        LRAnalyse();
    }


}
