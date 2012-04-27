package com.Tomas.tapit;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


public class GameActivity extends Activity{
	


	GameBrain brain;
	TextView score;
	TextView command;
	TextView difficultyText;
	TextView time;
	Boolean isPlaying;
	int commandInput;
	int timeLeft;
	Boolean isPaused;
	Vibrator vibe;
	SoundPool soundPool;
	int soundTapIt;
	int soundGreen;
	int soundPurple;
	int soundPink;
	int soundBlue;
	int soundDoubleGreen;
	int soundDoublePurple;
	int soundDoublePink;
	int soundDoubleBlue;
	ProgressBar progressBar;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub				
		super.onCreate(savedInstanceState);
		
		Log.d("GAMEACTIVITY", "OnCreate: ");
		setContentView(R.layout.game);
		Drawable d = findViewById(R.id.pink).getBackground();  
        PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.rgb(254, 0, 152), PorterDuff.Mode.SRC_ATOP);  
        d.setColorFilter(filter); 
		
		d = findViewById(R.id.purple).getBackground();  
        filter = new PorterDuffColorFilter(Color.rgb(102, 0, 102), PorterDuff.Mode.SRC_ATOP);  
        d.setColorFilter(filter);  
        
        d = findViewById(R.id.green).getBackground();  
        filter = new PorterDuffColorFilter(Color.rgb(1, 204, 52), PorterDuff.Mode.SRC_ATOP);  
        d.setColorFilter(filter);
        
        d = findViewById(R.id.blue).getBackground();  
        filter = new PorterDuffColorFilter(Color.rgb(127, 181, 255), PorterDuff.Mode.SRC_ATOP);  
        d.setColorFilter(filter);     
        
        getDifficultyText().setText(getBrain().getDifficultyText());
		prep();
		
		isPlaying = true;
	
		Log.d("GAMEOVERACTIVITY", "onCreate: " + getBrain().difficulty);
		vibe = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC, 0);
		
		soundGreen = soundPool.load(getApplicationContext(), R.raw.tapitgreen, 1);
		soundBlue = soundPool.load(getApplicationContext(), R.raw.tapitblue, 1);
		soundPink = soundPool.load(getApplicationContext(), R.raw.tapitpink, 1);
		soundPurple = soundPool.load(getApplicationContext(), R.raw.tapitpurple, 1);
		soundDoubleGreen = soundPool.load(getApplicationContext(), R.raw.doublegreen, 1);
		soundDoubleBlue = soundPool.load(getApplicationContext(), R.raw.doubleblue, 1);
		soundDoublePink = soundPool.load(getApplicationContext(), R.raw.doublepink, 1);
		soundDoublePurple = soundPool.load(getApplicationContext(), R.raw.doublepurple, 1);
		new Timer().execute();
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setMax(getBrain().getDifficulty());
		
		
		

	}
	
	public class Timer extends AsyncTask<String, Integer, String>{
		
		
		
		@Override
		protected String doInBackground(String... params) {
			hearCommand(getBrain().getCommand());
			while(isPlaying && timeLeft>0){				
				
				try {
					Thread.sleep(1);
					timeLeft -= 1;
					Log.d("GAMEACTIVITY", "TimeLeft: " + timeLeft);
					publishProgress();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();					
				}                
				if(isPaused){
					break;
				}
			}
			if (isPlaying && !isPaused)
				gameOver("TOO SLOW!");
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			getTime().setText("Time left: "+ timeLeft);
			progressBar.setProgress(timeLeft);
			super.onProgressUpdate(values);
		}	
	}
	
	
	
	
	@Override
	protected void onPause() {
		isPaused = true;
		super.onPause();
	}

	@Override
	protected void onResume() {
		isPaused = false;
		timeLeft = getBrain().getDifficulty();
		new Timer().execute();
		super.onResume();
	}

	TextView getScore(){
		if (this.score == null)
			this.score = (TextView) findViewById(R.id.score);
		return this.score;
	}
	
	TextView getCommand(){
		if (this.command == null)
			this.command = (TextView) findViewById(R.id.command);
		return this.command;
	}
	
	TextView getTime(){
		if (this.time == null)
			this.time = (TextView) findViewById(R.id.time);
		return this.time;
	}
	
	TextView getDifficultyText(){
		if (this.difficultyText == null)
			this.difficultyText = (TextView) findViewById(R.id.difficulty);
		return this.difficultyText;
	}
	
	
	
	
	
	GameBrain getBrain(){
		if (this.brain == null){
			this.brain = new GameBrain(getIntent().getExtras().getInt("difficulty"));
		}
		return this.brain;
	}
	
	public void prep(){
		getScore().setText("YOUR SCORE: " + getBrain().getScore());
		getCommand().setText(getBrain().getStringCommand());
		timeLeft = getBrain().getDifficulty();
		isPaused = false;
		
	}
	
	public void purple(View v){	
		isCorrect(1);		
	}
	
	public void pink(View v){
		isCorrect(2);	
	}
	
	public void green(View v){
		isCorrect(3);			
	}		
	
	public void blue(View v){		
		isCorrect(4);		
	}
	
	public void isCorrect(int i){
		vibe.vibrate(50);
		if (getBrain().isDouble()){
			if(!getBrain().isCorrect(i+4))
				 gameOver("WRONG!");
			getBrain().setSingle();
		}else if (getBrain().isCorrect(i)){
			getBrain().earnPoints();
			getScore().setText("YOUR SCORE: " + getBrain().getScore());
			getCommand().setText(getBrain().getStringCommand());
			timeLeft = getBrain().getDifficulty();
			hearCommand(getBrain().getCommand());
		} else gameOver("WRONG!");	
	}
	
	public void pause(View v){
		Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setClassName(this, PauseActivity.class.getName());
    	this.startActivity(intent);
	}
	
	void hearCommand(int command){
		if (command == 1)
			soundPool.play(soundPurple, 1, 1, 0, 0, 1);
		if (command == 2)
			soundPool.play(soundPink, 1, 1, 0, 0, 1);
		if (command == 3)
			soundPool.play(soundGreen, 1, 1, 0, 0, 1);
		if (command == 4)
			soundPool.play(soundBlue, 1, 1, 0, 0, 1);
		if (command == 5)
			soundPool.play(soundDoublePurple, 1, 1, 0, 0, 1);
		if (command == 6)
			soundPool.play(soundDoublePink, 1, 1, 0, 0, 1);
		if (command == 7)
			soundPool.play(soundDoubleGreen, 1, 1, 0, 0, 1);
		if (command == 8)
			soundPool.play(soundDoubleBlue, 1, 1, 0, 0, 1);
		
		

	}
	
	void gameOver(String reason){
		isPlaying = false;		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (checkIfHighScore() >= 1){
			intent.setClassName(this, ScoreActivity.class.getName());
	    	intent.putExtra("position", checkIfHighScore());
	    	intent.putExtra("score", getBrain().getScore() );
		}else {	
	    	intent.setClassName(this, GameOverActivity.class.getName());
	    	Log.d("GAMEOVER INTENT", ""+getBrain().getDifficulty());
	    	intent.putExtra("difficulty", getBrain().getDifficulty());
	    	intent.putExtra("reason", reason);			
		}
		this.startActivity(intent);
    	finish();
	}
	
	int checkIfHighScore(){
		Log.d("GAMEACTIVITY", "CheckIfHighScore: (score)" + getBrain().getScore());
		int finalScore = (getBrain().getScore());
		SharedPreferences highScore = getSharedPreferences("tap it", 0);
		String[] scores = highScore.getString("highscore", "-|-|-|-|-|0|0|0|0|0").split("\\|");
		Log.d("GAMEACTIVITY", "CheckIfHighScore:(high score string) " + highScore.getString("highscore", "1.|2.|3.|4.|5.|0|0|0|0|0"));

		if (finalScore == 0){
			return 0;
		}
		if (finalScore >= Integer.parseInt(scores[5])){
			Log.d("GAMEACTIVITY", "CheckIfHighScore: 1 true  "+scores[5]);
			return 1;
		}
		if (finalScore >= Integer.parseInt(scores[6])){
			Log.d("GAMEACTIVITY", "CheckIfHighScore: 2 true "+scores[6]);
			return 2;
		}
		if (finalScore >= Integer.parseInt(scores[7])){
			Log.d("GAMEACTIVITY", "CheckIfHighScore: 3 true "+scores[7]);
			return 3;
		}
		if (finalScore >= Integer.parseInt(scores[8])){
			Log.d("GAMEACTIVITY", "CheckIfHighScore: 4 true "+scores[8]);
			return 4;
		}
		if (finalScore >= Integer.parseInt(scores[9])){
			Log.d("GAMEACTIVITY", "CheckIfHighScore: 5 true"+scores[9]);
			return 5;
		}else return 0;
			
	}

	
	
	
}
