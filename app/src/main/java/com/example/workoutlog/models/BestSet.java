package com.example.workoutlog.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

public class BestSet implements Parcelable {

    @Embedded
    private UserRoutineExercise userRoutineExercise;

    @Relation(parentColumn = "id",entityColumn = "userRoutineExerciseRoutineId", entity= Set.class)
    private Set set;

    @Relation(parentColumn = "exerciseTypeId",entityColumn = "id", entity= Exercise.class)
    private Exercise exercise;


    @Relation(parentColumn = "workoutId",entityColumn = "id", entity= Workout.class)
    private Workout workout;

    private double weight;

    public BestSet() {

    }

    protected BestSet(Parcel in) {
        userRoutineExercise = in.readParcelable(UserRoutineExercise.class.getClassLoader());
        set = in.readParcelable(Set.class.getClassLoader());
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        workout = in.readParcelable(Workout.class.getClassLoader());
        weight = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(userRoutineExercise, flags);
        dest.writeParcelable(set, flags);
        dest.writeParcelable(exercise, flags);
        dest.writeParcelable(workout, flags);
        dest.writeDouble(weight);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BestSet> CREATOR = new Creator<BestSet>() {
        @Override
        public BestSet createFromParcel(Parcel in) {
            return new BestSet(in);
        }

        @Override
        public BestSet[] newArray(int size) {
            return new BestSet[size];
        }
    };

    public UserRoutineExercise getUserRoutineExercise() {
        return userRoutineExercise;
    }

    public void setUserRoutineExercise(UserRoutineExercise userRoutineExercise) {
        this.userRoutineExercise = userRoutineExercise;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Workout getWorkout() {
        return workout;
    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }


    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "BestSet{" +
                "userRoutineExercise=" + userRoutineExercise +
                ", set=" + set +
                ", exercise=" + exercise +
                ", workout=" + workout +
                ", weight=" + weight +
                '}';
    }
}
