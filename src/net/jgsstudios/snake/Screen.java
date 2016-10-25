package net.jgsstudios.snake;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Screen extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 1L;

	private Thread thread = new Thread(this);
	
	private KeyHandler keyHandler;
	
	private Food[] food;
	
	private Frame frame;
	private Snake snake;
	
	private Random random;
	
	private Image menuImage = null;
	private boolean isInMenu = true;
	

	public Screen(Frame frame){
		this.random = new Random();
		this.food = new Food[10];
		
		this.frame = frame;
		this.snake = new Snake(this.food);
		this.keyHandler = new KeyHandler();
		this.frame.addKeyListener(this.keyHandler);
		
		
		this.thread.start();
	}
	
	public void paintComponent(Graphics g){
		g.clearRect(0, 0, Frame.WIDTH, Frame.HEIGHT);
		
		if(isInMenu){
			//draw menu
			drawMenu(g);
		}else{
			//draw everything else
			for(int i = 0; i < snake.tail.length; i++){
				if(snake.tail[i] != null){
					g.setColor(Color.BLACK);
					g.drawRect(snake.tail[i].x*snake.tailSize, snake.tail[i].y*snake.tailSize, snake.tailSize, snake.tailSize);
					
					g.setColor(new Color(0, 136+((i*5)<(255-136)?(i*5):(255-136)), 25));
					g.fillRect(snake.tail[i].x*snake.tailSize+1, snake.tail[i].y*snake.tailSize+1, snake.tailSize-1, snake.tailSize-1);
				}
			}
			
			for(int i = 0; i < food.length; i++){
				if(food[i] != null){
					g.setColor(Color.BLACK);
					g.drawRect(food[i].x*snake.tailSize+(snake.tailSize-food[i].foodSize)/2, food[i].y*snake.tailSize+(snake.tailSize-food[i].foodSize)/2, food[i].foodSize, food[i].foodSize);
					
					g.setColor(new Color(255, 0, 0));
					g.fillRect(food[i].x*snake.tailSize+(snake.tailSize-food[i].foodSize)/2+1, food[i].y*snake.tailSize+(snake.tailSize-food[i].foodSize)/2+1, food[i].foodSize-1, food[i].foodSize-1);
				}
				
			}
			
			g.setColor(Color.BLACK);
			g.drawString("Press R to Restart your Best Length", 1150, 25);
			g.drawString("Press Esc to Pause", 1150, 40);
			g.drawString("FPS: " + fps, 10, 25);
			g.drawString("Length: " + snake.length, 10 , 40);
			g.drawString("Created by JGS Studios", 10, 70);
			
			if(Integer.parseInt(cheats) > 0){
				g.setColor(new Color(0, 136, 25));
				g.drawString("Jacob's Cheat Mode Activated", 1150, 55);
				g.drawString("Press P to Disable", 1150, 70);
			}
			
			if(this.snake.dead){
				g.setColor(Color.RED);
				g.drawString("Game Over", 650, 375);
				g.drawString("You Died", 655, 400);
			}
			if(this.snake.length > 98){
				g.setColor(Color.RED);
				g.drawString("Game Over", 650, 375);
			}
			if(highscore.equals("")){
				highscore = this.getHighscore();
			}
			g.setColor(Color.BLACK);
			g.drawString("Best: " + highscore, 10, 55);
		}
		
	}
	
	private int maxFoodSpawn = 200;
	private int foodSpawn;
	private String highscore = "";
	private String cheats = "0";
	
	public void drawMenu(Graphics g){
		if(this.menuImage == null){
		try{
			URL imagePath = Screen.class.getResource("snakeMenu.png");
			menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
		g.drawImage(menuImage, 0, 0, 1366, 728, this);
}	
	
	public void checkScore(){
		//format	Jacob/:/50
		if(snake.length > Integer.parseInt((highscore.split(":")[1]))){
			//user has set a new record
			String name = JOptionPane.showInputDialog("You set a new Best Length! What is your name?");
			highscore = name + ":" + snake.length;
			
			File scoreFile = new File("highscore.dat");
			if(!scoreFile.exists()){
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try{
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highscore);
			}
			catch (Exception e){
				//errors
			}
			finally{
				try{
			if (writer != null)
				writer.close();
				}
				catch (Exception e){}
			}
		}
	}
	
	public void maxScore(){
		if(snake.length > Integer.parseInt((highscore.split(":")[1]))){
			if(snake.length > 98){
		String name = JOptionPane.showInputDialog("You set the MAX Lenght! What is your name?");
		highscore = name + ":" + snake.length + " MAX Length!";
		
		File scoreFile = new File("highscore.dat");
		if(!scoreFile.exists()){
			try {
				scoreFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter writeFile = null;
		BufferedWriter writer = null;
		try{
			writeFile = new FileWriter(scoreFile);
			writer = new BufferedWriter(writeFile);
			writer.write(this.highscore);
		}
		catch (Exception e){
			//errors
		}
		finally{
			try{
		if (writer != null)
			writer.close();
			}
			catch (Exception e){}
			}
		}else{
		checkScore();
	}	
}	
}		
		
	public String getHighscore(){
		//format	Jacob:50
		FileReader readFile = null;
		BufferedReader reader = null;
		try{
			readFile = new FileReader("highscore.dat");
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		catch (Exception e){
			return "Nobody:0";
		}
		finally{
			try {
				if(reader != null)
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void update(){
		if(!isInMenu){
		if (this.snake.length > 98)maxScore();
		if(!this.snake.dead)this.snake.update(); else{checkScore();}
		
		if(foodSpawn >= maxFoodSpawn){
			for(int i = 0; i < food.length; i++){
				if(food[i] == null){
					boolean spawned = false;
					
					int x = 0;
					int y = 0;
					
					while(!spawned){
					x = random.nextInt(Frame.WIDTH/snake.tailSize-1);
					y = random.nextInt(Frame.HEIGHT/snake.tailSize-1);
					
					spawned=true;
					
					for(int j = 0; j < food.length; j++){
						if(food[j] != null){
							if(food[j].x == x && food[j].y == y){
								spawned=false;
							}
						}
					}
					}
					for(int j = 0; j < snake.tail.length; j++){
						if(snake.tail[j] != null){
							if(snake.tail[j].x == x && snake.tail[j].y == y){
								spawned=false;
							}
						}
					}
					food[i] = new Food (x, y);
					
					break;
				}
			}
			foodSpawn=0;
		}else{
			foodSpawn++;
		}
	}
	}	
	private long maxFrameRate = 60;
	private int fps = 0;
	
	public void run() {
		int frames = 0;
		long secondStart = System.currentTimeMillis();
		
		while(true){
			update();
			
			try {
				Thread.sleep(1000/this.maxFrameRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			repaint();

			if(secondStart + 1000 <= System.currentTimeMillis()){
				secondStart = System.currentTimeMillis();
				fps=frames;
				frames=0;
			}
			
			frames++;
		}
	}
	private class KeyHandler implements KeyListener{
		public void keyPressed(KeyEvent e) {
			//Start Game
			if(e.getKeyCode() == 10){
				isInMenu = false;
			}
			//Pause Game
			if(e.getKeyCode() == 27){
				isInMenu = true;
			}
			//Add Length
			if(e.getKeyCode() == 61){
				if (Integer.parseInt(cheats) == 1){
			String add = JOptionPane.showInputDialog("How much length would you like to add?");
			snake.length += Integer.parseInt(add);
			}
			}
			//Subtract Length
			if(e.getKeyCode() == 45){
				if (Integer.parseInt(cheats) > 0){
				String minus = JOptionPane.showInputDialog("How much length would you like to subtract");
				snake.length -= Integer.parseInt(minus);
			}
			}
			//Set Speed
			if(e.getKeyCode() == 73){
				if (Integer.parseInt(cheats) > 0){
				String speed = JOptionPane.showInputDialog("Set your new speed. 0 is the slowest and 100 is the fastest");
				snake.maxMoveSpeed = Integer.parseInt(speed);
			}
			}
			//Cheats Mode Enabled 
			if(e.getKeyCode() == 79){
					cheats="1";
			}
			//Cheats Mode Disabled
			if(e.getKeyCode() == 80){
				cheats="0";
			}
			//Restart Best Length
			if(e.getKeyCode() == 82){
				highscore="Nobody:0";
			}
			//Up
			if(e.getKeyCode() == 38||e.getKeyCode() == 87){
				snake.moveDirection=3;
			}

		    //Right
			if(e.getKeyCode() == 39||e.getKeyCode() == 68){
				snake.moveDirection=0;
			}
			
			//Down
			if(e.getKeyCode() == 40||e.getKeyCode() == 83){
				snake.moveDirection=1;
			}
			
			//Left
			if(e.getKeyCode() == 37||e.getKeyCode() == 65){
				snake.moveDirection=2;
			}
			
		}

		public void keyReleased(KeyEvent e) {
			
		}

		public void keyTyped(KeyEvent e) {
			
		}
		
	}

}
