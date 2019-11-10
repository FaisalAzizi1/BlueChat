package com.foxslip.faisal.bluechat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String saparator = "__<>__";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DATA_CENTER_DATA1";

    //CONVERSATION TABLE
    private static final String TABLE_CONVERSATION = "conversation";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "times";
    private static final String KEY_MESSAGES= "messages";
    private static final String KEY_IS_ME = "isMe";
    private static final String KEY_ME = "me";
    private static final String KEY_OTHER = "other";

    public DatabaseHandler (Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_CONVERSATION = "CREATE TABLE "+ TABLE_CONVERSATION +" ("+KEY_ID+ " TEXT," +KEY_TIME+" TEXT,"
                +KEY_MESSAGES + " TEXT,"
                +KEY_IS_ME+" TEXT,"
                +KEY_ME + " TEXT,"
                +KEY_OTHER + " TEXT"
                +")";
        db.execSQL(CREATE_TABLE_CONVERSATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONVERSATION);
        onCreate(db);
    }

    public void addConveration(Conversation conversation, boolean isNew)
    {
        Log.d("DATABASE HANDLER m", "ADDCONVERSATION: "+conversation.getConversation().get(0).getMessage());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String isMe = "";
        for (int i = 0;i<conversation.getConversation().size();i++)
        {
            if (conversation.getConversation().get(i).isMe() == false)
            {
                isMe = isMe+"ELSE"+saparator;
            }
            else
                {
                    isMe = isMe+"ME"+saparator;
                }

        }

        Log.d("DATABASE HANDLER", "ADDCONVERSATION: "+isMe);

        String chatMessages = ArrayListToString(conversation.getConversation());

        values.put(KEY_ID,conversation.getId());
        values.put(KEY_TIME,conversation.getTimeAndDate());
        values.put(KEY_MESSAGES,chatMessages);
        values.put(KEY_IS_ME,isMe);
        values.put(KEY_ME,conversation.getMe());
        values.put(KEY_OTHER,conversation.getOther());

        if (isNew)
        {
            db.insert(TABLE_CONVERSATION,null,values);
        }
        else
            {
                db.update(TABLE_CONVERSATION, values, KEY_ID + " = ?",
                        new String[] { String.valueOf(conversation.getId()) });
            }


        Log.d("TAGGG", "addConveration: Added"+conversation.getMe());
        db.close();

    }

    public Conversation getConversation(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONVERSATION,new String[]{KEY_ID,KEY_TIME,KEY_MESSAGES,KEY_IS_ME,KEY_ME,KEY_OTHER},
                KEY_ID + "=?",new String[]{id},null,null,null,null);


        if (cursor != null)
            cursor.moveToFirst();

        List<String> isMe;
        List<String> messages;

        List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

        messages = new ArrayList<String>(Arrays.asList(cursor.getString(2).split(saparator)));
        isMe = new ArrayList<String>(Arrays.asList(cursor.getString(3).split(saparator)));
        

        for (int i = 0;i<messages.size();i++)
        {
            chatMessages.add(new ChatMessage("",messages.get(i).substring(messages.get(i).length() - 5),messages.get(i),(isMe.get(i).equals("ME")? true : false)));
        }

        Conversation conversation = new Conversation(cursor.getString(0)
                ,cursor.getString(1),chatMessages,isMe,cursor.getString(4)
                ,cursor.getString(5));

        return  conversation;
    }
    public List<Conversation> getAllConversations()
    {
        List<Conversation> conversations = new ArrayList<Conversation>();

        String selectQuery = "SELECT * FROM "+TABLE_CONVERSATION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);


        if (cursor.moveToFirst())
        {
            do
            {
                List<String> isMe;
                List<String> messages;

                List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

                messages = new ArrayList<String>(Arrays.asList(cursor.getString(2).split(saparator)));
                isMe = new ArrayList<String>(Arrays.asList(cursor.getString(3).split(saparator)));


                for (int i = 0;i<messages.size();i++)
                {
                    chatMessages.add(new ChatMessage("",messages.get(i),messages.get(i),(isMe.get(i).equals("ME")? true : false)));
                }

                Conversation conversation = new Conversation(cursor.getString(0)
                        ,cursor.getString(1),chatMessages,isMe,cursor.getString(4)
                        ,cursor.getString(5));
                conversations.add(conversation);
            }while (cursor.moveToNext());
        }
        return conversations;
    }
    private String ArrayListToString(List<ChatMessage> chats)
    {
        String s = "";
        for (int i = 0;i<chats.size();i++)
        {
            s = s+chats.get(i).getMessage()+saparator;
        }

        return s;
    }
    public void deleteConversation(Conversation conversation)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONVERSATION, KEY_ID + " = ?",
                new String[] { String.valueOf(conversation.getId()) });
        db.close();
    }
}