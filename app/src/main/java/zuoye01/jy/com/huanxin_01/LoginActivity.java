package zuoye01.jy.com.huanxin_01;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import zuoye01.jy.com.huanxin_01.util.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_psd)
    EditText mEtPsd;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.btn_register)
    Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果已经登录过了直接进入主页面

        if (EMClient.getInstance().isLoggedInBefore()) {
            go2MainActivity();
            finish();
        }


        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @butterknife.OnClick({R.id.btn_login, R.id.btn_register})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                go2Register();
                break;
        }
    }

    private void go2Register() {

        startActivity(new Intent(this,RegisterActivity.class));

    }

    private void login() {

        String name = mEtName.getText().toString().trim();
        String psd = mEtPsd.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(psd)){
            ToastUtil.showShort("用户名或密码不能为空");
            return;
        }


        EMClient.getInstance().login(name,psd,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("登录成功");
                    }
                });
                go2MainActivity();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("登录失败");
                    }
                });
            }
        });

    }

    private void go2MainActivity() {
        startActivity(new Intent(this,MainActivity.class));
    }
}
