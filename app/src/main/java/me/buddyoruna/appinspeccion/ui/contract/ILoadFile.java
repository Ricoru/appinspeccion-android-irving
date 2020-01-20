package me.buddyoruna.appinspeccion.ui.contract;

/**
 * Created by Ricoru on 19/02/18.
 */

public interface ILoadFile {

    void success(String file);
    void exception(Throwable throwable);

}
