/*
 * created 01.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: FileModelStorage.java 148 2006-01-30 20:40:59Z csell $
 */
package com.byterefinery.rmbench.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.util.xml.ModelReader;
import com.byterefinery.rmbench.util.xml.ModelWriter;

/**
 * a model storage that writes to an file, using the java.io API
 *  
 * @author cse
 */
public class FileModelStorage implements IModelStorage {

    private File file;
    private Model model;
    
    public FileModelStorage(String filePath) {
        this.file = new File(filePath);
    }

    public FileModelStorage(File file) {
        this.file = file;
    }

    public FileModelStorage(Model model) {
        this.model = model;
    }

    public FileModelStorage(File file, Model model) {
        this.file = file;
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public boolean isNew() {
        return file == null;
    }
    
    public void store() throws SystemException {
        if(model == null || file == null) {
            throw new IllegalStateException("model and file must be set");
        }
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            throw new SystemException(e);
        }
        ModelWriter.write(model, outStream);
    }

    public void load(LoadListener listener) throws SystemException {
        if(file == null)
            throw new IllegalStateException("file must be set");
        if(model != null)
            throw new IllegalStateException("model already set");
        
        try {
            FileInputStream inStream = new FileInputStream(file);
            model = ModelReader.read(inStream, file.getAbsolutePath(), listener);
        }
        catch (IOException e) {
            throw new SystemException(e);
        }
    }
}
