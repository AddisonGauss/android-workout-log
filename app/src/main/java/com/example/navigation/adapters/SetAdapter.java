package com.example.navigation.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigation.R;
import com.example.navigation.helpers.DecimalDigitsInputFilter;
import com.example.navigation.interfaces.IAddSetClickHandler;
import com.example.navigation.interfaces.ISendFromSetAdapterToExercise;
import com.example.navigation.models.RoutineDetails;
import com.example.navigation.models.Set;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {
    private static final String TAG = "SetAdapter";

    private Context mContext;
    private RoutineDetails currentExercise;
    public IAddSetClickHandler sendExternalClick;
    public ISendFromSetAdapterToExercise sendExerciseAdapterTextInfo;
    private Button addSetButton;
    private Set prevMaxSet;
    private Set currentSet;


    public SetAdapter(Context mContext, RoutineDetails currentExercise, IAddSetClickHandler sendExternalClick, ISendFromSetAdapterToExercise sendExerciseAdapterTextInfo, Button addSetButton, Set prevMaxSet) {
        this.mContext = mContext;
        this.currentExercise = currentExercise;
        if (currentExercise.getSets() == null) {
            currentExercise.setSets(new ArrayList<>());
        }
        this.sendExternalClick = sendExternalClick;
        this.sendExerciseAdapterTextInfo = sendExerciseAdapterTextInfo;
        this.addSetButton = addSetButton;
        this.prevMaxSet = prevMaxSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "SET ADAPTER onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_set_item, parent, false);
        return new ViewHolder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "SET ADAPTER onBindViewHolder: ");
        if (currentExercise.getSets() != null && currentExercise.getSets().size() > 0) {

            currentSet = currentExercise.getSets().get(position);
            currentSet.setDisplayNumber(position + 1);

            holder.txtSetNumber.setText(String.valueOf(currentSet.getDisplayNumber()));
            holder.txtPrevMaxSet.setText(prevMaxSet.getWeight() + " x " + prevMaxSet.getReps());

            //display hint data in edittext's hint if currentset's values are 0
            if (currentSet.getWeight() == 0) {
                holder.editTxtWeight.setHint(String.valueOf(currentSet.getHintWeight()));
            } else {
                holder.editTxtWeight.setText(String.valueOf(currentSet.getWeight()));
            }

            if (currentSet.getReps() == 0) {
                holder.editTxtReps.setHint(String.valueOf(currentSet.getHintReps()));
            } else {
                holder.editTxtReps.setText(String.valueOf(currentSet.getReps()));
            }

            if(!String.valueOf(holder.editTxtReps.getText()).equals("") && !String.valueOf(holder.editTxtWeight.getText()).equals(""))
            {
                holder.btnSetComplete.setActivated(true);
            }

            if (currentSet.isComplete()) {

                holder.btnSetComplete.setPressed(true);

                holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));

                holder.editTxtWeight.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
                holder.editTxtReps.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));

                holder.editTxtWeight.setText(String.valueOf(currentSet.getWeight()));
                holder.editTxtReps.setText(String.valueOf(currentSet.getReps()));
            }


            holder.btnRemoveSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendExternalClick.onItemClickedAt(currentExercise.getSets().get(position), "delete");
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });


            //reference to addSetButton is received from exercise adapter
            addSetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                    currentSet = currentExercise.getSets().get(position);
                    Set editedSet = new Set();

                    //takes the last edittext's text data or the edittext's hint data and creates a new set with that hint data to display on next recycler view cycle
                    editedSet.setHintWeight(Double.parseDouble(String.valueOf(String.valueOf(holder.editTxtWeight.getText()).equals("") ? currentSet.getHintWeight() : holder.editTxtWeight.getText())));
                    editedSet.setHintReps(Double.parseDouble(String.valueOf(String.valueOf(holder.editTxtReps.getText()).equals("") ? currentSet.getHintReps() : holder.editTxtReps.getText())));

                    editedSet.setUserRoutineExerciseRoutineId(currentExercise.getUserRoutineExercise().getId());

                    currentExercise.getSets().add(editedSet);

                    //send exercise adapter reference to current exercise that add set button was clicked on
                    if (sendExerciseAdapterTextInfo != null) {
                        sendExerciseAdapterTextInfo.onItemClickedAt(currentExercise);
                    }
                }
            });


            //sets values after editText is changed to prevent losing entered data whenever livedata is changed
            holder.editTxtWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged: WEIGHT");
                    if (!String.valueOf(s).equals("")){
                        currentExercise.getSets().get(position).setWeight(Double.parseDouble(String.valueOf(s)));
                        if (!String.valueOf(holder.editTxtReps.getText()).equals("")){
                            holder.btnSetComplete.setActivated(true);
                        }
                    }  else {
                    holder.btnSetComplete.setActivated(false);
                    currentExercise.getSets().get(position).setWeight(0);
                }

                }
            });
            holder.editTxtReps.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG, "onTextChanged: ");
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged: REPS");
                    if (!String.valueOf(s).equals("")){
                        currentExercise.getSets().get(position).setReps(Double.parseDouble(String.valueOf(s)));
                        if (!String.valueOf(holder.editTxtWeight.getText()).equals("")){
                            holder.btnSetComplete.setActivated(true);
                        }
                    } else {
                        holder.btnSetComplete.setActivated(false);
                        currentExercise.getSets().get(position).setReps(0);
                    }


                }
            });

            holder.btnSetComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set currentSet = currentExercise.getSets().get(position);
                    currentSet.setComplete(!currentSet.isComplete());

                    if (currentSet.isComplete()) {
                        //if marked complete and no text has been entered, add hint data to set and display complete design
                        if (String.valueOf(holder.editTxtReps.getText()).equals((""))) {
                            currentSet.setReps(Double.parseDouble(String.valueOf(holder.editTxtReps.getHint())));
                        }
                        if (String.valueOf(holder.editTxtWeight.getText()).equals((""))) {
                            currentSet.setWeight(Double.parseDouble(String.valueOf(holder.editTxtWeight.getHint())));
                        }
                    }
                    //send the current exercise's sets to insert into database to prevent view from not updating this and any other set's text boxes that were edited after adding.
                    sendExternalClick.onSetsClickedAt(currentExercise.getSets());
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return currentExercise.getSets().size();
    }


    public void addToSets(Set set) {
        set.setUserRoutineExerciseRoutineId(currentExercise.getUserRoutineExercise().getId());
        currentExercise.getSets().add(set);
        notifyDataSetChanged();

    }


    public void setSets(List<Set> sets) {
        currentExercise.setSets(sets);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout parent;
        private EditText editTxtWeight, editTxtReps;
        private TextView txtSetNumber, txtPrevMaxSet;
        private Button btnRemoveSet;//btnSetComplete,
        private ImageButton btnSetComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "SET ADAPTER ViewHolder: ");
            parent = itemView.findViewById(R.id.parent);
            txtPrevMaxSet = itemView.findViewById(R.id.txtPrevMax);
            editTxtReps = itemView.findViewById(R.id.editTextReps);
            editTxtWeight = itemView.findViewById(R.id.editTxtPounds);
            txtSetNumber = itemView.findViewById(R.id.txtSetNumber);
            btnSetComplete = itemView.findViewById(R.id.btnComplete);
            btnRemoveSet = itemView.findViewById(R.id.btnRemoveSet);
            editTxtWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
            editTxtReps.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            Log.d(TAG, "finalize: ");
        }
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d(TAG, "onAttachedToRecyclerView: ");
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d(TAG, "onDetachedFromRecyclerView: ");
        super.onDetachedFromRecyclerView(recyclerView);
    }
}

