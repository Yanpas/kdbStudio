package studio.qeditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.editor.TokenID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QSyntaxTest {


    private QSyntax syntax;

    @BeforeEach
    public void init() {
        String text = "f: {[arg1; agr2] if[arg1; :arg2]; : 2*arg2; }";
        syntax = new QSyntax();
        syntax.load(null, text.toCharArray(), 0, text.length(), true, -1);
    }

    @Test
    public void testSyntax() {
        TokenID token = null;
        do {
            token = syntax.nextToken();
            System.out.println(token);
        } while (token != null);



    }

}
