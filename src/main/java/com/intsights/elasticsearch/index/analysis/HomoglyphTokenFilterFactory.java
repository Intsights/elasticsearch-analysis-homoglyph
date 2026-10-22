package com.intsights.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.plugin.NamedComponent;
import org.elasticsearch.plugin.analysis.TokenFilterFactory;

@NamedComponent(HomoglyphTokenFilterFactory.NAME)
public class HomoglyphTokenFilterFactory implements TokenFilterFactory {
    public static final String NAME = "homoglyph";

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HomoglyphTokenFilter(tokenStream);
    }
}
