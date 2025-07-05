package com.winlator;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winlator.container.Container;
import com.winlator.container.ContainerManager;
import com.winlator.contentdialog.ContentDialog;
import com.winlator.contentdialog.StorageInfoDialog;
import com.winlator.core.PreloaderDialog;
import com.winlator.xenvironment.ImageFs;

import java.util.ArrayList;
import java.util.List;

public class ContainersFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ContainerManager manager;
    private PreloaderDialog preloaderDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        preloaderDialog = new PreloaderDialog(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new ContainerManager(getContext());
        loadContainersList();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.containers);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.containers_fragment, container, false);
        recyclerView = frameLayout.findViewById(R.id.RecyclerView);
        emptyTextView = frameLayout.findViewById(R.id.TVEmptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        return frameLayout;
    }

    private void loadContainersList() {
        ArrayList<Container> containers = manager.getContainers();
        recyclerView.setAdapter(new ContainersAdapter(containers));
        if (containers.isEmpty()) emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.containers_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.containers_menu_add) {
            if (!ImageFs.find(getContext()).isValid()) return false;
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.FLFragmentContainer, new ContainerDetailFragment())
                    .commit();
            return true;
        } else return super.onOptionsItemSelected(menuItem);
    }

    private class ContainersAdapter extends RecyclerView.Adapter<ContainersAdapter.ViewHolder> {
        private final List<Container> data;

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageButton menuButton;
            private final ImageView imageView;
            private final TextView title;

            private ViewHolder(View view) {
                super(view);
                this.imageView = view.findViewById(R.id.ImageView);
                this.title = view.findViewById(R.id.TVTitle);
                this.menuButton = view.findViewById(R.id.BTMenu);
            }
        }

        public ContainersAdapter(List<Container> data) {
            this.data = data;
        }

        @Override
        public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.container_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Container item = data.get(position);
            holder.imageView.setImageResource(R.drawable.icon_container);
            holder.title.setText(item.getName());
            holder.menuButton.setOnClickListener((view) -> showListItemMenu(view, item));
        }

        @Override
        public final int getItemCount() {
            return data.size();
        }

        private void showListItemMenu(View anchorView, Container container) {
            final Context context = getContext();
            PopupMenu listItemMenu = new PopupMenu(context, anchorView);
            listItemMenu.inflate(R.menu.container_popup_menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listItemMenu.setForceShowIcon(true);

            listItemMenu.setOnMenuItemClickListener((menuItem) -> {
                final int itemId = menuItem.getItemId();
                if (itemId == R.id.container_run) {

                    if (!XrActivity.isSupported()) {
                        Intent intent = new Intent(context, XServerDisplayActivity.class);
                        intent.putExtra("container_id", container.id);
                        context.startActivity(intent);
                    } else XrActivity.openIntent(getActivity(), container.id, null);
                } else if (itemId == R.id.container_edit) {

                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.FLFragmentContainer, new ContainerDetailFragment(container.id))
                            .commit();
                } else if (itemId == R.id.container_duplicate) {

                    ContentDialog.confirm(getContext(), R.string.do_you_want_to_duplicate_this_container, () -> {
                        preloaderDialog.show(R.string.duplicating_container);
                        manager.duplicateContainerAsync(container, () -> {
                            preloaderDialog.close();
                            loadContainersList();
                        });
                    });
                } else if (itemId == R.id.container_remove) {

                    ContentDialog.confirm(getContext(), R.string.do_you_want_to_remove_this_container, () -> {
                        preloaderDialog.show(R.string.removing_container);
                        manager.removeContainerAsync(container, () -> {
                            preloaderDialog.close();
                            loadContainersList();
                        });
                    });
                } else if (itemId == R.id.container_info) {

                    (new StorageInfoDialog(getActivity(), container)).show();
                }
                return true;
            });
            listItemMenu.show();
        }
    }
}
