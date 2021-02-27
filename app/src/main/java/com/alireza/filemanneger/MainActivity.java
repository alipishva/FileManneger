package com.alireza.filemanneger;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.File;

public class MainActivity extends AppCompatActivity implements AddNewFolderDialog.AddNewFolderCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText etMainSearch = findViewById(R.id.et_main_search);

        etMainSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
                if (fragment instanceof FilesListFragment) {
                    ((FilesListFragment) fragment).search(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (StorageHelper.isExternalStorageReadable()) {
            File externalFilesDir = getExternalFilesDir(null);
            listFile(externalFilesDir.getPath(), false);
        }
        ImageView ivAddNewFolder = findViewById(R.id.img_main_addNewFolder);
        ivAddNewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewFolderDialog dialog = new AddNewFolderDialog();
                dialog.show(getSupportFragmentManager(), null);
            }
        });

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroup_main);
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == R.id.btn_main_list && isChecked) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
                    if (fragment instanceof FilesListFragment) {
                        ((FilesListFragment) fragment).setViewType(ViewType.ROW);
                    }
                } else if (checkedId == R.id.btn_main_grid && isChecked) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
                    if (fragment instanceof FilesListFragment) {
                        ((FilesListFragment) fragment).setViewType(ViewType.GRID);
                    }
                }
            }
        });
    }

    public void listFile(String path, boolean addToBackStack) {
        Bundle args = new Bundle();
        args.putString("path", path);
        FilesListFragment filesListFragment = new FilesListFragment();
        filesListFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main_fragmentContainer, filesListFragment, null);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void listFile(String path) {
        this.listFile(path, true);
    }

    @Override
    public void onCreateFolderName(String folderName) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
        if (fragment instanceof FilesListFragment) {
            ((FilesListFragment) fragment).createNewFolder(folderName);
        }
    }
}