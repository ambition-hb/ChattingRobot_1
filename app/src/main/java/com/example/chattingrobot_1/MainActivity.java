package com.example.chattingrobot_1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{

    private List<Msg> msgList = new ArrayList<>();

    private EditText inputText;

    private Button sendRequest;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMsgs();//初始化消息数据
        inputText = (EditText)findViewById(R.id.input_text);
        sendRequest = (Button)findViewById(R.id.send_request);
        msgRecyclerView = (RecyclerView)findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
//                Log.d("content", content);//ok
                String content_1 = content.replace(" ","");
                String content_2 = content_1.replace("\n","");
//                Log.d("content_2", content_2);//ok

                String message = null;
                try{
                    message = java.net.URLEncoder.encode(content_2, "UTF-8");
                }catch (Exception e){
                    e.printStackTrace();
                }


                String contentUrl = "http://www.tuling123.com/openapi/api?key=d2b1eedeaee7415ab61bfbc0b6865a3e&info=" + message;
//                Log.d("contentUrl", contentUrl);//ok
                final String Url =contentUrl;
//                Log.d("URL", Url);//ok
                if (!"".equals(content)){
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    //当有新消息时，刷新listview中的显示
                    adapter.notifyItemInserted(msgList.size() - 1);
                    //将ListView定位到最后一行
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    //清空输入框中的内容
                    inputText.setText("");
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection = null;
                        BufferedReader reader = null;
                        try{
                            URL url = new URL(Url);
                            Log.d("url:", url.toString());//ok
                            connection = (HttpURLConnection)url.openConnection();
                            InputStream in = connection.getInputStream();
//                            Log.d("in", in.toString());//ok
                            //下面对获取到的输入流进行读取
                            reader = new BufferedReader(new InputStreamReader(in));
//                            Log.d("reader:", reader.toString());//ok
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null){
                                response.append(line);
                            }
                            Log.d("response", response.toString());//ok
                            //按指定模式在字符串查找,只需要XXXX内容
                            //{"code":100000,"text":"XXXX"}
                            String str = response.toString();
                            String pattern = "\"(.*?)\"";
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(str);
                            int i=0;
                            while (m.find()){
//                                System.out.println(m.group(1));
                                str = m.group(1);
                            }
//                            Log.d("str", str);//ok
                            Msg msg1 = new Msg(str, Msg.TYPE_RECEIVED);
                            msgList.add(msg1);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            if (reader != null){
                                try {
                                    reader.close();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                            if (connection != null){
                                connection.disconnect();
                            }
                        }
                    }
                }).start();

            }
        });
    }



    private void initMsgs(){
        Msg msg1 = new Msg("您好！这里是浩比机器人，请问您有什么问题需要我帮助吗？", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
    }

}
