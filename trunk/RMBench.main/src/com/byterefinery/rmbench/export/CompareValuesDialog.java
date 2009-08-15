/*
 * created 31.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: CompareValuesDialog.java 215 2006-02-18 21:08:54Z cse $
 */
package com.byterefinery.rmbench.export;

import java.util.StringTokenizer;

import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * a dialog that shows the compared values in two multiline text fields, with differences 
 * highlighted
 * 
 * @author cse
 */
public class CompareValuesDialog extends Dialog {

    private static final Color DIFF_ADD = ColorConstants.green;
    private static final Color DIFF_CHG = ColorConstants.red;
    private static final Color DIFF_BG = ColorConstants.white;
    
    private static int TEXT_HEIGHT = 7;
    private static int TEXT_WIDTH = 75;
    
    private final String modelValue;
    private final String dbValue;
    
    private final WordRangeComparator leftComparator, rightComparator;
    private final RangeDifference[] differences;

    /**
     * @param parentShell
     * @param modelValue the model value
     * @param dbValue the database value
     */
    protected CompareValuesDialog(Shell parentShell, String modelValue, String dbValue) {
        super(parentShell);
        this.modelValue = modelValue;
        this.dbValue = dbValue;
        setShellStyle(getShellStyle() | SWT.RESIZE);
        
        leftComparator = new WordRangeComparator(modelValue);
        rightComparator = new WordRangeComparator(dbValue);
        differences = RangeDifferencer.findDifferences(leftComparator, rightComparator);
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.CompareValuesDialog_title);
    }
    
    protected Control createDialogArea(Composite container) {
        
        Composite parent = (Composite) super.createDialogArea(container);
        
        Label modelLabel = new Label(parent, SWT.NONE);
        modelLabel.setLayoutData(new GridData());
        modelLabel.setText(Messages.CompareValuesDialog_modelLabel);
        StyledText modelText = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = convertWidthInCharsToPixels(TEXT_WIDTH);
        gd.heightHint = convertHeightInCharsToPixels(TEXT_HEIGHT);
        modelText.setLayoutData(gd);
        modelText.setText(modelValue);
        
        Label dbLabel = new Label(parent, SWT.NONE);
        dbLabel.setLayoutData(new GridData());
        dbLabel.setText(Messages.CompareValuesDialog_databaseLabel);
        StyledText dbText = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = convertWidthInCharsToPixels(TEXT_WIDTH);
        gd.heightHint = convertHeightInCharsToPixels(TEXT_HEIGHT);
        dbText.setLayoutData(gd);
        dbText.setText(dbValue);
        
        int startPos, length;
        for (int i = 0; i < differences.length; i++) {
            boolean add = differences[i].rightLength() == 0;
            boolean delete = differences[i].leftLength() == 0;
            
            if(!delete) {
                startPos = leftComparator.getPosition(differences[i].leftStart());
                length = leftComparator.getLength(differences[i].leftStart(), differences[i].leftLength());
                modelText.setStyleRange(new StyleRange(startPos, length, add ? DIFF_ADD : DIFF_CHG, DIFF_BG));
            }
            if(!add) {
                startPos = rightComparator.getPosition(differences[i].rightStart());
                length = rightComparator.getLength(differences[i].rightStart(), differences[i].rightLength());
                dbText.setStyleRange(new StyleRange(startPos, length, DIFF_CHG, DIFF_BG));
            }
        }
        return parent;
    }    

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
    
    /*
     * a comparator that uses tokens (words) as compare ranges, and memorizes token positions  
     */
    private static class WordRangeComparator implements IRangeComparator {

        private final String[] tokens;
        private final int[] positions;
        
        WordRangeComparator(String value) {
            StringTokenizer tokenizer = new StringTokenizer(value);
            this.tokens = new String[tokenizer.countTokens()];
            this.positions = new int[tokens.length];
            
            int i = 0, lastPos = 0;
            while(tokenizer.hasMoreTokens()) {
                tokens[i] = tokenizer.nextToken();
                lastPos = value.indexOf(tokens[i], lastPos);
                positions[i] = lastPos;
                i++;
            }
        }
        
        public int getPosition(int index) {
            return positions[index];
        }

        public int getLength(int index, int count) {
            int endIndex = index + count - 1;
            int start = positions[index];
            int end = positions[endIndex] + tokens[endIndex].length();
            return end - start;
        }

        public int getRangeCount() {
            return tokens.length;
        }

        public boolean rangesEqual(int thisIndex, IRangeComparator other, int otherIndex) {
            return ((WordRangeComparator)other).getToken(otherIndex).equals(getToken(thisIndex));
        }

        private String getToken(int index) {
            return tokens[index];
        }

        public boolean skipRangeComparison(int length, int maxLength, IRangeComparator other) {
            return false;
        }
    }
}
