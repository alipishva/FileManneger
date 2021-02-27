package com.alireza.filemanneger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddNewFolderDialog extends DialogFragment {

    private AddNewFolderCallBack callBack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callBack = (AddNewFolderCallBack) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_dialog_new_folder, null, false);
        TextInputEditText editText = view.findViewById(R.id.et_addNewFolder_dialog);
        TextInputLayout etl = view.findViewById(R.id.etl_addNewFolder);
        view.findViewById(R.id.btn_addNewFolder_dialog_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.length() > 0) {
                    callBack.onCreateFolderName(editText.getText().toString());
                    dismiss();
                } else {
                    etl.setError("Add name !");
                }
            }
        });

        return builder.setView(view).create();
    }

    public interface AddNewFolderCallBack {
        void onCreateFolderName(String folderName);
    }
}
