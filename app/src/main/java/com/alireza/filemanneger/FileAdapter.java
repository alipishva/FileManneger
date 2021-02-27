package com.alireza.filemanneger;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<File> fileList;
    private FileItemListener fileItemListener;
    private List<File> filterFileList;
    private ViewType viewType = ViewType.ROW;

    public FileAdapter(List<File> fileList, FileItemListener fileItemListener) {
        this.fileList = new ArrayList<>(fileList);
        this.fileItemListener = fileItemListener;
        this.filterFileList = fileList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(viewType == ViewType.ROW.getValue() ? R.layout.item_file : R.layout.item_file_gride, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return viewType.getValue();
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.bindFile(filterFileList.get(position));
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
        notifyDataSetChanged();
    }

    public void addFile(File file) {
        fileList.add(0, file);
        notifyItemInserted(0);

    }

    public void search(String query) {
        if (query.length() > 0) {
            List<File> result = new ArrayList<>();
            for (File file : this.fileList) {
                if (file.getName().toLowerCase().contains(query.toLowerCase())) {
                    result.add(file);
                }
            }
            this.filterFileList = result;
            notifyDataSetChanged();
        } else {
            this.filterFileList = fileList;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return filterFileList.size();
    }

    public void deleteFile(File file) {
        int index = fileList.indexOf(file);
        if (index > -1) {
            fileList.remove(file);
            notifyItemRemoved(index);
        }
    }

    public interface FileItemListener {
        void onFileItemClicked(File file);

        void onDeleteFileItemClicked(File file);

        void onCopyFileItemClicked(File file);

        void onMoveFileItemClicked(File file);
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFileIcon, ivFileMore;
        private TextView tvFileName;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFileIcon = itemView.findViewById(R.id.iv_file_icon);
            ivFileMore = itemView.findViewById(R.id.iv_file_more);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
        }

        public void bindFile(File file) {
            if (file.isDirectory()) {
                ivFileIcon.setImageResource(R.drawable.ic_folder_black_32dp);
            } else {
                ivFileIcon.setImageResource(R.drawable.ic_file_black_32dp);
            }
            tvFileName.setText(file.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileItemListener.onFileItemClicked(file);
                }
            });

            ivFileMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_file_item, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menuItem_copy:
                                    fileItemListener.onCopyFileItemClicked(file);
                                    break;
                                case R.id.menuItem_delete:
                                    fileItemListener.onDeleteFileItemClicked(file);
                                    break;
                                case R.id.menuItem_move:
                                    fileItemListener.onMoveFileItemClicked(file);
                                    break;
                            }
                            return false;
                        }
                    });
                }
            });
        }


    }


}
