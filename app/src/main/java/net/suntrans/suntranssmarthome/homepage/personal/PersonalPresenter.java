package net.suntrans.suntranssmarthome.homepage.personal;

import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.utils.LogUtil;

import android.content.Context;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/4/19.
 */

public class PersonalPresenter implements PersonalContract.Presenter {

    private final Context context;
    private PersonalContract.View mView;

    public PersonalPresenter(PersonalContract.View view, Context context) {
        mView = view;
        mView.setPresenter(this);
        this.context = context;
    }

    @Override
    public void start() {
        RetrofitHelper.getApi().getUserInfo()
                .compose(((RxAppCompatActivity) context).<UserInfo>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UserInfo info) {
                        if (info != null) {
                            if (info.status.equals("1")) {
                                mView.getDataSuccess(info);
                                LogUtil.i(info.toString());
                            } else {

                            }
                        } else {

                        }
                    }
                });
    }

    @Override
    public void openUserinfoPage() {
        mView.openUserinfoUI();
    }

    @Override
    public void openSuggestionsPage() {
        mView.openSuggestionsUI();
    }

    @Override
    public void openUpdatePage() {
        mView.openUpdateUI();
    }

    @Override
    public void openAboutPage() {
        mView.openAboutUI();
    }

    @Override
    public void openHelpPage() {
        mView.openHelpUI();

    }

    @Override
    public void openSettingPage() {
        mView.openSettingUI();

    }
    @Override
    public void onLoginOut(){
        mView.onLoginOut();
    }

    @Override
    public void openDeviceManagerPage() {
        mView.openDeviceManagerUI();
    }
}
