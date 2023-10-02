package org.parsers;

import java.io.IOException;
import java.util.Collection;

public abstract class FileParser<T> implements Parser<T> {

    protected String fileName;
    public FileParser(String fileName){
        this.fileName = fileName;
    }
    public abstract void save(Collection<T> data) throws IOException;
}
