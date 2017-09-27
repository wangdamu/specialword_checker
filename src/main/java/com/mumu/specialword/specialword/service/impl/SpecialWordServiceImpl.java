package com.mumu.specialword.specialword.service.impl;

import com.mumu.specialword.specialword.service.SpecialWordService;
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
}
