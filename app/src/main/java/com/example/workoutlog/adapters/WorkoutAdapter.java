package com.example.workoutlog.adapters;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.helpers.Constants;
import com.example.workoutlog.models.WorkoutDetails;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<WorkoutDetails> listOfFinishedWorkouts = new ArrayList<>();
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d yyyy");
    private Activity activity;

    public WorkoutAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (listOfFinishedWorkouts != null && listOfFinishedWorkouts.size() > 0) {
            WorkoutDetails currentWorkoutDetails = listOfFinishedWorkouts.get(position);
            holder.txtWorkoutName.setText(listOfFinishedWorkouts.get(position).getWorkout().getName());
            String date = DATE_FORMAT.format(listOfFinishedWorkouts.get(position).getWorkout().getStartTime());
            holder.txtWorkoutDate.setText(date);

            if (currentWorkoutDetails.getWorkout().getFinishTime() != null) {
                long millis = Math.abs(currentWorkoutDetails.getWorkout().getFinishTime().getTime() - currentWorkoutDetails.getWorkout().getStartTime().getTime());
                long minutes = TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS);
                holder.txtWorkoutDuration.setText(String.valueOf(minutes));
                holder.txtWorkoutDuration.append(" minutes");
            }

            String[] exercisesStringArray = new String[currentWorkoutDetails.getUserRoutineExercises().size()];
            for (int i = 0; i < currentWorkoutDetails.getUserRoutineExercises().size(); i++) {
                if (i == currentWorkoutDetails.getUserRoutineExercises().size() - 1) {
                    exercisesStringArray[i] = currentWorkoutDetails.getUserRoutineExercises().get(i).getExercise().getName();
                } else {
                    exercisesStringArray[i] = currentWorkoutDetails.getUserRoutineExercises().get(i).getExercise().getName() + ",\t";
                }
            }
            SpannableStringBuilder content = new SpannableStringBuilder();
            for (String t1 : exercisesStringArray) {
                if (t1 != null) {
                    int contentStart = content.length();
                    content.append(t1);
                    int contentEnd = content.length();
                    content.setSpan(new LeadingMarginSpan.Standard(0, 66), contentStart, contentEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }

            holder.txtExercises.setText(content);

            //if item is clicked, go to show workout screen, and send the workoutdetails to display
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putParcelable(Constants.ARG_WORKOUT_DETAILS, listOfFinishedWorkouts.get(position));
                    NavController navController = Navigation.findNavController(activity, R.id.fragment);
                    navController.navigate(R.id.action_historyFragment_to_showWorkoutFragment, args);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOfFinishedWorkouts.size();
    }

    public void setWorkouts(List<WorkoutDetails> workouts) {
        this.listOfFinishedWorkouts = workouts;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getItemPositionWithDate(LocalDate date) {

        for (WorkoutDetails workoutDetails : listOfFinishedWorkouts) {
            LocalDate workoutLocalDate = Instant.ofEpochMilli(workoutDetails.getWorkout().getStartTime().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (workoutLocalDate.equals(date)) {
                return listOfFinishedWorkouts.indexOf(workoutDetails);
            }
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtWorkoutName, txtWorkoutDate, txtWorkoutDuration, txtExercises;
        private MaterialCardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            txtWorkoutName = itemView.findViewById(R.id.txtWorkoutName);
            txtWorkoutDate = itemView.findViewById(R.id.txtWorkoutDate);
            txtWorkoutDuration = itemView.findViewById(R.id.txtDuration);
            txtExercises = itemView.findViewById(R.id.txtListOfExercises);
        }
    }
}
