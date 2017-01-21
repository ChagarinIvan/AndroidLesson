package by.chagarin.androidlesson.auth;


import android.accounts.Account;
import android.accounts.AccountManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

@EBean(scope = EBean.Scope.Singleton)
public class SessionManager {
    private static final String AUTH_ACCOUNT_TUPE = "by.chagarin.androidlesson";

    @SystemService
    AccountManager accountManager;

    public void createAccount(String login, String authToken) {
        Account account = new Account(login, AUTH_ACCOUNT_TUPE);
        accountManager.addAccountExplicitly(account, null, null);
    }
}
