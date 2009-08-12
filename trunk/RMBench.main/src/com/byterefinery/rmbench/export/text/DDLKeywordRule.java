/*
 * created 12.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export.text;

import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * a rule that recognizes keywords 
 * 
 * @author cse
 */
public class DDLKeywordRule extends WordRule {

    private static final String[] keywords =  {
        "constraint", "create", "table", "schema", "foreign", "primary", "key", "index", "column", "references"
    };
    
    private static final IWordDetector wordDetector = new IWordDetector() {
        public boolean isWordStart(char c) {
            return Character.isLetter(c);
        }

        public boolean isWordPart(char c) {
            return Character.isJavaIdentifierPart(c);
        }
    };
    
    /**
     * @param successToken the success token
     */
    public DDLKeywordRule(Token successToken) {
        super(wordDetector);
        for (int i = 0; i < keywords.length; i++) {
            addWord(keywords[i], successToken);
            addWord(keywords[i].toUpperCase(), successToken);
        }
    }
}
