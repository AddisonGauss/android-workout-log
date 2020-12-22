package com.example.navigation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


@androidx.room.Database(entities = {Workout.class,Exercise.class,UserRoutineExercise.class,Set.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {
    private static final String TAG = "Database";
    private static Database instance;

    public abstract IWorkoutDao workoutDao();

    public abstract IExerciseDao exerciseDao();

    public abstract IUserRoutineExerciseDao userRoutineExerciseDao();

    public abstract IRoutineDetails routineDetailsDao();

    public abstract  IWorkoutDetailsDao workoutDetailsDao();

    public abstract ISetDao setDao();

    public static synchronized Database getInstance(Context context){
        Log.d(TAG, "getInstance: ");
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "database").fallbackToDestructiveMigration().addCallback(roomCallback).build();

        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        private static final String TAG = "Database";
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            Log.d(TAG, "onCreate: ");
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };



    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private static final String TAG = "PopulateDbAsyncTask";
        private IWorkoutDao workoutDao;
        private IExerciseDao exerciseDao;
        private IUserRoutineExerciseDao userRoutineExerciseDao;
        private ISetDao setDao;
        private IWorkoutDetailsDao workoutDetailsDao;

        private PopulateDbAsyncTask(Database db){
            workoutDao = db.workoutDao();
            exerciseDao = db.exerciseDao();
            userRoutineExerciseDao = db.userRoutineExerciseDao();
            setDao = db.setDao();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");

            //Starting exercises to choose from
            exerciseDao.insert(new Exercise( "Bench Press", "Chest"));
            exerciseDao.insert(new Exercise( "Squat", "Legs"));
            exerciseDao.insert(new Exercise( "Deadlift", "Back"));
            exerciseDao.insert(new Exercise( "Overhead Press", "Shoulder"));
            exerciseDao.insert(new Exercise( "Dumbell Curl", "Bicep"));
            exerciseDao.insert(new Exercise( "Shrug", "Traps"));
            exerciseDao.insert(new Exercise( "EZ bar curl", "Bicep"));
            exerciseDao.insert(new Exercise( "Skullcrusher", "Tricep"));
            exerciseDao.insert(new Exercise( "Hammer Curl", "Bicep"));
            exerciseDao.insert(new Exercise( "Neck Curl", "Neck"));
            exerciseDao.insert(new Exercise( "Stiff Leg Deadlift", "Hamstring"));
            exerciseDao.insert(new Exercise( "Quad Extension", "Quadricep"));
            return null;
        }
    }
}

