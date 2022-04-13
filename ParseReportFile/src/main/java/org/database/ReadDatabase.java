package org.database;

import org.source.ReadSource;

public interface ReadDatabase extends ReadSource, ManageDatabase {
    void readSource(String sourcePath);
}
