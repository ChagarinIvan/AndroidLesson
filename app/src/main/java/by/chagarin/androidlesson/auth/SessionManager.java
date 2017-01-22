package by.chagarin.androidlesson.auth;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.io.IOException;

@EBean(scope = EBean.Scope.Singleton)
public class SessionManager {
    private static final String AUTH_ACCOUNT_TUPE = "by.chagarin.androidlesson";
    private static final String AUTH_TOKEN_TYPE_FULL_ACCES = AUTH_ACCOUNT_TUPE + ".authToken";
    public static final String SESSION_OPEN_BROADCAST = "open-broadcast";

    @SystemService
    AccountManager accountManager;

    @RootContext
    Context context;

    public void createAccount(String login, String authToken) {
        Account account = new Account(login, AUTH_ACCOUNT_TUPE);
        accountManager.addAccountExplicitly(account, null, null);
    }

    @Background
    public void login() {
        android.accounts.Account[] availableAccounts = accountManager.getAccountsByType(AUTH_ACCOUNT_TUPE);
        if (availableAccounts.length == 0) {
            return;
        }
        AccountManagerFuture<Bundle> future = accountManager.getAuthToken(availableAccounts[0], AUTH_TOKEN_TYPE_FULL_ACCES, null, false, null, null);
        try {
            future.getResult();
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SESSION_OPEN_BROADCAST));
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }
    }
}
