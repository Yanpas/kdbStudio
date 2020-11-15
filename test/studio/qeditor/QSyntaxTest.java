package studio.qeditor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenID;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class QSyntaxTest {


    private QSyntax syntax;
    @BeforeEach
    public void initSyntax() {
        syntax = new QSyntax();
    }

    private static String encode(String buffer) {
        return buffer.replace("\n","\\n")
                .replace("\r","\\r")
                .replace("\t","\\t");
    }

    private static String decode(String buffer) {
        return buffer.replace("\\n","\n")
                .replace("\\r","\r")
                .replace("\\t","\t");
    }

    private void assertSyntax(String text, String... ids) {
        syntax.load(null, text.toCharArray(), 0, text.length(), true, text.length());
        int offset = 0;
        List<String> tokens = new ArrayList<>();
        for(;;) {
            TokenID token =syntax.nextToken();
            if (token == null) break;
            int oldOffset = offset;
            offset = syntax.getOffset();

            String[] lines = text.substring(oldOffset, offset).split("\n");
            int count = 0;
            for (String line: lines) {
                if (line.length() > 0) count++;
            }
            for (int i=0; i<count; i++) {
                tokens.add(token.getName());
            }
        }
        String tokenOutput = tokens.stream().collect(Collectors.joining(","));
        System.out.println(encode(text) + "," + tokenOutput);
//        assertArrayEquals(ids, tokens.toArray(new String[0]));
    }

    @Test
    public void testSyntaxFromFile() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("qsyntax.csv") ));
        String line;
        while ( (line = input.readLine()) != null ) {
            String[] words = line.split(",");
            String[] tokens = Stream.of(words).skip(1).toArray(String[]::new);
            assertSyntax(decode(words[0]), tokens);
        }
        input.close();
    }

}
