package com.alireza.filemanneger;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class FilesListFragment extends Fragment implements FileAdapter.FileItemListener {

    private static final String TAG = "FilesListFragment";
    private String path;
    private FileAdapter fileAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("path");
            Log.i(TAG, "onCreate: " + path);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvPath = view.findViewById(R.id.tv_files_path);
        recyclerView = view.findViewById(R.id.rv_files);
        ImageView imgBack = view.findViewById(R.id.iv_files_back);
        Context context;
        gridLayoutManager = new GridLayoutManager(getActivity(), 1, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        File currentFolder = new File(path);
        if (StorageHelper.isExternalStorageReadable()) {
            File[] fileList = currentFolder.listFiles();
            fileAdapter = new FileAdapter(Arrays.asList(fileList), this);
            recyclerView.setAdapter(fileAdapter);
        }

        tvPath.setText(currentFolder.getName().equalsIgnoreCase("files") ? "External Storage :" : currentFolder.getName());
    }

    public void search(String query) {
        if (fileAdapter != null) {
            fileAdapter.search(query);
        }
    }

    private void copy(File source, File destination) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(source);
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, length);
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    private File getDestination(String fileName) {
        return new File(getContext().getExternalFilesDir(null).getPath() + File.separator + "ali" + File.separator + fileName);
    }

    @Override
    public void onFileItemClicked(File file) {
        if (file.isDirectory()) {
            ((MainActivity) getActivity()).listFile(file.getPath());
        }
    }

    @Override
    public void onDeleteFileItemClicked(File file) {
        if (StorageHelper.isExternalStorageWritable()) {
            if (file.delete()) {
                fileAdapter.deleteFile(file);
            }
        }
    }

    @Override
    public void onCopyFileItemClicked(File file) {
        if (StorageHelper.isExternalStorageWritable()) {
            try {
                copy(file, getDestination(file.getName()));
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMoveFileItemClicked(File file) {
        if (StorageHelper.isExternalStorageWritable()) {
            try {
                copy(file, getDestination(file.getName()));
                onDeleteFileItemClicked(file);
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void createNewFolder(String folderName) {
        if (StorageHelper.isExternalStorageWritable()) {
            File newFolder = new File(path + File.separator + folderName);
            if (!newFolder.exists()) {
                if (newFolder.mkdir()) {
                    fileAdapter.addFile(newFolder);
                    recyclerView.scrollToPosition(0);
                }
            }
        }
    }

    public void setViewType(ViewType viewType) {
        if (fileAdapter != null) {
            fileAdapter.setViewType(viewType);
            if (viewType == ViewType.GRID) {
                gridLayoutManager.setSpanCount(2);
            } else {
                gridLayoutManager.setSpanCount(1);
            }
        }
    }

}