package com.example.workoutlog.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "set_table")
public class Set implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private double weight;

    private double reps;

    private boolean complete;

    private int displayNumber;

    //used for editText's hint value
    private double hintWeight;

    //used for editText's hint value
    private double hintReps;

    //each set will belong to an exercise, a workout, and a user if i get that far
    @ForeignKey(entity = UserRoutineExercise.class, parentColumns = "id", childColumns = "userRoutineExerciseId", onDelete = CASCADE)
    private Long userRoutineExerciseRoutineId;

    public Set(double weight, double reps, boolean complete) {
        this.weight = weight;
        this.reps = reps;
        this.complete = complete;
    }

    public Set(double weight, double reps) {
        this.weight = 0;
        this.reps = 0;
        this.complete = false;
    }

    public Set() {

    }


    protected Set(Parcel in) {
        id = in.readLong();
        weight = in.readDouble();
        reps = in.readDouble();
        complete = in.readByte() != 0;
        displayNumber = in.readInt();
        hintWeight = in.readDouble();
        hintReps = in.readDouble();
        if (in.readByte() == 0) {
            userRoutineExerciseRoutineId = null;
        } else {
            userRoutineExerciseRoutineId = in.readLong();
        }
    }

    public static final Creator<Set> CREATOR = new Creator<Set>() {
        @Override
        public Set createFromParcel(Parcel in) {
            return new Set(in);
        }

        @Override
        public Set[] newArray(int size) {
            return new Set[size];
        }
    };

    public int getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(int displayNumber) {
        this.displayNumber = displayNumber;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public double getReps() {
        return reps;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setReps(double reps) {
        this.reps = reps;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Long getUserRoutineExerciseRoutineId() {
        return userRoutineExerciseRoutineId;
    }

    public void setUserRoutineExerciseRoutineId(Long userRoutineExerciseRoutineId) {
        this.userRoutineExerciseRoutineId = userRoutineExerciseRoutineId;
    }

    public double getHintWeight() {
        return hintWeight;
    }

    public void setHintWeight(double hintWeight) {
        this.hintWeight = hintWeight;
    }

    public double getHintReps() {
        return hintReps;
    }

    public void setHintReps(double hintReps) {
        this.hintReps = hintReps;
    }

    @Override
    public String toString() {
        return "Set{" +
                "id=" + id +
                ", weight=" + weight +
                ", reps=" + reps +
                ", complete=" + complete +
                ", displayNumber=" + displayNumber +
                ", hintWeight=" + hintWeight +
                ", hintReps=" + hintReps +
                ", userRoutineExerciseRoutineId=" + userRoutineExerciseRoutineId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(weight);
        dest.writeDouble(reps);
        dest.writeByte((byte) (complete ? 1 : 0));
        dest.writeInt(displayNumber);
        dest.writeDouble(hintWeight);
        dest.writeDouble(hintReps);
        if (userRoutineExerciseRoutineId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userRoutineExerciseRoutineId);
        }
    }
}
