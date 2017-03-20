package by.chagarin.androidlesson.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.DataLoader_;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.MainActivity;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.fragments.fragments.NonSwipeableViewPager;
import by.chagarin.androidlesson.fragments.fragments.PlacesCategores;
import by.chagarin.androidlesson.fragments.fragments.ProceedCategores;
import by.chagarin.androidlesson.fragments.fragments.TransactionsCategores;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.User;

@EFragment(R.layout.fragment_categores)
public class CategoresFragment extends android.app.Fragment {
    private FragmentActivity myContext;
    private DataLoader_ loader;
    private boolean isCheck;

    @AfterViews
    void afterView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.actualFragment = this;
        mainActivity.setTitle(R.string.categores);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_categores, container, false);
        loader = DataLoader_.getInstance_(getActivity());
        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.transaction_categores)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.categores_proceed)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.places_categores)));
        final NonSwipeableViewPager viewPager = (NonSwipeableViewPager) inflatedView.findViewById(R.id.viewpager);
        FragmentManager fragManager = myContext.getSupportFragmentManager();
        viewPager.setAdapter(new PagerAdapter(fragManager, tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) inflatedView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
        return inflatedView;
    }

    private void alertDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.add_categores)
                .positiveText(R.string.ok_button)
                .content(R.string.chose_content)
                .items((CharSequence[]) KindOfCategories.getKinds())
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        return true;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        final int kind = dialog.getSelectedIndex();
                        MaterialDialog.Builder newBuilder = new MaterialDialog.Builder(getActivity())
                                .title(R.string.add_categores)
                                .positiveText(R.string.save)
                                .content(R.string.input_content)
                                .inputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                .inputRange(2, 20)
                                .input(R.string.hint_category_exemple, 0, false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                    }
                                })
                                .negativeText(R.string.dont_save)
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                                        final String userId = DataLoader.getUid();
                                        loader.mDatabase.child(DataLoader.USERS).child(userId).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        // Get user value
                                                        User user = dataSnapshot.getValue(User.class);

                                                        // [START_EXCLUDE]
                                                        if (user == null) {
                                                            // User is null, error out
                                                            Toast.makeText(getActivity(),
                                                                    "Error: could not fetch user.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            // Write new postString name, String kind, String author, boolean isShowIt, String key
                                                            String key = loader.mDatabase.child(DataLoader.CATEGORIES + "/" + KindOfCategories.getLatinKinds()[kind]).push().getKey();
                                                            String isShow;
                                                            if (isCheck) {
                                                                isShow = "not";
                                                            } else {
                                                                isShow = "yes";
                                                            }
                                                            //noinspection ConstantConditions
                                                            Category category = new Category(dialog.getInputEditText().getText().toString(),
                                                                    KindOfCategories.getKinds()[kind],
                                                                    userId,
                                                                    isShow,
                                                                    key);
                                                            loader.writeNewCategory(category, DataLoader.CATEGORIES + "/" + KindOfCategories.getLatinKinds()[kind] + "/" + key);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        // [START_EXCLUDE]
                                                    }
                                                });
                                        // [END single_value_read]
                                    }
                                });
                        if (TextUtils.equals(KindOfCategories.getKinds()[kind], KindOfCategories.getPlace())) {
                            newBuilder.checkBoxPrompt(getString(R.string.copilka), false, new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    isCheck = isChecked;
                                }
                            });
                        }
                        newBuilder.build().show();
                    }
                })
                .negativeText(R.string.cancel_button)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TransactionsCategores tab1 = new TransactionsCategores();
                    return tab1;
                case 1:
                    ProceedCategores tab2 = new ProceedCategores();
                    return tab2;
                case 2:
                    PlacesCategores tab3 = new PlacesCategores();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
