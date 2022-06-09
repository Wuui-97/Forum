package com.wuui.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.lang.model.element.VariableElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-15-14:04
 * @describe 敏感词过滤
 */
@Component
@Slf4j
public class SensitiveFilter {
    //前缀树的根节点
    private TrieNode root = new TrieNode();
    //将敏感词替换为"***"
    private final String REPLACEMENT = "***";

    //构造前缀树
    //在执行完构造器方法后直接初始化前缀树
    @PostConstruct
    private void init(){
        try(
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                )
        {
            String s;
            while((s = bufferedReader.readLine()) != null){
                //对读入的敏感词构造前缀树
                createTrie(s);
            }

        } catch (IOException e) {
            log.error("读取文件错误" + e.getMessage());
        }

    }

    //构造前缀树
    private void createTrie(String s) {
        if(StringUtils.isBlank(s)){
            return ;
        }
        TrieNode tempNode = root;
        for(char c : s.toCharArray()){
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            tempNode = subNode;
        }
        //末尾字符的子节点做标记
        tempNode.setEnd(true);
    }

    /**
     * 对输入的文本进行过滤
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //过滤后返回的文本
        StringBuilder sb = new StringBuilder();
        //根节点
        TrieNode tempNode = root;
        //待过滤单词的起始位置：前指针
        int begin = 0;
        //待过滤单词的末尾位置：后指针
        int end = 0;

        while(end < text.length()){
            char c = text.charAt(end);
            //判断是否是符号
            if(isSymbol(c)){
                //如果当前节点是根节点，则前后指针都移动
                if(tempNode == root){
                    end = ++begin;
                    sb.append(c);
                }else{
                    end++;
                }
                continue;
            }

            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //前缀树没有匹配到敏感词
                sb.append(text.charAt(begin));
                end = ++begin;
                tempNode = root;
            } else if(subNode.isEnd()){
                //前缀树匹配到敏感词，进行替换
                sb.append(REPLACEMENT);
                begin = ++end;
                tempNode = root;
            }else{
                end++;
                tempNode = subNode;
            }
        }
        //可能会出现最后只匹配到一部分，end到末尾，而begin没有的情况
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否是符号
    private boolean isSymbol(char c){
        //0x2E80-0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode{
        //用来标记是否是最后一个字符
        private boolean isEnd;
        //用来存放节点值及构造前缀树
        //采用map来存放子节点，对应 TrieNode[] childern;
        private Map<Character, TrieNode> subNode = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        //设置子节点
        public void addSubNode(Character c, TrieNode node) {
            subNode.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNode.get(c);
        }
    }

}
