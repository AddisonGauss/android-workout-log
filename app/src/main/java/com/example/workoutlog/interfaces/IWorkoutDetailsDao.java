package com.example.workoutlog.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.workoutlog.models.WorkoutDetails;

import java.util.List;

@Dao
public interface IWorkoutDetailsDao {

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    LiveData<List<WorkoutDetails>> getAllWorkoutsWithWorkoutId(long workoutId);


    @Transaction
    @Query("SELECT * FROM workout_table")
    LiveData<List<WorkoutDetails>> getAllWorkoutsAll();


    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    WorkoutDetails getWorkoutDetailsFromWorkoutId(long workoutId);


    @Transaction
    @Query("SELECT * FROM workout_table")
    List<WorkoutDetails> getAllWorkoutDetails();


    @Transaction
    @Query("SELECT * FROM  user_routine_exercise_table, workout_table WHERE user_routine_exercise_table.workoutId = workout_table.id AND user_routine_exercise_table.exerciseTypeId= :exerciseId ")
    List<WorkoutDetails> getAllWorkoutDetailsWithExercise(long exerciseId);

}
