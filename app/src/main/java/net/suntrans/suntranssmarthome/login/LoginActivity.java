package net.suntrans.suntranssmarthome.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import com.androidadvance.topsnackbar.TSnackbar;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import net.suntrans.suntranssmarthome.App;
import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.databinding.ActivityLoginBinding;
import net.suntrans.suntranssmarthome.homepage.MainActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.StatusBarCompat;
import net.suntrans.suntranssmarthome.widget.LoadingDialog;

public class LoginActivity extends RxAppCompatActivity implements Login.View {
    private LoginPresenter presenter;
    private LoginModel info;
    private LoadingDialog dialog;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((!isTaskRoot()) && (getIntent().hasCategory("android.intent.category.LAUNCHER")) && (getIntent().getAction() != null) && (getIntent().getAction().equals("android.intent.action.MAIN")))
        {
            finish();
            return;
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        String username = App.getSharedPreferences().getString("username", "");
        String password = App.getSharedPreferences().getString("password", "");
        info = new LoginModel(username, password);
        presenter = new LoginPresenter(this);
        binding.setUser(info);
        binding.setActionHandler(presenter);


    }

    @Override
    public void loginSuccess() {
        LogUtil.i("登录成功");
        dialog.dismiss();
        App.getSharedPreferences().edit().putString("username", info.getUsername())
                .putString("password", info.getPassword()).commit();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void loginFiled() {
        View view = findViewById(android.R.id.content);

        dialog.dismiss();
        showMessage(view,getString(R.string.tips_login_failed));
    }

    @Override
    public void startLogin() {
        if (dialog == null)
            dialog = new LoadingDialog(this, R.style.loading_dialog);
        dialog.setWaitText("正在登录...");
        dialog.show();
    }

    @Override
    public void fieldEmpty(String msg) {
        View view = findViewById(android.R.id.content);
        if (msg.equals("账号为不能为空")) {
            showMessage(view,getResources().getString(R.string.tips_empty_account));
        } else if (msg.equals("密码为不能为空")) {
            showMessage(view,getResources().getString(R.string.tips_empty_password));
        } else {
        showMessage(view,msg);
        }

    }

    private  void showMessage(View view,String msg) {
        TSnackbar snackbar = TSnackbar.make(view, msg, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(R.drawable.ic_info,24);
        View snackbarView = snackbar.getView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            snackbarView.setPadding(0,StatusBarCompat.getStatusBarHeight(this),0,0);
        }
        snackbarView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        snackbar.show();
    }

    @Override
    public void setPresenter(Login.Presenter presenter) {
    }
}
