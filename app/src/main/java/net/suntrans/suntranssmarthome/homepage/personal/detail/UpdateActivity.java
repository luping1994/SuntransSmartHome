package net.suntrans.suntranssmarthome.homepage.personal.detail;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.add.CreateModelResult;
import net.suntrans.suntranssmarthome.utils.UiUtils;
import net.suntrans.suntranssmarthome.widget.LoadingDialog;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/4/20.
 */

public class UpdateActivity extends BasedActivity implements View.OnClickListener {

    private EditText oldPassword;
    private EditText newPassword;
    private EditText rePassword;
    private Button commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setUpToolbar();
        init();
    }

    private void init() {
        oldPassword = (EditText) findViewById(R.id.oldpass);
        newPassword = (EditText) findViewById(R.id.newpass);
        rePassword = (EditText) findViewById(R.id.repass);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_modify_pass);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.commit) {

            String oldpass = oldPassword.getText().toString();
            String newpass = newPassword.getText().toString();
            String repass = rePassword.getText().toString();
            if (TextUtils.isEmpty(oldpass)) {
                UiUtils.showMessage(findViewById(android.R.id.content), "请输入旧密码");
                return;
            }
            if (TextUtils.isEmpty(newpass)) {
                UiUtils.showMessage(findViewById(android.R.id.content), "请输入新密码");
                return;
            }
            if (TextUtils.isEmpty(repass)) {
                UiUtils.showMessage(findViewById(android.R.id.content), "请确认新密码");
                return;
            }
            if (!repass.equals(newpass)) {
                UiUtils.showMessage(findViewById(android.R.id.content), "两次输入的密码不同");
                return;
            }
            final LoadingDialog dialog = new LoadingDialog(this,R.style.loading_dialog);
            dialog.setWaitText("请稍后...");
            dialog.show();
            RetrofitHelper.getApi().modifyPass(oldpass, newpass, repass)
                    .compose(this.<CreateModelResult>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<CreateModelResult>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            dialog.dismiss();

                        }

                        @Override
                        public void onNext(CreateModelResult result) {
                            dialog.dismiss();
                            if (result != null)
                                UiUtils.showMessage(findViewById(android.R.id.content), result.msg);

                        }
                    });
        }
    }
}
