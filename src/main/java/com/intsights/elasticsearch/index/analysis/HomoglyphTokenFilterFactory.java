package com.intsights.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class HomoglyphTokenFilterFactory extends AbstractTokenFilterFactory {

    public HomoglyphTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HomoglyphTokenFilter(tokenStream);
    }
}
