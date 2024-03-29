/*
 * created 11.02.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id$
 */
package com.byterefinery.rmbench.util;

import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

/**
 * This class accumulates the values configured by the print dialog screen. 
 * 
 * @author Thomas
 *
 */
public class PrintState {

    /** 
     * The selected printer object. If no printer information is given this value should fall back 
     * to the systems default printer. 
     */
    public PrinterData printer = Printer.getDefaultPrinterData();
    /** The additional margin arround the printing.*/
    public double margin = 0.0;
    /** The print mode. Correct values are: 
     * <ul>
     *      <li> PrintFigureOperation.TILE</li>
     *      <li> PrintFigureOperation.FIT_PAGE</li>
     *      <li> PrintFigureOperation.FIR_WIDTH</li>
     *      <li> PrintFigureOperation.FIT_HEIGHT</li>
     * </u>
     */
    public int mode = PrintFigureOperation.TILE;
}
