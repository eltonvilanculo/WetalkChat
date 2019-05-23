package com.adilson.wetalk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class UserDataFragment extends Fragment {
    String TAG = getClass().getSimpleName();
    private TextView nameTxt;
    private TextView emailTxt;
    private User currentUser;
    private TextView phoneTxt;
    private TextView genderTxt;

    public static UserDataFragment newInstance(User user) {
        Bundle args = new Bundle();

        Log.d("UserDataFragment", "user: " + user);

        args.putParcelable(ChatRoomActivity.USER, user);
        UserDataFragment fragment = new UserDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_data, container, false);

        //image = view.findViewById(R.id.)
        nameTxt = view.findViewById(R.id.profile_name);
        emailTxt = view.findViewById(R.id.profile_email);
        phoneTxt = view.findViewById(R.id.profile_phone);
        genderTxt = view.findViewById(R.id.profile_gender);

        initData();

        return view;
    }

    private void initData() {
        try {
            currentUser = getArguments().getParcelable(ChatRoomActivity.USER);
            Log.d(TAG, "initData: " + currentUser);

            nameTxt.setText(currentUser.getName());
            emailTxt.setText(currentUser.getEmail());
            phoneTxt.setText(currentUser.getPhoneNumber());
            genderTxt.setText(printGender(currentUser.isMale()));

            if (currentUser.isMale()) {

            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String printGender(boolean male) {
        if (male)
            return "Masculino";
        else return "Feminino";
    }
}
