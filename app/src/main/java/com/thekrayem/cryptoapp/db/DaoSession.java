package com.thekrayem.cryptoapp.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.thekrayem.cryptoapp.db.file;
import com.thekrayem.cryptoapp.db.chat_user;
import com.thekrayem.cryptoapp.db.chat_message;

import com.thekrayem.cryptoapp.db.fileDao;
import com.thekrayem.cryptoapp.db.chat_userDao;
import com.thekrayem.cryptoapp.db.chat_messageDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig fileDaoConfig;
    private final DaoConfig chat_userDaoConfig;
    private final DaoConfig chat_messageDaoConfig;

    private final fileDao fileDao;
    private final chat_userDao chat_userDao;
    private final chat_messageDao chat_messageDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        fileDaoConfig = daoConfigMap.get(fileDao.class).clone();
        fileDaoConfig.initIdentityScope(type);

        chat_userDaoConfig = daoConfigMap.get(chat_userDao.class).clone();
        chat_userDaoConfig.initIdentityScope(type);

        chat_messageDaoConfig = daoConfigMap.get(chat_messageDao.class).clone();
        chat_messageDaoConfig.initIdentityScope(type);

        fileDao = new fileDao(fileDaoConfig, this);
        chat_userDao = new chat_userDao(chat_userDaoConfig, this);
        chat_messageDao = new chat_messageDao(chat_messageDaoConfig, this);

        registerDao(file.class, fileDao);
        registerDao(chat_user.class, chat_userDao);
        registerDao(chat_message.class, chat_messageDao);
    }
    
    public void clear() {
        fileDaoConfig.getIdentityScope().clear();
        chat_userDaoConfig.getIdentityScope().clear();
        chat_messageDaoConfig.getIdentityScope().clear();
    }

    public fileDao getFileDao() {
        return fileDao;
    }

    public chat_userDao getChat_userDao() {
        return chat_userDao;
    }

    public chat_messageDao getChat_messageDao() {
        return chat_messageDao;
    }

}
