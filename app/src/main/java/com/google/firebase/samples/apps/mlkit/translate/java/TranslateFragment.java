/*
 * Copyright 2019 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.firebase.samples.apps.mlkit.translate.java;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.samples.apps.mlkit.translate.R;
import com.google.firebase.samples.apps.mlkit.translate.databinding.TranslateFragmentBinding;
import com.google.firebase.samples.apps.mlkit.translate.java.Database.LocalDatabase;
import com.google.firebase.samples.apps.mlkit.translate.java.TranslateViewModel.Language;
import com.google.firebase.samples.apps.mlkit.translate.java.TranslateViewModel.ResultOrError;
import com.google.firebase.samples.apps.mlkit.translate.java.model.History;
import com.google.firebase.samples.apps.mlkit.translate.java.recyclerview.RecyclerViewAdopter;

import java.util.ArrayList;
import java.util.List;

public class TranslateFragment extends BaseFragment {


    private RecyclerViewAdopter mAdopter;
    private LocalDatabase mDatabase;
    private TranslateFragmentBinding mBinding;

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mDatabase = LocalDatabase.getLocalDatabase(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.translate_fragment, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DataBindingUtil.bind(view);
        setUpRecyclerView(mBinding.historyRecyclerView);

        final TranslateViewModel viewModel =
                ViewModelProviders.of(this).get(TranslateViewModel.class);

        // Get available language list and set up source and target language spinners
        // with default selections.
        final ArrayAdapter<Language> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, viewModel.getAvailableLanguages());
        mBinding.sourceLangSelector.setAdapter(adapter);
        mBinding.targetLangSelector.setAdapter(adapter);
        mBinding.sourceLangSelector.setSelection(adapter.getPosition(new Language("en")));
        mBinding.targetLangSelector.setSelection(adapter.getPosition(new Language("es")));
        mBinding.sourceLangSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setProgressText(mBinding.targetText);
                viewModel.sourceLang.setValue(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBinding.targetText.setText("");
            }
        });
        mBinding.targetLangSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setProgressText(mBinding.targetText);
                viewModel.targetLang.setValue(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBinding.targetText.setText("");
            }
        });

        mBinding.buttonSwitchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressText(mBinding.targetText);
                int sourceLangPosition = mBinding.sourceLangSelector.getSelectedItemPosition();
                mBinding.sourceLangSelector.setSelection(mBinding.targetLangSelector.getSelectedItemPosition());
                mBinding.targetLangSelector.setSelection(sourceLangPosition);
            }
        });

        // Set up toggle buttons to delete or download remote models locally.
        mBinding.buttonSyncSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Language language = adapter.getItem(mBinding.sourceLangSelector.getSelectedItemPosition());
                if (isChecked) {
                    viewModel.downloadLanguage(language);
                } else {
// alert dialogue to conform
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Are you Sure to Delete Model")
                            .setCancelable(false)
                            .setTitle("Are you Sure")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    viewModel.deleteLanguage(language);
                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_green_light));
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_red_dark));
                        }
                    });
                    alert.show();
                }
            }
        });
        mBinding.buttonAddToHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDatabase.userDao().addNewHistory(new History(mBinding.sourceText.getText().toString(),mBinding.targetText.getText().toString()));
            }
        });
        mBinding.buttonSyncTarget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Language language = adapter.getItem(mBinding.targetLangSelector.getSelectedItemPosition());
                if (isChecked) {
                    viewModel.downloadLanguage(language);
                } else {
                    // alert dialogue to conform
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Are you Sure to Delete Model")
                            .setCancelable(false)
                            .setTitle("Are you Sure")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    viewModel.deleteLanguage(language);
                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_green_light));
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_red_dark));
                        }
                    });
                    alert.show();

                }
            }
        });

        // Translate input text as it is typed
        mBinding.sourceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setProgressText(mBinding.targetText);
                viewModel.sourceText.postValue(s.toString());
            }
        });
        viewModel.translatedText.observe(this, new Observer<ResultOrError>() {
            @Override
            public void onChanged(TranslateViewModel.ResultOrError resultOrError) {
                if (resultOrError.error != null) {
                    mBinding.sourceText.setError(resultOrError.error.getLocalizedMessage());
                } else {
                    mBinding.targetText.setText(resultOrError.result);
                }
            }
        });

        // Update sync toggle button states based on downloaded models list.
        viewModel.availableModels.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> firebaseTranslateRemoteModels) {
                String output = getContext().getString(R.string.downloaded_models_label,
                        firebaseTranslateRemoteModels);
                mBinding.downloadedModels.setText(output);
                mBinding.buttonSyncSource.setChecked(firebaseTranslateRemoteModels.contains(
                        adapter.getItem(mBinding.sourceLangSelector.getSelectedItemPosition()).getCode()));
                mBinding.buttonSyncTarget.setChecked(firebaseTranslateRemoteModels.contains(
                        adapter.getItem(mBinding.targetLangSelector.getSelectedItemPosition()).getCode()));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDatabase.userDao().getAll().observe(this, new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> histories) {
                List<Object> list = new ArrayList<>();

                for (History obj:
                     histories) {
                    list.add(obj);
                }
                mAdopter.setListData(list);
            }
        });
    }

    @Override
    protected RecyclerView.Adapter onPrepareAdopter() {
        List<Object> objectList = new ArrayList<>();
        mAdopter = new RecyclerViewAdopter(getContext(),R.layout.item_hisotry,objectList);
        return mAdopter;
    }

    private void setProgressText(TextView tv) {
        tv.setText(getContext().getString(R.string.translate_progress));
    }
}
