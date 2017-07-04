package net.suntrans.smarthome.login;

import net.suntrans.smarthome.model.BaseView;
import net.suntrans.smarthome.presenter.BasePresenter;

/**
 * Created by Looney on 2017/4/19.
 */

public interface Login {
    interface View extends BaseView<Presenter> {

        void loginSuccess();

        void loginFiled();

        void startLogin();

        void fieldEmpty(String msg);
    }

    interface Presenter extends BasePresenter {
        void login(String username, String password);
        void signUp();
        void forgetPassword();
    }
}
