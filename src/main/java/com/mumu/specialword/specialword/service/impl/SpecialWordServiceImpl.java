package com.mumu.specialword.specialword.service.impl;

import com.mumu.specialword.specialword.service.SpecialWordService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@Service
public class SpecialWordServiceImpl implements SpecialWordService {

    @Override
    public void checkSpecialWords(File file, StringBuilder specialWords) {
//        try {
//            CompilationUnit compilationUnit = JavaParser.parse(file);
//            List<TypeDeclaration<?>> classOrInterfaceDeclarationList = compilationUnit.getTypes().stream().filter(t->t instanceof ClassOrInterfaceDeclaration).collect(Collectors.toList());
//            classOrInterfaceDeclarationList.forEach(t->{
//                ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration)t;
//                cid.g
//            });
//
//
//            System.out.println("=========================");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        State state = new NormalState();
        int lineNumber = 0;
        boolean hasSpecial = false;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")))) {
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if(line.trim().startsWith("logger.")){
                    state = new LoggerState();
                }else if(line.trim().contains("@Api(")){
                    state = new PendingNormalIgnoreState("@Api(", ")");
                }else if(line.trim().contains("@ApiOperation(")){
                    state = new PendingNormalIgnoreState("@ApiOperation(", ")");
                }else if(line.trim().contains("@ApiParam(")){
                    state = new PendingNormalIgnoreState("@ApiParam(", ")");
                }

                boolean specialFlag = false;
                for (char ch : line.toCharArray()) {
                    state = state.read(ch);
                    if (ch > 127 && state instanceof NormalState) {
                        specialFlag = true;
                    }
                }
                state = state.read('\n');

                lineNumber++;

                if (specialFlag) {
                    if(sb.length() > 0){
                        sb.append(", ");
                    }
                    sb.append(lineNumber);
//                    specialWords.append(file.getAbsoluteFile())
//                            .append("(").append(lineNumber).append(")\n");
                    hasSpecial = true;
                }
            }

            if(hasSpecial){
                specialWords.append(file.getAbsolutePath())
                        .append("(")
                        .append("line: 【")
                        .append(sb)
                        .append("】)")
                        .append("\n\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private interface State{
        State read(char ch);
    }

    class NormalState implements State{

        @Override
        public State read(char ch) {
            if(ch == '/'){
                return new PendingCommentState();
            }else if(ch == '@'){
            }
            return this;
        }
    }

    class PendingCommentState implements State{

        @Override
        public State read(char ch) {
            if(ch == '/'){
                return new LineCommentState();
            }else if(ch == '*'){
                return new MultiLineCommentState();
            }
            return new NormalState();
        }
    }

    class LineCommentState implements State{

        @Override
        public State read(char ch) {
            if(ch == '\n'){
                return new NormalState();
            }
            return this;
        }
    }

    class MultiLineCommentState implements State{

        @Override
        public State read(char ch) {
            if(ch == '*'){
                return new PendingLeaveMultiLineCommentState();
            }
            return this;
        }
    }

    class PendingLeaveMultiLineCommentState implements State{

        @Override
        public State read(char ch) {
            if(ch == '/'){
                return new NormalState();
            }else if(ch == '*'){
                return this;
            }
            return new MultiLineCommentState();
        }
    }

    class LoggerState implements State{

        @Override
        public State read(char ch) {
            if(ch == ')'){
                return new LoggerPendingEndState();
            }
            return this;
        }
    }

    class LoggerPendingEndState implements State{

        @Override
        public State read(char ch) {
            if(ch == ' '){
                return this;
            }else if(ch == ';'){
                return new NormalState();
            }
            return new LoggerState();
        }
    }

    class PendingNormalIgnoreState extends NormalState{
        private String start;
        private String end;

        public PendingNormalIgnoreState(String start, String end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public State read(char ch) {
            if(ch == start.charAt(0)){
                PendingIgnoreState state = new PendingIgnoreState(start, end);
                return state.read(ch);
            }
            return super.read(ch);
        }
    }

    class PendingIgnoreState implements State{
        private String start;
        private String end;
        private int index = 0;

        public PendingIgnoreState(String start, String end) {
            this.start = start;
            this.end = end;
        }


        @Override
        public State read(char ch) {
            try {
                if(ch == start.charAt(index)){
                    if(index == start.length() - 1){
                        // 是最后一个
                        return new IgnoreState(end);
                    }
                    return this;
                }
                return new NormalState();
            } finally {
                index++;
            }
        }
    }

    class IgnoreState implements State{
        private String end;
        private int index = 0;

        public IgnoreState(String end) {
            this(end, 0);
        }

        public IgnoreState(String end, int index) {
            this.end = end;
            this.index = index;
        }


        @Override
        public State read(char ch) {
            try {
                if(ch == end.charAt(index)){
                    if(index == end.length() - 1){
                        // 是最后一个
                        return new NormalState();
                    }else{
                        return new PendingIgnoreEndState(end, index + 1);
                    }
                }
                return new IgnoreState(end);
            } finally {
                index++;
            }
        }
    }

    class PendingIgnoreEndState implements State{
        private String end;
        private int index = 0;

        public PendingIgnoreEndState(String end, int index) {
            this.end = end;
            this.index = index;
        }

        @Override
        public State read(char ch) {
            try {
                if(ch == end.charAt(index)){
                    if(index == end.length() - 1){
                        // 是最后一个
                        return new NormalState();
                    }else{
                        if(ch == end.charAt(0)){
                            return new IgnoreState(end, 1);
                        }else{
                            return new IgnoreState(end);
                        }
                    }
                }
                return new NormalState();
            } finally {
                index++;
            }
        }
    }
}
