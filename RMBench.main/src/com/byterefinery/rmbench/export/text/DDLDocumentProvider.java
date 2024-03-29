/*
 * created 04.01.2006
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
package com.byterefinery.rmbench.export.text;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import com.byterefinery.rmbench.export.DDLEditorInput;
import com.byterefinery.rmbench.export.ModelCompareEditorInput;

/**
 * configures new documents for partitioning into DDL partition types, which are
 * later used in syntax coloring
 * 
 * @author cse
 */
public class DDLDocumentProvider extends FileDocumentProvider {

    protected IDocument createDocument(Object element) throws CoreException {
        IDocument document = super.createDocument(element);
        if (document != null) {
            IDocumentPartitioner partitioner =
                new FastPartitioner(
                    new DDLPartitionScanner(),
                    DDLSourceViewerConfiguration.CONTENT_TYPES);
            partitioner.connect(document);
            document.setDocumentPartitioner(partitioner);
        }
        return document;
    }

    protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
        if (element instanceof DDLEditorInput || element instanceof ModelCompareEditorInput) {
            //DDLEditor needs an AM so the vertical bar is shown
            //ModelCompareEditor uses it for synchronizing element selection
            return new AnnotationModel();
        }
        return super.createAnnotationModel(element);
    }
}
