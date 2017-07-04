package net.suntrans.smarthome.model.add;

/**
 * Created by Looney on 2017/4/21.
 */

public class AddPresenter implements AddContract.Presenter {
    private AddContract.View view;

    public AddPresenter(AddContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {
    }

    @Override
    public void createModel() {
        view.onCreateModel();
    }

    @Override
    public void openGallery() {
        view.onOpenGallery();

    }
}


