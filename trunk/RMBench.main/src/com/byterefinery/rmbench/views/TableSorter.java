/*
 * Created on 04.08.2005 by cse
 * 
 * Copyright (c) 2005, DynaBEAN Consulting. All rights reserved
 * 
 * $Id: TableSorter.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.views;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;

/**
 * helper class which sorts the table entries according to the selected column and
 * sort direction
 * 
 * @author cse
 */
@SuppressWarnings("unchecked")
public class TableSorter implements Comparator {
    
    private int sortColumn;
    private boolean ascending;

    private ITableLabelProvider labelProvider;

    private List<?> entries;

    public TableSorter(ITableLabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    public int getSortColumn() {
        return sortColumn;
    }

    public void reverseOrder() {
        ascending = !ascending;
        if(entries != null)
            java.util.Collections.sort(entries, this);
    }

    public Object getEntries() {
        return entries;
    }
    
    public void setEntries(List entries) {
        this.entries = entries;
    }
    
    public void setSortColumn(int column) {
        sortColumn = column;
        if(entries != null)
            java.util.Collections.sort(entries, this);
    }

    public void resort() {
        if(entries != null)
            java.util.Collections.sort(entries, this);
    }

    public int compare(Object o1, Object o2) {
        String t1 = labelProvider.getColumnText(o1, sortColumn);
        String t2 = labelProvider.getColumnText(o2, sortColumn);

        return ascending ? t1.compareTo(t2) : t2.compareTo(t1);
    }
}