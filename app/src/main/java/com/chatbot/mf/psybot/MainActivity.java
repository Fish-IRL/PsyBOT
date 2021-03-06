package com.chatbot.mf.psybot;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;
import com.chatbot.mf.psybot.ChatMessageAdapter;
import com.chatbot.mf.psybot.model.ChatMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public Bot bot;
    public static Chat chat;

    private ListView mListView;
    private EditText mEditTextMessage;
    private ImageButton mButtonSend;
    private ChatMessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wireView();
        setSendButtonListener();
        handleFileStream();
        generateBot();

        mimicOtherMessage(getString(R.string.hello));
        mimicOtherMessage(getString(R.string.welcome_message));
        mimicOtherMessage(getString(R.string.help));
    }

    private void wireView(){
        mListView = findViewById(R.id.listView);
        mButtonSend = findViewById(R.id.btn_send);
        mEditTextMessage = findViewById(R.id.et_message);

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
    }

    private void setSendButtonListener(){
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                //bot
                String response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mimicOtherMessage(response);

                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }

    private void handleFileStream(){
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(getCacheDir().toString() + "/psybot/bots/PsyBOT");
//        File jayDir = new File(getCacheDir().toString() + "/chatbot/bots/PsyBOT");
        boolean b = jayDir.mkdirs(); //create dir to be copied to cache dir
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("PsyBOT")) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    System.out.println("Output : " + subdir_check);
                    for (String file : assets.list("PsyBOT/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("PsyBOT/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        // copy file to cache dir
                        copyFile(in, out);
                        in.close();
                        out.flush();
                        out.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateBot(){
        //get the working directory
        MagicStrings.root_path = getCacheDir().toString() + "/psybot";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("PsyBOT", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

//    private void sendMessage() {
//        ChatMessage chatMessage = new ChatMessage(null, true, true);
//        mAdapter.add(chatMessage);
//
//        mimicOtherMessage();
//    }
//
//    private void mimicOtherMessage() {
//        ChatMessage chatMessage = new ChatMessage(null, false, true);
//        mAdapter.add(chatMessage);
//    }

    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}