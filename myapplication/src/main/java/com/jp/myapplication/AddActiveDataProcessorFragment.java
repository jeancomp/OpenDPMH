package com.jp.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessorManager;

public class AddActiveDataProcessorFragment extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(getBaseContext());
        // you can use LayoutInflater.from(getContext()).inflate(...) if you have xml layout
        View view;
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_add_activedataprocessor, null);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    List<ListDataProcessor> listDataProcessors = new ArrayList();
                    ListDataProcessorManager ldpm = ListDataProcessorManager.getInstance();
                    listDataProcessors = ldpm.select();

                    ListDataProcessorAdapter adapter = new ListDataProcessorAdapter(getBaseContext(), listDataProcessors, new List<String>() {
                        @Override
                        public int size() {
                            return 0;
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }

                        @Override
                        public boolean contains(@Nullable Object o) {
                            return false;
                        }

                        @NonNull
                        @Override
                        public Iterator<String> iterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public Object[] toArray() {
                            return new Object[0];
                        }

                        @NonNull
                        @Override
                        public <T> T[] toArray(@NonNull T[] a) {
                            return null;
                        }

                        @Override
                        public boolean add(String s) {
                            return false;
                        }

                        @Override
                        public boolean remove(@Nullable Object o) {
                            return false;
                        }

                        @Override
                        public boolean containsAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(@NonNull Collection<? extends String> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(int index, @NonNull Collection<? extends String> c) {
                            return false;
                        }

                        @Override
                        public boolean removeAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean retainAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public void clear() {

                        }

                        @Override
                        public String get(int index) {
                            return null;
                        }

                        @Override
                        public String set(int index, String element) {
                            return null;
                        }

                        @Override
                        public void add(int index, String element) {

                        }

                        @Override
                        public String remove(int index) {
                            return null;
                        }

                        @Override
                        public int indexOf(@Nullable Object o) {
                            return 0;
                        }

                        @Override
                        public int lastIndexOf(@Nullable Object o) {
                            return 0;
                        }

                        @NonNull
                        @Override
                        public ListIterator<String> listIterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public ListIterator<String> listIterator(int index) {
                            return null;
                        }

                        @NonNull
                        @Override
                        public List<String> subList(int fromIndex, int toIndex) {
                            return null;
                        }
                    });

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    mRecyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
