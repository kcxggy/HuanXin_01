package zuoye01.jy.com.huanxin_01.util;


import android.widget.Toast;

import zuoye01.jy.com.huanxin_01.BaseApp;


/**
 * @author xts
 *         Created by asus on 2019/8/30.
 */

public class ToastUtil {

    public static void showShort(String msg){
        Toast.makeText(BaseApp.sBaseApp,msg, Toast.LENGTH_SHORT).show();
    }
}
