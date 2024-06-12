package org.example.experiment3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RPNCalculator {
    public static void main(String[] args) {
        String infixExpression = "((a+b)*c)/(a-c/d)*(d*(a+c)/(a-d))";
        System.out.println(infixExpression);
        Map<Character, Integer> variables = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("请分别输入abcd的值");
        int avar= sc.nextInt();
        int bvar= sc.nextInt();
        int cvar= sc.nextInt();
        int dvar= sc.nextInt();
        variables.put('a', avar);
        variables.put('b', bvar);
        variables.put('c', cvar);
        variables.put('d', dvar);
        List<String> infixList = convertToList(infixExpression);
        List<String> postfixList = infixToPostfix(infixList);
        int result = evaluatePostfix(postfixList, variables);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\main\\java\\org\\example\\experiment3\\output3"))) {
            writer.write("中缀表达式: " + infixList + System.lineSeparator());
            writer.write("后缀表达式: " + postfixList + System.lineSeparator());
            writer.write("所输入的abcd对应的值"+avar+'\t'+bvar+'\t'+cvar+'\t'+dvar+System.lineSeparator());
            writer.write("结果: " + result + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> infixToPostfix(List<String> infix) {
        Map<Character, Integer> precedence = new HashMap<>();
        precedence.put('+', 1);
        precedence.put('-', 1);
        precedence.put('*', 2);
        precedence.put('/', 2);

        Stack<Character> operators = new Stack<>();
        List<String> postfix = new ArrayList<>();

        for (String token : infix) {
            char c = token.charAt(0);
            if (Character.isLetterOrDigit(c)) {
                postfix.add(token);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    postfix.add(String.valueOf(operators.pop()));
                }
                operators.pop();
            } else {
                while (!operators.isEmpty() && precedence.getOrDefault(operators.peek(), 0) >= precedence.get(c)) {
                    postfix.add(String.valueOf(operators.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            postfix.add(String.valueOf(operators.pop()));
        }

        return postfix;
    }

    public static int evaluatePostfix(List<String> postfix, Map<Character, Integer> variables) {
        Stack<Integer> stack = new Stack<>();
        for (String token : postfix) {
            char c = token.charAt(0);
            if (Character.isLetter(c)) {
                stack.push(variables.get(c));
            } else {
                if (c == '-') {
                    if (stack.size() < 2) {
                        // 如果栈中没有足够的元素，则将0作为第一个操作数
                        int num2 = stack.pop();
                        int num1 = 0;
                        switch (c) {
                            case '+':
                                stack.push(num1 + num2);
                                break;
                            case '-':
                                stack.push(num1 - num2);
                                break;
                            case '*':
                                stack.push(num1 * num2);
                                break;
                            case '/':
                                stack.push(num1 / num2);
                                break;
                        }
                    } else {
                        int num2 = stack.pop();
                        int num1 = stack.pop();
                        stack.push(num1 - num2);
                    }
                } else {
                    int num2 = stack.pop();
                    int num1 = stack.pop();
                    switch (c) {
                        case '+':
                            stack.push(num1 + num2);
                            break;
                        case '-':
                            stack.push(num1 - num2);
                            break;
                        case '*':
                            stack.push(num1 * num2);
                            break;
                        case '/':
                            stack.push(num1 / num2);
                            break;
                    }
                }
            }
        }
        return stack.pop();
    }


    public static List<String> convertToList(String expression) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else if (c == '(' || c == ')' || c == '+' || c == '-' || c == '*' || c == '/') {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
                // 处理负号
                if (c == '-' && (i == 0 || expression.charAt(i - 1) == '(')) {
                    // 如果负号前面是左括号或者是开头，则表示是负号
                    sb.append(c);
                } else {
                    list.add(String.valueOf(c));
                }
            }
        }

        if (sb.length() > 0) {
            list.add(sb.toString());
        }

        return list;
    }


}
