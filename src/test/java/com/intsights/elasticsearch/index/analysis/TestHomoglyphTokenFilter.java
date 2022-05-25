package com.intsights.elasticsearch.index.analysis;

import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.tests.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.io.StringReader;

class NoUtf16ParsingTokenizer extends MockTokenizer {
    public NoUtf16ParsingTokenizer(){
        super(MockTokenizer.WHITESPACE, false);
    }

    @Override
    protected int readCodePoint() throws IOException {
        int ch = readChar();
        return ch;
    }
}

public class TestHomoglyphTokenFilter extends BaseTokenStreamTestCase {
    public void testEmptyInput() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[0]);
    }

    public void testSingleHomoglyph() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("tℯst");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {"test"});
    }

    public void testNoHomoglyphs() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("test");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {"test"});
    }

    public void testMultipleMappingHomoglyphs() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("heｌｌО");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {
            "hello", "hell0", "hel1o", "hel10", "he1lo", "he1l0", "he11o", "he110"
        });
    }

    public void test4byteUtf16Homoglyphs() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("𝒕𝒆𝒔𝒕");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {"test"});
    }

    public void testMultipleLetterHomoglyphs() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("㏔-㎰");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {"mb-ps"});
    }

    public void testLowercasing() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("TEST");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {"test"});
    }

    public void testMultipleTokenInput() throws Exception {
        TokenStream stream = whitespaceMockTokenizer("onℯ two 𝒕hree fОur FIVE sⅸ");
        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {
            "one", "two", "three", "four", "f0ur", "five", "six"
        }, new int[] {1, 1, 1, 1, 0, 1, 1});
    }

    public void testUnicodeEdgeCases() throws Exception {
        NoUtf16ParsingTokenizer stream = new NoUtf16ParsingTokenizer();
        stream.setReader(new StringReader("aa\ud802aaℯ bb\udc02bbℯ ℯcc\ud803"));

        HomoglyphTokenFilter filter = new HomoglyphTokenFilter(stream);
        assertTokenStreamContents(filter, new String[] {
            "aa\ud802aae", "bb\udc02bbe", "ecc\ud803"
        });
    }
}
