/*
 * created 08.08.2005
 * 
 * $Id: ColumnPair.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.views.table;

import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * helper class for maintaining a pair of column -> target column in the context of
 * foreign key/reference views
 * 
 * @author cse
 */
class ColumnPair {
    final Column column;
    final Column targetColumn;
    
    private ColumnPair(Column column, Column targetColumn) {
        this.column = column;
        this.targetColumn = targetColumn;
    }

    static ColumnPair[] createFrom(ForeignKey key) {
        Column[] cols = key.getColumns();
        ColumnPair[] result = new ColumnPair[cols.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ColumnPair(cols[i], key.getTargetColumn(cols[i]));
        }
        return result;
    }
}
