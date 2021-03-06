package com.example.workoutlog.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.interfaces.IAddSetClickHandler;
import com.example.workoutlog.interfaces.ISendFromSetAdapterToExercise;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.Set;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private static final String TAG = "ExerciseAdapter";
    private Context context;
    public List<RoutineDetails> exercises;
    private WorkoutViewModel workoutViewModel;
    private IAddSetClickHandler IAddSetClickHandler;
    private double prevMaxWeightForExercise;


    private ISendFromSetAdapterToExercise ISendFromSetAdapterToExercise = new ISendFromSetAdapterToExercise() {
        @Override
        public void onItemClickedAt(RoutineDetails routineDetails) {
            //insert all sets with valid data entered into database
            for (RoutineDetails routine : exercises) {
                IAddSetClickHandler.onSetsClickedAt(routine.getSets());
            }
        }
    };

    public ExerciseAdapter(Context context, IAddSetClickHandler IAddSetClickHandler, List<RoutineDetails> exercises, WorkoutViewModel workoutViewModel) {
        this.context = context;
        this.exercises = exercises;
        this.IAddSetClickHandler = IAddSetClickHandler;
        this.workoutViewModel = workoutViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (exercises != null && exercises.size() > 0) {

            holder.txtSetName.setText(exercises.get(position).getExercise().getName());
            try {
                prevMaxWeightForExercise = workoutViewModel.getMaxWeightFromWorkoutWithExercise(exercises.get(position).getUserRoutineExercise().getWorkoutId(), exercises.get(position).getExercise().getId());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setHasFixedSize(true);
            SetAdapter setAdapter = new SetAdapter(context, exercises.get(position), IAddSetClickHandler, ISendFromSetAdapterToExercise, holder.btnAddSet, prevMaxWeightForExercise);
            holder.recyclerView.setAdapter(setAdapter);
            setAdapter.setSets(exercises.get(position).getSets());

            holder.btnAddSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int setSize = exercises.get(position).getSets().size();

                    if (setSize == 0) {
                        Set blankSet = new Set();
                        //first set template will be added to the selected exercise
                        blankSet.setUserRoutineExerciseRoutineId(exercises.get(position).getUserRoutineExercise().getId());
                        exercises.get(position).getSets().add(blankSet);
                        setExercises(exercises);
                        try {
                            IAddSetClickHandler.onItemClickedAt(blankSet, "insert");
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            holder.imgBtnCurrentExerciseMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.imgBtnCurrentExerciseMenu);
                    popup.inflate(R.menu.exercise_item_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.removeExercise:
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                                    alertBuilder.setTitle("Remove " + exercises.get(position).getExercise().getName() + " from your workout?")
                                            .setMessage("If you remove this exercise, all the sets with it will be deleted.")
                                            .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    workoutViewModel.deleteUserRoutineExercise(exercises.get(position).getUserRoutineExercise());
                                                }
                                            }).setNegativeButton("CANCEL", null);
                                    final AlertDialog builtAlert = alertBuilder.create();
                                    builtAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            builtAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.attr.colorOnBackground);
                                            builtAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.attr.colorOnBackground);
                                        }
                                    });
                                    builtAlert.show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
        } else {
            return;
        }
    }

    public List<RoutineDetails> getCurrentRoutines() {
        return exercises;
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void setExercises(List<RoutineDetails> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout parent;
        private Button btnAddSet;
        private RecyclerView recyclerView;
        private TextView txtSetName, txtPrevMaxSet;
        private ImageView imgBtnCurrentExerciseMenu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSetName = itemView.findViewById(R.id.txtSetName);
            recyclerView = itemView.findViewById(R.id.recViewSet);
            parent = itemView.findViewById(R.id.parent);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
            imgBtnCurrentExerciseMenu = itemView.findViewById(R.id.exerciseItemMenu);

        }
    }
}






