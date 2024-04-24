package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class Main extends JFrame{
	private static GamePanel p1 = new GamePanel();//創建遊戲面板
	private static InformationPanel p2 = new InformationPanel();//創建信息面板

	public Main(){//配置框架的佈局
		setLayout(new BorderLayout());
		add(p1,BorderLayout.CENTER);
		add(p2,BorderLayout.EAST);
	}

	public static void main(String[] args){
		JFrame frame = new Main();//新建框架

		//配置框架
		frame.setTitle("貪吃蛇");
		frame.setSize(1100, 800);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		//新建線程
		Thread thread1 = new Thread(p1);
		thread1.start();
		Thread thread2 = new Thread(p2);
		thread2.start();
	}
}

class GamePanel extends JPanel implements Runnable{
	public static final int PER_UNIT_LENGTH = 20;//單位長度
	public static final int MULTIPLE = 15;//倍數
	public static final int HALF_SIDE = MULTIPLE * PER_UNIT_LENGTH;//遊戲邊框的一半長 = 倍數 * 單位長度

	private boolean isFirstRun = true;//判斷是否需要初始化
	private boolean isStarted = false;//判斷是否開始
	private boolean isPaused = false;//判斷是否暫停

	private static int score = 0;//遊戲分數
	private static int information = 0;//傳遞遊戲信息

	private Snake snake = new Snake();//新建一條蛇
	private Dot dessert = new Dot();//新建一個點心

	protected void paintComponent(Graphics g){
		super.paintComponent(g);

		//獲取中點座標
		int xCentre = getWidth() / 2;
		int yCentre = getHeight() / 2;

		//獲取一個隨機點座標
		int xRandom, yRandom;

		if(isFirstRun){
			isFirstRun = false;

			//初始化遊戲邊框
			g.drawRect(xCentre - HALF_SIDE, yCentre - HALF_SIDE, HALF_SIDE * 2, HALF_SIDE * 2);

			//初始化蛇頭
			snake.getHead().setX(xCentre);
			snake.getHead().setY(yCentre);
			g.setColor(Color.ORANGE);
			g.fillOval(snake.getHead().getX(), snake.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);

			//初始化點心
			do{
				xRandom = xCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
				yRandom = yCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
			}while(xRandom == snake.getHead().getX() && yRandom == snake.getHead().getY());
			dessert.setX(xRandom);
			dessert.setY(yRandom);
			g.setColor(Color.DARK_GRAY);
			g.fillOval(dessert.getX(), dessert.getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
		}
		else{
			//繪畫遊戲邊框
			g.drawRect(xCentre - HALF_SIDE, yCentre - HALF_SIDE, HALF_SIDE * 2, HALF_SIDE * 2);

			//繪畫蛇身
			g.setColor(Color.MAGENTA);
			for(int i = 0;i < snake.getBody().size();i++){
				g.fillOval(snake.getBody().get(i).getX(), snake.getBody().get(i).getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
			}

			//繪畫蛇頭
			g.setColor(Color.ORANGE);
			g.fillOval(snake.getHead().getX(), snake.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);

			//如果蛇吃到點心，則更新點心
			if(isEncountered()){
				do{
					xRandom = xCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
					yRandom = yCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
				}while(xRandom == snake.getHead().getX() && yRandom == snake.getHead().getY());
				dessert.setX(xRandom);
				dessert.setY(yRandom);
			}
			g.setColor(Color.DARK_GRAY);
			g.fillOval(dessert.getX(), dessert.getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);

			//如果遊戲結束，則追加繪畫GAME OVER
			if(isCrushed()){
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();
				int stringWidth = fm.stringWidth("GAME OVER");
				int stringAscent = fm.getAscent();
				int xCoordinate = xCentre - stringWidth / 2;
				int yCoordinate = yCentre - stringAscent / 2;
				g.drawString("GAME OVER", xCoordinate, yCoordinate);
			}
		}
	}

	public GamePanel(){
		//配置面板屬性
		setFocusable(true);
		setFont(new Font("Californian FB", Font.BOLD, 80));

		//註冊鍵盤監聽器
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				int direction = snake.getDirection();
				switch(e.getKeyCode()){
					case KeyEvent.VK_UP:
						if(isStarted && !isPaused && !isCrushed()){
							if(direction != Snake.DIRECTION_UP && direction != Snake.DIRECTION_DOWN){
							snake.setDirection(Snake.DIRECTION_UP);
							changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_DOWN:
						if(isStarted && !isPaused && !isCrushed()){
							if(direction != Snake.DIRECTION_UP && direction != Snake.DIRECTION_DOWN){
							snake.setDirection(Snake.DIRECTION_DOWN);
							changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_LEFT:
						if(isStarted && !isPaused && !isCrushed()){
							if(direction != Snake.DIRECTION_LEFT && direction != Snake.DIRECTION_RIGHT){
							snake.setDirection(Snake.DIRECTION_LEFT);
							changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_RIGHT:
						if(isStarted && !isPaused && !isCrushed()){
							if(direction != Snake.DIRECTION_LEFT && direction != Snake.DIRECTION_RIGHT){
							snake.setDirection(Snake.DIRECTION_RIGHT);
							changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_ENTER:
						if(isCrushed()){//如果遊戲結束，則重置數據重新開始遊戲
							snake.setDirection(Snake.DIRECTION_RIGHT);
							snake.setSpeed(Snake.SPEED_3);
							snake.setBody(new LinkedList<Dot>());

							isFirstRun = true;
							isStarted = false;
							isPaused = false;

							score = 0;
							information = 0;

							repaint();
						}
						else{
							isStarted = true;
						}
						break;
					case KeyEvent.VK_SPACE:
						if(isStarted && !isCrushed())
							isPaused = !isPaused;
						break;
					case KeyEvent.VK_F1:snake.setSpeed(Snake.SPEED_1);break;
					case KeyEvent.VK_F2:snake.setSpeed(Snake.SPEED_2);break;
					case KeyEvent.VK_F3:snake.setSpeed(Snake.SPEED_3);break;
					case KeyEvent.VK_F4:snake.setSpeed(Snake.SPEED_4);break;
					case KeyEvent.VK_F5:snake.setSpeed(Snake.SPEED_5);break;
					default:
				}
			}
		});
	}

	public void run(){//控制蛇自動前進
		while(true){
			if(isStarted && !isPaused && !isCrushed()){
				changeSnakeLocation();
			}
			try{
				Thread.sleep(snake.getSpeed());
			}
			catch(InterruptedException ex){
				ex.printStackTrace();
			}
		}
	}

	public synchronized void changeSnakeLocation(){//改變蛇的位置信息並重畫
		//存儲蛇頭先前的信息，爲蛇身的更新提供依據
		int xPrevious = snake.getHead().getX();
		int yPrevious = snake.getHead().getY();

		//更新蛇頭位置
		switch(snake.getDirection()){
			case Snake.DIRECTION_UP:snake.getHead().setY(yPrevious - PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_DOWN:snake.getHead().setY(yPrevious + PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_LEFT:snake.getHead().setX(xPrevious - PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_RIGHT:snake.getHead().setX(xPrevious + PER_UNIT_LENGTH);break;
			default:
		}

		//根據蛇頭信息和是否吃到點心更新蛇身位置
		if(isEncountered()){
			score++;
			snake.getBody().addFirst(new Dot(xPrevious, yPrevious));
		}
		else{
			snake.getBody().addFirst(new Dot(xPrevious, yPrevious));
			snake.getBody().removeLast();
		}

		//重畫並獲取焦點
		repaint();
		requestFocus();
	}

	public boolean isEncountered(){//判斷是否吃到點心
		if(snake.getHead().getX() == dessert.getX() 
		&& snake.getHead().getY() == dessert.getY()){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean isCrushed(){//判斷遊戲是否結束
		//先判斷是否碰觸邊框
		boolean isCrushedByBorder = snake.getHead().getX() >= getWidth() / 2 + HALF_SIDE  
		|| snake.getHead().getX() < getWidth() / 2 - HALF_SIDE 
		|| snake.getHead().getY() >= getHeight() / 2 + HALF_SIDE 
		|| snake.getHead().getY() < getHeight() / 2 - HALF_SIDE;
		if(isCrushedByBorder){
			information = 1;
			return true;
		}

		//再判斷是否碰觸自身
		boolean isCrushedByItself = false;
		for(int i = 0;i < snake.getBody().size();i++){
			if(snake.getHead().getX() == snake.getBody().get(i).getX() 
			&& snake.getHead().getY() == snake.getBody().get(i).getY() && !isCrushedByItself)
				isCrushedByItself = true;
		}
		if(isCrushedByItself){
			information = 2;
			return true;
		}
		else{
			return false;
		}
	}

	public static int getScore(){
		return score;
	}

	public static int getInformation(){
		return information;
	}
}

class InformationPanel extends JPanel implements Runnable{
	private Box box = Box.createVerticalBox();//創建一個垂直盒子容器
	private JLabel[] help = new JLabel[5];//顯示幫助信息
	private JLabel score = new JLabel("分數：");//顯示分數
	private JLabel show = new JLabel();//顯示信息

	public InformationPanel(){
		//初始化數組
		for(int i = 0;i < help.length;i++)
			help[i] = new JLabel();

		//配置字體
		Font font1 = new Font("DialogInput", Font.BOLD, 20);
		Font font2 = new Font("DialogInput", Font.BOLD + Font.ITALIC, 25);
		for(int i = 0;i < help.length;i++)
			help[i].setFont(font1);
		score.setFont(font2);
		score.setForeground(Color.GREEN);
		show.setFont(font2);
		show.setForeground(Color.RED);

		//配置幫助信息
		help[0].setText("回車鍵開始遊戲");
		help[1].setText("方向鍵移動蛇");
		help[2].setText("空格鍵暫停遊戲");
		help[3].setText("按鍵F1-F5調節蛇速");
		help[4].setText("按回車鍵可以重新開始遊戲");

		//配置信息面板
		add(box);
		box.add(Box.createVerticalStrut(150));
		for(int i = 0;i < help.length;i++){
			box.add(help[i]);
			box.add(Box.createVerticalStrut(10));
		}
		box.add(Box.createVerticalStrut(90));
		box.add(score);
		box.add(Box.createVerticalStrut(150));
		box.add(show);		
	}

	public void run(){//更新遊戲信息
		while(true){
			String string1 = "分數：" + Integer.toString(GamePanel.getScore());
			score.setText(string1);
			String string2 = null;
			switch(GamePanel.getInformation()){
				case 0:break;
				case 1:string2 = "你撞穿牆壁了！";break;
				case 2:string2 = "你吃到自己了！";break;
				default:
			}
			show.setText(string2);
		}
	}
}

class Snake{//蛇類
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	public static final int DIRECTION_LEFT = 3;
	public static final int DIRECTION_RIGHT = 4;
	public static final int SPEED_1 = 300;
	public static final int SPEED_2 = 200;
	public static final int SPEED_3 = 150;
	public static final int SPEED_4 = 100;
	public static final int SPEED_5 = 30;
	private int direction = DIRECTION_RIGHT;
	private int speed = SPEED_3;
	private Dot head = new Dot();
	private LinkedList<Dot> body = new LinkedList<Dot>();

	public Snake(){
	}

	public Dot getHead(){
		return head;
	}

	public LinkedList<Dot> getBody(){
		return body;
	}

	public int getDirection(){
		return direction;
	}

	public int getSpeed(){
		return speed;
	}

	public void setBody(LinkedList<Dot> body){
		this.body = body;
	}

	public void setDirection(int direction){
		this.direction = direction;
	}

	public void setSpeed(int speed){
		this.speed = speed;
	}
}

class Dot{//點類
	private int x = 0;
	private int y = 0;

	public Dot(){
	}

	public Dot(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}
}
