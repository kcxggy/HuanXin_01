package zuoye01.jy.com.huanxin_01;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import zuoye01.jy.com.huanxin_01.adpter.MyAdapter;
import zuoye01.jy.com.huanxin_01.util.ThreadManager;
import zuoye01.jy.com.huanxin_01.util.ToastUtil;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.tvUser)
    TextView mTvUser;
    @BindView(R.id.rlv)
    RecyclerView mRlv;
    private MyAdapter adapter;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initPar();
        initData();

        initLiebiao();



    }

    private void initPar() {

        String[] pers = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        };
        ActivityCompat.requestPermissions(this,pers,100);

    }


    private void initLiebiao() {
        mRlv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter();
        mRlv.setAdapter(adapter);
        mRlv.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClickListener(int pos) {
                go2Chat(adapter.mData.get(pos));
            }
        });



    }

    //跳转到单聊页面
    private void go2Chat(String s) {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend",s);
        startActivity(intent);

    }

    private void initData() {

        //获取当前登录用户
        String currentUser = EMClient.getInstance().getCurrentUser();
        mTvUser.setText(currentUser);

        haoyou();
    }

    private void haoyou() {

        //获取好友列表
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setData(usernames);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 0, "退出登录");
        menu.add(1, 2, 0, "群聊");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                logout();
                break;
            case 2:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("退出登录成功");
                    }
                });
                go2LoginActivity();


            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("退出失败");
                    }
                });
            }
        });
    }


    private void go2LoginActivity() {

        startActivity(new Intent(this,LoginActivity.class));

    }


}
