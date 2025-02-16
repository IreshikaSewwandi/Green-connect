package com.example.greenconnect.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hi L.E.G.Balasooriya , welcome to Green Connect ! How can I help you today?");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
