package com.thekrayem.cryptoapp.boxdb.handler.file;

import com.thekrayem.cryptoapp.boxdb.models.FileRecord;

import java.util.List;

public interface FileDatabaseHandler {

    List<FileRecord> getAllFiles();

    void put(FileRecord fileRecord);

    void deleteById(long id);

    FileRecord getFileById(long id);
}
