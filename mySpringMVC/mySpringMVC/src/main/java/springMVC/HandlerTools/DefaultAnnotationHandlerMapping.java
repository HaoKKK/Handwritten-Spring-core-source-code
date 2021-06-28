package springMVC.HandlerTools;
import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * HandlerMapping的具体实现
 */
public class DefaultAnnotationHandlerMapping extends AbstractHandlerMapping{
    public void defaultAnnotationMethod(){
        System.out.println("这里可以拓展一些处理注解配置Controller的特殊方法");
    }
//  比较复杂的算法模块：路径匹配，还记得SpringMVC路径匹配的规则吗？还记得*、**、？分别代表什么吗？
    @Override
    protected HandlerExecutionChain searchMatchedURLAddress(String requestURL) {
        HandlerExecutionChain res = null;
//        这里我们采用的方法是先把所有匹配的都找出来，然后找出最近匹配的
        List<Map.Entry<String, HandlerExecutionChain>> ans = new ArrayList<>();
        for(Map.Entry<String,HandlerExecutionChain> entry:handlerMap.entrySet()){
            if(urlJudgingModel(entry.getKey(),requestURL)){
                ans.add(entry);
            }
        }
//        虽然找到了所有可以匹配的，但我们需要找出最近匹配原则。这里我采取
//        积分制度，当然真正的MVC框架并不是这么做的，我们只是在保证原有设计思路、设计模式和架构的基础上，
//        具体实现方法还是自己决定,读者大大一定能想到更精妙的最近匹配方法，欢迎交流！
        int score = -1;
        int curScore;
        for (Map.Entry<String, HandlerExecutionChain> a : ans) {
            curScore = getScore(a.getKey());
            if (curScore > score) {
                score = curScore;
                res = a.getValue();
            }
        }
        return res;
    }
//  所谓的积分制就是找到非正则的直接匹配的最远距离
    private int getScore(String rowURL) {
        int score = 0;
        for (int i =0;i<rowURL.length();i++){
//            如果有正则表达，结束
            if(rowURL.charAt(i) != '*' && rowURL.charAt(i) != '?'){
                score++;
            }else {
                break;
            }
        }
        return score;
    }
//这个匹配模型采用的记忆化搜索，是一种变种的动态规划，不了解的同学多刷题啊！！
    public boolean urlJudgingModel(String rowURL, String requestURL) {
        String[] rowURLs = rowURL.split("/");
        String[] requestURLs = requestURL.split("/");
//        0表示不知道能不能匹配，1表示可以匹配，-1表示不可以匹配。
        int[][] mem = new int[rowURLs.length][requestURLs.length];
//        注意先把连续的**合并一下，免得出问题，因为一个**和n个**是一样的
        boolean beforeIsStar = false;
        ArrayList<String> temp = new ArrayList<>();
        for(int i =0;i<rowURLs.length;i++){
            if(rowURLs[i].equals("**")){
                if(!beforeIsStar){
                    temp.add(rowURLs[i]);
                }
                beforeIsStar = true;
            }else {
                beforeIsStar =false;
                temp.add(rowURLs[i]);
            }
        }
        String[] simpleRowURLS = new String[temp.size()];
        for(int i =0;i<simpleRowURLS.length;i++){
            simpleRowURLS[i] = temp.get(i);
        }
//        Matching()函数表示两个URL分别从i和j开始是否可以匹配，可以为1，不可以-1，不知道为0.
        return matching(simpleRowURLS, 0, requestURLs, 0, mem) == 1;
    }
//目录匹配
//    具体的匹配逻辑就不一行一行讲了，算法不是我们的重点，我们关注的是设计思想和架构。
    private int matching(String[] rowURLs,int i, String[] requestURLs,int j ,int[][] mem) {
        if(i == rowURLs.length && j == requestURLs.length){return 1;}
        if(i == rowURLs.length ){return -1;}
        if(j == requestURLs.length){
            if(i == rowURLs.length-1 && rowURLs[i].equals("**")){mem[i][j] =1;return 1;}
            return -1;
        }
        if(mem[i][j] != 0){return mem[i][j];}
        if(i == rowURLs.length-1 && j == requestURLs.length-1){
            if(rowURLs[i].equals("**")){
                mem[i][j] = 1;
            }
            else if(singleMatching(rowURLs[i],requestURLs[j])){
                mem[i][j] = 1;
            }else {
                mem[i][j] = -1;
            }
            return mem[i][j];
        }
        int res = -1;
        if(rowURLs[i].equals("**")){
            int next = j;
            while (next <=requestURLs.length){
                if(matching(rowURLs,i+1,requestURLs,next,mem) == 1){
                    res = 1;
                    break;
                }
                next++;
            }

        }else {
            if(singleMatching(rowURLs[i],requestURLs[j])){
                res = matching(rowURLs,i+1,requestURLs,j+1,mem);
            }
        }
        mem[i][j] = res;
        return mem[i][j];
    }
//目录内文件匹配
    private boolean singleMatching(String rowURL, String requestURL) {
        if(rowURL.length() == 0 && requestURL.length() == 0){return true;}
        if(rowURL.length() == 0 || requestURL.length() == 0){return false;}
        if(rowURL.charAt(0) == '{'){return true;}
        int[][] mem = new int[rowURL.length()][requestURL.length()];
//        预处理一下，把连续的*去掉
        boolean beforeIsStar = false;
        StringBuilder temp = new StringBuilder();
        for(int i =0;i<rowURL.length();i++){
            if(rowURL.charAt(i) == '*'){
                if(!beforeIsStar){temp.append(rowURL.charAt(i));}
                beforeIsStar = true;
            }else {
                beforeIsStar = false;
                temp.append(rowURL.charAt(i));
            }
        }
        String simpleRowURL = temp.toString();
        return isMatching(simpleRowURL,requestURL,0,0,mem) == 1;
    }

    private int isMatching(String rowURL, String requestURL, int i, int j, int[][] mem) {
        if(i == rowURL.length() && j == requestURL.length()){return 1;}
        if(i == rowURL.length()){return -1;}
        if(j == requestURL.length()){
            if(i == rowURL.length()-1 && rowURL.charAt(i) == '*'){return 1;}
            return -1;
        }
        if(mem[i][j] != 0){return mem[i][j];}
        int res = -1;
        if(rowURL.charAt(i) == '*'){
            for(int k = j;k<=requestURL.length();k++){
                if(isMatching(rowURL,requestURL,i+1,k,mem) == 1){
                    res = 1;
                    break;
                }
            }
        }else {
            if(rowURL.charAt(i) == requestURL.charAt(j) || rowURL.charAt(i) == '?'){
                res = isMatching(rowURL,requestURL,i+1,j+1,mem);
            }
        }
        mem[i][j] = res;
        return res;
    }
}
