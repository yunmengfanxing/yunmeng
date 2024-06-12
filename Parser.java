package org.example.experiment2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Scanner;

public class Parser {
    private static final Map<String, Map<String, String>> dicM = new HashMap<>();
    private static final String[] VN = {"E", "P", "T", "p", "F"};
    private static final String[] VT = {"i", "+", "*", "(", ")", "e", "$"};

    static {
        Map<String, String> eMap = new HashMap<>();
        eMap.put("i", "TP");
        eMap.put("C", "TP");
        dicM.put("E", eMap);

        Map<String, String> pMap = new HashMap<>();
        pMap.put("+", "+TP");
        pMap.put(")", "e");
        pMap.put("$", "e");
        dicM.put("P", pMap);

        Map<String, String> tMap = new HashMap<>();
        tMap.put("i", "Fp");
        tMap.put("(", "Fp");
        dicM.put("T", tMap);

        Map<String, String> smallPMap = new HashMap<>();
        smallPMap.put("+", "e");
        smallPMap.put("*", "*Fp");
        smallPMap.put(")", "e");
        smallPMap.put("$", "e");
        dicM.put("p", smallPMap);

        Map<String, String> fMap = new HashMap<>();
        fMap.put("i", "i");
        fMap.put("(", "(E)");
        dicM.put("F", fMap);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要分析的符号串：");
        String strString = scanner.nextLine() + "$";
        scanner.close();

        int COUNT = 0;
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(VN[0]);

        try (PrintWriter writer = new PrintWriter("src\\main\\java\\org\\example\\experiment2\\output2")) {
            writer.println("步骤\t分析栈\t\t\t\t\t剩余输入串\t\t\t\t所用产生式\t\t\t动作");
            while (!stack.isEmpty()) {
                COUNT++;
                char ch = strString.charAt(0);
                String CSRight0 = findCSS(stack.peek(), String.valueOf(ch));

                if (CSRight0 != null && !isTerminal(stack.peek())) {
                    writer.printf("%d\t%-20s\t%-20s\t%s\t\t\t", COUNT, stack, strString, stack.peek() + "->" + CSRight0);
                    writer.println("POP, PUSH(" + CSRight0 + ")");
                } else if (isTerminal(CSRight0) || isTerminal(stack.peek())) {
                    writer.printf("%d\t%-20s\t%-20s\t\t\t\t\t", COUNT, stack, strString);
                    writer.println("POP");
                } else if (CSRight0 == null) {
                    writer.println("该句子不是LL(1)文法！");
                    break;
                }

                String CHS = stack.pop();
                String CSRight = findCSS(CHS, String.valueOf(ch));

                if (!isTerminal(CHS)) {
                    if (CSRight != null) {
                        if (isNonTerminal(CSRight.charAt(0))) {
                            for (char c : new StringBuilder(CSRight).reverse().toString().toCharArray()) {
                                stack.push(String.valueOf(c));
                            }
                        } else if (isTerminal(String.valueOf(CSRight.charAt(0))) && !CSRight.equals("e")) {
                            for (char c : new StringBuilder(CSRight).reverse().toString().toCharArray()) {
                                stack.push(String.valueOf(c));
                            }
                        }
                    }
                } else if (isTerminal(CHS)) {
                    if (ch == CHS.charAt(0)) {
                        if (CHS.equals("$")) {
                            writer.println("该句子是LL(1)文法文法！");
                        } else {
                            strString = strString.substring(1);
                        }
                    } else if (!CHS.equals("$")) {
                        writer.println("该句子不是LL(1)文法！");
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("文件未找到：" + e.getMessage());
        }
    }

    private static String findCSS(String argS, String argstr) {
        if (dicM.containsKey(argS)) {
            Map<String, String> tempValue = dicM.get(argS);
            if (tempValue.containsKey(argstr)) {
                return tempValue.get(argstr);
            }
        }
        return null;
    }

    private static boolean isTerminal(String symbol) {
        for (String s : VT) {
            if (s.equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNonTerminal(char symbol) {
        for (String s : VN) {
            if (s.charAt(0) == symbol) {
                return true;
            }
        }
        return false;
    }
}