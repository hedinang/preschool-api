package org.example.preschool.util.constant;

public class DBCollectionFieldName {

    public class BaseEntity {
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
        public static final String STATUS = "status";

    }

    public class Conversation {
        public static final String CONVERSATION_ID = "conversation_id";
        public static final String LAST_MESSAGE_TS = "last_message_ts";
        public static final String TYPE = "type";
        public static final String STATUS = "status";

    }

    public class UserConversation {
        public static final String CONVERSATION_ID = "conversation_id";
        public static final String CONVERSATION_NAME = "conversation_name";
        public static final String MEMBERS_NAME = "members_name";
        public static final String UNREAD_COUNT = "unread_count";
        public static final String TYPE = "type";
        public static final String LABEL_ID = "label_id";
        public static final String STARRED = "starred";
        public static final String USER_ID = "user_id";
        public static final String LAST_SEEN_TS = "last_seen_ts";
        public static final String ROLE = "role";

    }

    public class Message {
        public static final String MESSAGE_ID = "message_id";
        public static final String CONTENT = "content";
        public static final String CONVERSATION_ID = "conversation_id";
        public static final String RECIPIENT_ID = "recipient_id";
        public static final String NOTIFICATION_ID = "notification_id";
    }

    public class Organization {
        public static final String OBJECT_ID = "object_id";
        public static final String PARENT_ID = "parent_id";
        public static final String NAME = "name";
        public static final String TYPE = "type";

    }

    public class OrganizationUser {
        public static final String OBJECT_ID = "object_id";
        public static final String PARENT_ID = "parent_id";
        public static final String NAME = "name";
        public static final String TYPE = "type";

    }

    public class User {
        public static final String USER_ID = "user_id";
        public static final String NAME = "name";
        public static final String BIRTHDAY = "birthday";
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String LANGUAGE = "language";
    }

    public class Resource {
        public static final String RESOURCE_ID = "resource_id";
        public static final String PATH = "path";
        public static final String USER_ID = "user_id";
        public static final String CONVERSATION_ID = "conversation_id";
        public static final String FOLDER = "folder";
        public static final String DATE = "date";
    }

}
