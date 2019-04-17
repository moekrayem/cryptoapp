package com.thekrayem.cryptoapp.helper;

import java.io.File;
import java.io.StringReader;

public class StaticValues {

    public static final String DB_NAME="CRYPTOAPPDB";

    public static final String DEFAULT_SAVE_FOLDER_ENC = "CryptoApp Files" + File.separator + "ENC";
    public static final String DEFAULT_SAVE_FOLDER_PLAIN = "CryptoApp Files" + File.separator + "PLAIN";


    public static final int DEFAULT_ITERATIONS = 5000;
    public static final int DEFAULT_KEY_LENGTH = 256;

    public static final int DEFAULT_CHAT_KEY_LENGTH = 200;

    public class RequestCodes{

        public static final int SELECT_FILE_ENCRYPTION_REQUEST_CODE=420;
        public static final int CREATE_NEW_CHAT_REQUEST_CODE=421;
        public static final int ENCRYPT_CHAT_REQUEST_CODE = 422;
        public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 423;
        public static final int SELECT_FILE_TO_LINK_REQUEST_CODE = 424;
    }

    public class IntentExtraValues{

        public static final String CHAT_USER_ID_INTENT_EXTRA_VALUE = "CHAT.USER.ID.INTENT.EXTRA.VALUE";
        public static final String USER_NAME_INTENT_EXTRA_VALUE = "USER.NAME.INTENT.EXTRA.VALUE";

        public static final String ENCRYPT_CHAT_USER_ID_INTENT_EXTRA_VALUE = "ENCRYPT_CHAT_USER_ID";
        public static final String CHAT_DETAILS_BROADCAST_RECEIVER_COMMAND = "CHAT_DETAILS_BROADCAST_COMMAND";
        public static final int CHAT_DETAILS_BROADCAST_RECEIVER_NEW_MESSAGE = 1000;
        public static final String CHAT_DETAILS_BROADCAST_RECEIVER_DATA = "CHAT_DETAILS_BROADCAST_DATA";
        public static final String CHAT_DETAILS_BROADCAST_RECEIVER_FILTER = "CHAT_DETAILS_BROADCAST_FILTER";
        public static final String CHAT_DETAILS_BROADCAST_RECEIVER_MESSAGE_ID = "CHAT_DETAILS_BROADCAST_MESSAGE_ID";
    }

    public class ServerValues{


        public static final int SERVER_REPLY_NEW_MESSAGE = 1;




        public static final String REQUEST_HEADER_COMMAND = "Command";

        // Reminder: server response is 1 or 0
        public static final String RESPONSE_HEADER_RESULT_KEY = "Result";


        public static final int SERVER_COMMAND_TEST = 1;
        public static final int SERVER_COMMAND_NEW_TOKEN = 2;
        public static final int SERVER_COMMAND_CHECK_TOKEN = 3;
        public static final int SERVER_COMMAND_NEW_MESSAGE = 4;
    }

    public class JSONTags{

        public class Firebase{
            public static final String INSTANCE_ID_FROM = "ins";
            public static final String TOKEN_FROM = "tok";
        }

        public class Server{
            public static final String COMMAND = "com";
            public static final String MESSAGE_CONTENT = "con";
            public static final String MESSAGE_REAL_CONTENT = "real";
            public static final String MESSAGE_SENDER_ID = "sen";
            public static final String MESSAGE_RECEIVER_ID = "rec";
        }

        public class KeyBitmap{
            public static final String KEY = "key";
            public static final String ID = "id";
        }

        public class EncryptChat{
            public static final String ENCRYPT_CHAT_ID = "id";
            public static final String ENCRYPT_CHAT_NAME = "name";
            public static final String ENCRYPT_CHAT_USER_OBJECT = "user_object";
            public static final String ENCRYPT_CHAT_MESSAGE_TIME = "message_time";
            public static final String ENCRYPT_CHAT_MESSAGE_CONTENT = "message_content";
            public static final String ENCRYPT_CHAT_CHAT = "chat";
        }
    }
}
