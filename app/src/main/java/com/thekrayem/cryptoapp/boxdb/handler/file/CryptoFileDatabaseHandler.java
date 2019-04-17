package com.thekrayem.cryptoapp.boxdb.handler.file;

import com.thekrayem.cryptoapp.boxdb.models.FileRecord;
import com.thekrayem.cryptoapp.helper.App;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.BoxStore;

public class CryptoFileDatabaseHandler implements FileDatabaseHandler {

    @Inject
    BoxStore boxStore;

    @Inject
    public CryptoFileDatabaseHandler() {
        App.getInstance().getAppComponent().inject(this);
    }


    @Override
    public List<FileRecord> getAllFiles() {
        return boxStore.boxFor(FileRecord.class).getAll();
    }

    @Override
    public void put(FileRecord fileRecord) {
        boxStore.boxFor(FileRecord.class).put(fileRecord);
    }

    @Override
    public void deleteById(long id) {
        boxStore.boxFor(FileRecord.class).remove(id);
    }

    @Override
    public FileRecord getFileById(long id) {
        return boxStore.boxFor(FileRecord.class).get(id);
    }
}
