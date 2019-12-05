package zuoye01.jy.com.huanxin_01;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zuoye01.jy.com.huanxin_01.util.ThreadManager;
import zuoye01.jy.com.huanxin_01.util.ToastUtil;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_psd)
    EditText mEtPsd;
    @BindView(R.id.btn_register)
    Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_register)
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }

    private void register() {

        String name = mEtName.getText().toString().trim();
        String pwd = mEtPsd.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)){
            ToastUtil.showShort("用户名或密码不能为空");
            return;
        }

        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //注册失败会抛出HyphenateException
                try {
                    EMClient.getInstance().createAccount(name, pwd);//同步方法
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort("注册成功");
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

