package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityStateListBinding;
import com.pepdeal.in.databinding.ActivityStateSearchListBinding;
import com.pepdeal.in.databinding.ItemStateListLayoutBinding;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.StateModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class StateSearchListActivity extends AppCompatActivity {

    ActivityStateSearchListBinding binding;
    ProgressDialog dialog;
    List<StateModel> stateModelList = new ArrayList<>();
    StateAdapter adapter;
    Filter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_state_search_list);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(StateSearchListActivity.this)) {
            getStateList();
        } else {
            Utils.InternetAlertDialog(StateSearchListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null) {
                    filter.filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void getStateList() {
        dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.stateList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    stateModelList = new ArrayList<>();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<StateModel>>() {
                    }.getType();
                    stateModelList = new ArrayList<>();
                    stateModelList.addAll(gson.fromJson(jsonObject.getString("data"), listType));

                    binding.recList.setLayoutManager(new LinearLayoutManager(StateSearchListActivity.this));
                    adapter = new StateAdapter();
                    filter = adapter.getFilter();
                    binding.recList.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                dismissDialog();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(StateSearchListActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(StateSearchListActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(StateSearchListActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(StateSearchListActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(StateSearchListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StateSearchListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> implements Filterable {
        List<StateModel> cityModelListSearch;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemStateListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(StateSearchListActivity.this),
                    R.layout.item_state_list_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StateModel model = stateModelList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return stateModelList.size();
        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    // mDisplayedValues = (ArrayList<Product>) results.values; // has the filtered values
                    stateModelList = (ArrayList<StateModel>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation com values
                    ArrayList<StateModel> FilteredArrList = new ArrayList<StateModel>();

                    if (cityModelListSearch == null) {
                        cityModelListSearch = new ArrayList<StateModel>(stateModelList); // saves the original data com mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = cityModelListSearch.size();
                        results.values = cityModelListSearch;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < cityModelListSearch.size(); i++) {
                            String data = cityModelListSearch.get(i).getName();
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(new StateModel(cityModelListSearch.get(i).getName(), cityModelListSearch.get(i).getId()));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemStateListLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemStateListLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(StateModel model, int position) {
                layoutBinding.txtState.setText(model.getName());
                layoutBinding.lnrState.setOnClickListener(view -> {
                    Intent intent = new Intent(StateSearchListActivity.this,CityListSearchActivity.class);
                    intent.putExtra("state_id", model.getId());
                    intent.putExtra("state_name", model.getName());
                    startActivity(intent);
                });
            }
        }
    }
}