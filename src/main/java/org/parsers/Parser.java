package org.parsers;

import java.io.IOException;
import java.util.List;

public interface Parser<T> {
    List<T> parse() throws IOException;
}
