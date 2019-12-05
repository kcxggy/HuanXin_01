package zuoye01.jy.com.huanxin_01;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zuoye01.jy.com.huanxin_01.adpter.ChatAdapter;
import zuoye01.jy.com.huanxin_01.util.AudioUtil;
import zuoye01.jy.com.huanxin_01.util.ThreadManager;
import zuoye01.jy.com.huanxin_01.util.ToastUtil;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.tv_friend)
    TextView mTvFriend;
    @BindView(R.id.rlv)
    RecyclerView mRlv;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.btn_record)
    Button mBtnRecord;
    @BindView(R.id.btn_send_voice)
    Button mBtnSendVoice;
    @BindView(R.id.btn_voice_chat)
    Button mBtnVoiceChat;
    @BindView(R.id.btn_video_chat)
    Button mBtnVideoChat;
    private String friend;
    private ArrayList<EMMessage> mlist;
    private ChatAdapter chatAdapter;
    private String mPath;
    private long mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        initView();
        //聊天的功能 ：发 收


    }

    private void initView() {


        //和那个好友聊天textview中显示那个用户名
        Intent intent = getIntent();
        friend = intent.getStringExtra("friend");
        mTvFriend.setText("当前联系人:" + friend);

        //接收消息
        setReceiveMsgListener();

        //处理recyclerView
        mRlv.setLayoutManager(new LinearLayoutManager(this));
        mRlv.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        mlist = new ArrayList<>();
        chatAdapter = new ChatAdapter(mlist);
        mRlv.setAdapter(chatAdapter);

        chatAdapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void OnItemClickListener(View v, int pos) {
                ToastUtil.showShort("播放");
                //播放语音
                EMMessage emMessage = chatAdapter.list.get(pos);
                EMMessageBody body = emMessage.getBody();
                //对象 instanceof 类，判断对象是否是该类的类型
                if (body instanceof EMVoiceMessageBody){
                    EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) body;
                    String localUrl = voiceMessageBody.getLocalUrl();
                    if (!TextUtils.isEmpty(localUrl)){
                        startVoice(localUrl);
                    }
                }
            }
        });

    }

    private void startVoice(String localUrl) {

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(localUrl);
            mediaPlayer.prepare();//同步
//            mediaPlayer.prepareAsync();//异步
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setReceiveMsgListener() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            //处理消息把消息显示到界面上
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mlist.addAll(messages);

                    chatAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };


    @OnClick({R.id.btn_send, R.id.btn_record, R.id.btn_send_voice,
            R.id.btn_voice_chat, R.id.btn_video_chat})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_send:

                //发送文本消息
                sendTextMsg();

                break;
            case R.id.btn_record:

                //判断是否录音
                if (AudioUtil.isRecording){
                    //正在录音
                    AudioUtil.stopRecord();
                    mBtnRecord.setText("开始录音");
                }else {
                    AudioUtil.startRecord(new AudioUtil.ResultCallBack() {



                        @Override
                        public void onFail(String s) {
                            showToast(s);
                        }

                        @Override
                        public void onSuccess(String absolutePath, long duration) {
                            mPath = absolutePath;
                            mDuration = duration;
                        }
                    });
                    mBtnRecord.setText("停止录音");
                }

                break;
            case R.id.btn_send_voice:

                //发送语音消息
                sendVoiceMsg();

                break;
            case R.id.btn_voice_chat:
                break;
            case R.id.btn_video_chat:
                break;
        }
    }

    private void sendVoiceMsg() {

        if (TextUtils.isEmpty(mPath)){
            showToast("请先录音");
            return;
        }

        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //发送语音两步
                //1,录音  语音位置+语音时长
                //2,发送

                //filePath为语音文件路径，length为录音时间(秒)
                EMMessage message = EMMessage.createVoiceSendMessage(mPath, (int) mDuration, friend);
                //如果是群聊，设置chattype，默认是单聊
        /*if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);*/
                EMClient.getInstance().chatManager().sendMessage(message);

                //自己发送的消息需要添加到列表中显示
                addMsg(message);
            }
        });

    }

    private void sendTextMsg() {

        String content = mEtContent.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            showToast("发送内容不能为空");
            return;
        }


        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，
                // 后文皆是如此
                EMMessage message = EMMessage.createTxtSendMessage(content, friend);
                //如果是群聊，设置chattype，默认是单聊
                //if (chatType == CHATTYPE_GROUP)
                //    message.setChatType(EMMessage.ChatType.GroupChat);
                //发送消息
                EMClient.getInstance().chatManager().sendMessage(message);

                //自己发送的消息需要添加到列表中显示
                addMsg(message);

            }
        });

    }

    private void addMsg(EMMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mlist.add(message);
                mEtContent.setText("");
                chatAdapter.notifyDataSetChanged();
            }
        });

    }


    public void showToast(final String msg) {
        //切换主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(msg);
            }
        });
    }

}
