package studio.qeditor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenID;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        syntax.load(null, text.toCharArray(), 0, text.length(), true, -1);
        int index = 0;
        for(;;index++) {
            TokenID token =syntax.nextToken();
            if (token == null) break;
            assertTrue(index < ids.length, "Text has more token as expected: " + encode(text));
            assertEquals(ids[index], token.getName(), "Different token: " + encode(text));
        }
        assertEquals(ids.length, index, "Found less token as expected: " + encode(text));
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
