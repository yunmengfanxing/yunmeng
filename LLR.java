package org.example.experiment1;

import java.io.*;
import java.util.Scanner;
import java.util.*;

class Results{//保存要输出的结果
    boolean rig;//是否是错误
    int type;//记录类型
    String res;//输出内容
    int li=0,co=0;//所在行和列
}
public class LLR {
    static final int N=101;//定义一个常量，当输入增多时便于后续修改
    static int[] a=new int [N];//该数组用来存放文本每行的列数（假设输入数据不超过100行）
    static boolean ok=false;//记录每行第一个单词前是否有空格，如果有为true
    static boolean mark=false;//如果判断出是注释的内容，是则为true，否则为false
    static boolean biu=false;//当存在多行注释时为true
    static int blank=0;//保存前面的空格数
    static int k=0,lines,cols;//lines用来记录是第几行,cols记录第几列，不赋初值时自动为0
    static int num=0;//记录每一行字符的个数
    static boolean words=false;//当由""引起来的字符串占多行时为true
    static String strs="";//保存多行字符串前面的内容
    static Results[] RE =new Results[N];//保存一行中遇到的字符
    static String[] keyWord ={"void","var","int","float","string","begin","end","if",
            "then","else","while","do","call","read","write","and","or"};//关键字
    static String[] symbol ={"{","}","(",")",";"," "};//分隔符
    static String[] operation ={"==","=","<","<=",">",">=","<>","+","-","*","/"};//运算符
    static ArrayList<String> keyWords=null;//关键字
    static ArrayList<String> symbols=null;//分隔符
    static ArrayList<String> operations=null;//运算符

    //关键字,字母或$的识别   1 2
    public static void letterCheck(String str){
        cols=k+1;
        String token= String.valueOf(str.charAt(k++));
        char ch;
        for( ;k<str.length();k++){
            ch=str.charAt(k);
            if (!Character.isLetterOrDigit(ch)&&ch!='$')
                break;
            else
                token+=ch;
        }
        if(ok)
            cols+=blank;
        if (keyWords.contains(token)){
            RE[num].rig=true;RE[num].type=getnumber(token);
            RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
            ++num;
        }
        else{
            RE[num].rig=true;RE[num].type=0;
            RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
            ++num;
        }
        if (k!=str.length()-1||(k==str.length()-1&&(!Character.isLetterOrDigit(str.charAt(k))&&str.charAt(k)!='$')))
            k--;
    }
    //数字的识别  3 4
    //1、识别退出：遇到空格符，遇到运算符或者界符   2、错误情况：两个及以上小数点，掺杂字母
    public static void digitCheck(String str){
        cols=k+1;
        String token= String.valueOf(str.charAt(k++));
        int flag=0;//记录小数点的个数
        boolean err=false;
        char ch;
        for( ;k<str.length();k++){
            ch=str.charAt(k);
            if(ch==' '||(!Character.isLetterOrDigit(ch)&&ch!='.')||
                    symbols.contains(ch)||operations.contains(ch))//遇到空格，运算符或者标识符则退出，属于正常退出
                break;
            else if (err)
                token+=ch;
            else{
                token+=ch;
                if (ch == '.') {//这个if判断是否存在多个小数点的情况
                    if(flag>=1)//如果之前已经记录有一个小数点了，那么此时就是错误
                        err=true;
                    flag++;//只要遇到小数点就加一
                }
                else if (Character.isLetter(ch))//遇到字母时是不对的
                    err=true;
            }
        }
        if(token.charAt(token.length()-1)=='.'&&flag>=2)//如果最后的出的字符串最后一位是小数点，并且出现多个小数点则错误
            err=true;  //小数点前面不能为空，后面2可以为空
        if(err){
            RE[num].rig=false;RE[num].type=100;
            RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
            ++num;
        }
        else{
            if(ok)
                cols+=blank;
            if(flag==0) {//flag为0时说明没有小数点，数字串和小数要分开输出
                RE[num].rig=true;RE[num].type=30;
                RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
                ++num;
            }
            else{
                RE[num].rig=true;RE[num].type=4;
                RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
                ++num;
            }
        }
        if(k!=str.length()-1||(k==str.length()-1&&!Character.isDigit(str.charAt(k))))
            k--;
    }
    //字符串检查   5
    public static void stringCheck(String str){
        cols=k+1;
        String token=String.valueOf(str.charAt(k++));
        char ch;
        int n=str.length();
        if(!words){//直接寻找第一次分号的位置并记录下来，如果不加这个条件可能会被识别为第2次的
            for(int i=0;i<n;++i){
                if(str.charAt(i)=='"'){
                    RE[num].li=lines;
                    RE[num].co=i+1+(ok==true?blank:0);
                    break;
                }
            }
        }
        for( ;k<n;++k){
            ch=str.charAt(k);
            token+=ch;
            if(words==true&&ch=='"'){//多行结束情况判断，遇到引号结束，就把strs的值赋给token
                strs+=token;
                token=strs;//最后保存到RE中的是token，所以将strs的值赋给token
                words=false;//这里一定要置为false，否则后面对1,2的判断都不会出现
                break;
            }
            if(ch=='"')//单独一行就直接break就行了
                break;
        }
        if(token.charAt(token.length()-1)!='"') {//最后一个字符不是双引号说明是多行字符串，标记为true，直接返回继续判断
            words=true;
            strs+=token;
            return;
        }
        else{
            RE[num].type=5;
            RE[num].res=token;
            ++num;
        }
    }
    //单引号引起来字符的识别   6
    public static void charCheck(String str){
        cols=k+1;
        String token=String.valueOf(str.charAt(k++));
        char ch;
        int n=str.length();
        for( ;k<n;++k){
            ch=str.charAt(k);
            token+=ch;
            if(ch=='\'')//单个打印号要用转义字符表示
                break;
        }
        if(token.charAt(token.length()-1)!='\'') {//最后一个字符不是单引号
            RE[num].rig=false;RE[num].type=100;
            RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
            ++num;
        }
        else {//没有就可以输出是第几个类型的词，本身，行号和列号
            if(ok)
                cols+=blank;
            RE[num].rig=true;RE[num].type=6;
            RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
            ++num;
        }
    }
    //分隔符，运算符的识别   7 8
    public static void symbolCheck(String str){
        cols=k+1;
        String token= String.valueOf(str.charAt(k++));
        char ch;
        if (symbols.contains(token)){//如果该符号包含在分隔符中，因为分隔符中都是单个符号，所以直接输出token
            if(ok)
                cols+=blank;
            RE[num].rig=true;RE[num].type=getdelimiter(token);
            RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
            ++num;
            k--;
        }
        else {
            if (operations.contains(token)){
                if (k<str.length()){
                    ch=str.charAt(k);

                    if(str.charAt(k)=='/'&&str.charAt(k-1)=='/'){//只要遇到单行注释，直接return就行，不用做标记
                        mark=true;
                        return;
                    }
                    if(str.charAt(k-1)=='/'&&str.charAt(k)=='*'){//当存在多行注释时，使biu为true
                        biu=true;
                        //return; 当在一行中找到/*符号时，可能这一行也会有*/符号，所以这个需要判断，不能直接返回
                    }
                    if(biu){//判断是不是会在当前这行注释结束
                        for(int i=k;i<str.length();++i){
                            if(str.charAt(i)=='*'&&str.charAt(i+1)=='/'){
                                biu=false;
                                k=i+1;//从多行注释结束的下一个字符开始分析
                                return;
                            }
                        }
                        return;//如果没在同一行结束，直接返回判断下一行就好了
                    }
                    if (operations.contains(token+ch)){//如果包含，说明该运算符由2个字符组成
                        token+=ch;
                        if(ok)
                            cols+=blank;
                        RE[num].rig=true;RE[num].type=100;
                        RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
                        ++num;
                    }
                    else {//此情况说明只是单个字符的运算符
                        k--;
                        if(ok)
                            cols+=blank;
                        if(mark==false&&biu==false){
                            RE[num].rig=true;RE[num].type=getoperate(token);
                            RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
                            ++num;
                        }
                    }
                }
            }
            else {//错误
                k--;
                if(ok)
                    cols+=blank;
                RE[num].rig=false;RE[num].type=100;
                RE[num].res=token;RE[num].li=lines;RE[num].co=cols;
                ++num;
            }
        }
    }

    public static void init(){//初始化把数组转换为ArrayList,容易查找
        keyWords=new ArrayList<>();
        operations=new ArrayList<>();
        symbols=new ArrayList<>();
        Collections.addAll(keyWords,keyWord);
        Collections.addAll(symbols,symbol);
        Collections.addAll(operations,operation);
    }
    public static void analyze(String str){
        k=0;
        char ch;
        str=str.trim();//去掉每一行前后的空格
        int n=str.length();
        if(biu){//判断是不是会在下一行注释结束
            for(int i=0;i<n-1;++i){
                if(str.charAt(i)=='*'&&str.charAt(i+1)=='/'){
                    biu=false;
                    k=i+2;//从多行注释结束的下一个字符开始分析
                    break;
                }
            }
        }
        if(biu)
            return;//如果当前没有多行注释结束标志，说明这一行还是注释，直接跳过
        for ( ;k<str.length();k++){
            ch=str.charAt(k);
            if (Character.isDigit(ch))
                digitCheck(str);
            else if((words==false)&&(Character.isLetter(ch)||ch=='$'))//多行字符串时不能被1,2判断
                letterCheck(str);
            else if (words==true||ch=='"')//多行字符串的情况或者双引号开头都要归为第5类判断
                stringCheck(str);
            else if(ch=='\'')
                charCheck(str);
            else if (ch==' ')
                continue;
            else {
                symbolCheck(str);
                if(mark||biu)
                    return;
            }

        }
    }

    public static void main(String[] args) {
        init();
        try {
            String filePath = "src\\main\\java\\org\\example\\experiment1\\实验一.txt";//初始化文件路径
            File file = new File(filePath);//创建文件对象
            PrintWriter writer = new PrintWriter(new FileWriter("src\\main\\java\\org\\example\\experiment1\\output1.txt"));//创建文件写入流
            writer.println("单词\t\t\t二元序列\t\t\t类 型\t\t位置（行，列）");
            try (Scanner in = new Scanner(file)) {//读取文件并分析内容
                while (in.hasNextLine()) {
                    String str = in.nextLine();//每次读取一行
                    cols = 0;//每一次换行的列数 都要从头计数
                    ok = false;
                    mark = false;//每一行新判断的时候都应该把注释记录清空
                    if (!words) {//当有多行字符串时，要保存之前的行和列，所以就不对其清零了。并且数组也不从0开始保存
                        num = 0;//表示一行中分析到词的个数
                        for (int i = 0; i < N; ++i) {
                            RE[i] = new Results();//必须要先对每一个对象先实例化之后再对其赋值，否则会报空指针的错误
                            RE[i].rig = true;
                            RE[i].type = 0;
                            RE[i].res = "";
                            RE[i].li = 0;
                            RE[i].co = 0;
                        }
                    }
                    int len = str.length();
                    a[++lines] = len;//用数组存放每一行有多少列
                    if (str.trim().length() != a[lines]) {//如果前面有空格，就先把相差的空格数记上
                        blank = (a[lines] - str.trim().length());
                        ok = true;//只有开头有空格时ok才为true，在句子中时或者句子前没空格时ok为false
                    }
                    analyze(str);
                    for (int i = 0; i < num; ++i) {
                        if (RE[i].rig) {
                            String wordType = getWordType(RE[i].type); // 获取单词类型字符串
                            writer.println(RE[i].res + "\t\t\t(" + RE[i].type + "," + RE[i].res + ")\t\t\t" + wordType + "\t\t\t(" + RE[i].li + "," + RE[i].co + ")");
                        }
                        else {
                            writer.println(RE[i].res+"\t\t\t("+RE[i].type + "," + RE[i].res + ")\t\t\tError\t\t\t(" + RE[i].li + "," + RE[i].co + ")");
                        }
                    }
                }
                writer.close(); // 关闭写入流
                System.out.println("输出成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getWordType(int type) {
        switch (type) {
            case 0 -> {
                return "标识符";
            }
            case 20,21,22,23,24,25-> {
                return "关键字";
            }
            case 30 -> {
                return "常量";
            }
            case 1,2,3,4,5,11,12 -> {
                return "分界符";
            }
            case 6,7,8,9,10 -> {
                return "关系运算符";
            }
            case 100 -> {
                return "Error";
            }
            default -> {
                return "未知类型";
            }
        }
    }
    public static int getnumber(String str){
        if (str.equals("main")){
            return 20;
        }
        if (str.equals("int")){
            return 21;
        }
        if (str.equals("if")){
            return 22;
        }
        if (str.equals("then")){
            return 23;
        }
        if (str.equals("Else")){
            return 24;
        }if (str.equals("Return")){
            return 25;
        }
        return -1;
    }
    public static int getoperate(String s){
        if (s.equals("=")){
            return 6;
        }if (s.equals("+")){
            return 7;
        }if (s.equals("*")){
            return 8;
        }
        if (s.equals("<")){
            return 9;
        }
        if (s.equals(">")){
            return 10;
        }
        return -1;
    }
    public static int getdelimiter(String s){
        if (s.equals("(")){
            return 1;
        } if (s.equals(")")){
            return 2;
        } if (s.equals("{")){
            return 3;
        } if (s.equals("}")){
            return 4;
        } if (s.equals(";")){
            return 5;
        } if (s.equals(",")){
            return 11;
        } if (s.equals("?")){
            return 12;
        }
        return -1;
    }
}
