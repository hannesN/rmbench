/*
 * created 03.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * a partition scanner that recognizes the following partition types:
 * <ul>
 * <li>single line comment</li>
 * <li>multi line comment</li>
 * </ul> 
 * @author cse
 */
public class DDLPartitionScanner extends RuleBasedPartitionScanner {
    
    public static final String DDL_SINGLELINE_COMMENT = "ddl_single_line_comment"; //$NON-NLS-1$
    public static final String DDL_MULTILINE_COMMENT = "ddl_multi_line_comment"; //$NON-NLS-1$
    public static final String DDL_CODE = IDocument.DEFAULT_CONTENT_TYPE;
    
    
    public DDLPartitionScanner() {
        IPredicateRule[] rules = new IPredicateRule[2];
        rules[0] = new EndOfLineRule("--", new Token(DDL_SINGLELINE_COMMENT));
        rules[1] = new MultiLineRule("/*", "*/", new Token(DDL_MULTILINE_COMMENT));
        
        setPredicateRules(rules);
    }
}
