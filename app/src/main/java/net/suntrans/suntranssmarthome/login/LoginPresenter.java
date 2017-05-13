package net.suntrans.suntranssmarthome.login;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.suntrans.suntranssmarthome.App;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.utils.LogUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by Looney on 2017/4/19.
 */

public class LoginPresenter implements Login.Presenter {

    public LoginPresenter(Login.View view) {
        mView = checkNotNull(view);
    }

    private Login.View mView;

    @Override
    public void login(String username, String password) {
        if (username == null || username.equals("")) {
            mView.fieldEmpty("账号为不能为空");
            return;
        }
        if (password == null || password.equals("")) {
            mView.fieldEmpty("密码为不能为空");
            return;
        }
        mView.startLogin();
        RetrofitHelper.getLoginApi().login("password", "1", "559eb687a4fcafdabe991c320172fcc9", username, password)
                .compose(((RxAppCompatActivity) mView).<LoginResult>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoginResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFiled();
                    }

                    @Override
                    public void onNext(LoginResult loginResult) {
                        if (loginResult != null) {
                            LogUtil.i(loginResult.toString());
                            if (loginResult.access_token != null) {
                                App.getSharedPreferences().edit().putString("access_token", loginResult.access_token)
                                        .putString("expires_in", loginResult.expires_in)
                                        .putLong("firsttime", System.currentTimeMillis())
                                        .commit();
                                mView.loginSuccess();
                            } else {
                                mView.fieldEmpty(loginResult.error_description);
                            }

                        } else {
                            mView.loginFiled();
                        }
                    }
                });
    }

    @Override
    public void signUp() {

    }

    @Override
    public void forgetPassword() {
        LogUtil.i("forgetPassword");
    }

    @Override
    public void start() {

    }
}
