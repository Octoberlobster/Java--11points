package src;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


//Pair
class Tuple<X, Y> {
    public X x;
    public Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

class MyJFrame extends JFrame implements ActionListener,Runnable{ 
    
    private JPanel contentPane;
    public JPanel MainPanel;
    public  boolean draw=false;
    public  boolean raise=false;
    JButton Draw = new JButton("抽牌");
    JButton NoDraw = new JButton("不抽牌");
    JButton Raise = new JButton("加注");
    JButton NoRaise = new JButton("不加注");
    JLabel countdown = new JLabel();
    JLabel hint=new JLabel();
    JLabel[] playerMoney=new JLabel[4];
    JLabel[] card=new JLabel[6];
    @Override
    public void run() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 30, 1280, 720);
        contentPane = new JPanel();
        MainPanel = new JPanel();
        
        MainPanel.setVisible(false);
        contentPane.setLayout(null);
        setContentPane(contentPane);
    
        JButton Start = new JButton("開始遊戲");
        Start.setBounds(590, 360, 100, 60);
        contentPane.add(Start);
        Start.addActionListener(this);
    
        JButton Rule = new JButton("規則說明");
        Rule.setBounds(590, 460, 100, 60);
        contentPane.add(Rule);
        Rule.addActionListener(this);
    
        JLabel label = new JLabel("歡迎來到11點遊戲");
        label.setBounds(590, 260, 100, 60);
         contentPane.add(label);
    
        JButton Exit = new JButton("離開遊戲");
        Exit.setBounds(590, 560, 100, 60);
        contentPane.add(Exit);
        Exit.addActionListener(this);
    
            
    
        setTitle("Java project");
        setVisible(true);
        
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("開始遊戲")){
            MainPanel.setVisible(true);
            contentPane.setVisible(false);
            setContentPane(MainPanel);
            MainPanel.setLayout(null);
            
            MainPanel.add(Draw);
            Draw.setBounds(1020, 600, 100, 60);
            Draw.setEnabled(false);
            Draw.addActionListener(this);

            MainPanel.add(NoDraw);
            NoDraw.setBounds(1140, 600, 100, 60);
            NoDraw.setEnabled(false);
            NoDraw.addActionListener(this);
            
            MainPanel.add(Raise);
            Raise.setBounds(1020, 500, 100, 60);
            Raise.addActionListener(this);

            MainPanel.add(NoRaise);
            NoRaise.setBounds(1140, 500, 100, 60);
            NoRaise.addActionListener(this);

            

            MyGame game = new MyGame();
            game.frame=this;


            countdown.setBounds(1080, 400, 100, 60);
            MainPanel.add(countdown);

            hint.setBounds(500, 300, 300, 60);
            MainPanel.add(hint);

            for(int i=0;i<4;++i)
            {
                playerMoney[i]=new JLabel();
                playerMoney[i].setBounds(1000, 100+i*100, 100, 60);
                MainPanel.add(playerMoney[i]);
            }

            for(int i=1;i<=5;++i)
            {
                card[i]=new JLabel();
                MainPanel.add(card[i]);
            }

            game.start();
        }
        else if(e.getActionCommand().equals("規則說明")){
            JOptionPane.showMessageDialog(null, "1. 遊戲開始時，每位玩家和電腦各抽一張牌，並下注100元\n2. 玩家和電腦可以選擇是否要加注\n3. 玩家和電腦可以選擇是否要抽牌\n4. 玩家和電腦最多可以抽5張牌\n5. 玩家和電腦的點數最接近11點的人獲勝\n6. 如果有兩位以上的人點數相同，則看誰持有的牌數最多，再相同則平手", "規則說明", JOptionPane.INFORMATION_MESSAGE);
        }
        else if(e.getActionCommand().equals("離開遊戲")){
            System.exit(0);
        }
        else if(e.getActionCommand().equals("抽牌")){
            draw=true;
        }
        else if(e.getActionCommand().equals("加注")){
            Raise.setEnabled(false);
            raise=true;
            NoRaise.setEnabled(false);
            Draw.setEnabled(true);
            NoDraw.setEnabled(true);
        }
        else if(e.getActionCommand().equals("不抽牌")){
            NoDraw.setEnabled(false);
            Draw.setEnabled(false);
        }
        else if(e.getActionCommand().equals("不加注")){
            NoRaise.setEnabled(false);
            Raise.setEnabled(false);
            Draw.setEnabled(true);
            NoDraw.setEnabled(true);
        }
    }
}


class MyGame extends Thread {
    
        //計算點數
        static int countpoint(int rank)
        {
            if((rank+1)%2==0)
            {
                if(rank+1>10)
                {
                    return -10;
                }
                else
                {
                    return -(rank+1);
                }
            }
            else
            {
                if(rank+1>10)
                {
                    return 10;
                }
                else
                {
                    return (rank+1);
                }
            }
        }
        
        //機器人是否要抽牌
        static boolean botWantToDraw(int point)
        {
            if(point>=6||point<=-5) return true;
            else return false;
        }
    
        static boolean botWantToRaise(int point,int money)
        {
            if(point<6&&point>-5&&money/2>=0) return true;
            else return false;
        }
    
        //抽牌funtion
        static int drawCard(HashMap<String,Tuple<ArrayList<Integer>,ArrayList<Integer>>> playerCard,Boolean[][] deck,String player,int playerPoint)
        {
            Random rand=new Random();
            int suit=rand.nextInt(4);
            int rank=rand.nextInt(13);
            while(deck[suit][rank])
            {
                suit=rand.nextInt(4);
                rank=rand.nextInt(13);
            }
            deck[suit][rank]=true;
            Tuple<ArrayList<Integer>,ArrayList<Integer>> tuple=playerCard.get(player);
            tuple.x.add(suit);
            tuple.y.add(rank);
            playerCard.put(player,tuple);
            playerPoint+=countpoint(rank);
            return playerPoint;
        }
    
        MyJFrame frame;
        String player="tiger";
        String bot1="bot1",bot2="bot2",bot3="bot3";
        Boolean[][] deck=new Boolean[4][13];
        String[] Suit= {"flower","heart","square","black"};
        int playerPoint=0,bot1Point=0,bot2Point=0,bot3Point=0;
        int playerhad=1,bot1had=1,bot2had=1,bot3had=1;
        int[] playerTotalMoney={1000,1000,1000,1000};
        int[] playerBet={0,0,0,0};
        int pot=0;
        int mostCard=0,temp=0;
        Boolean[] outBound={false,false,false,false};
        boolean[] tie={false,false,false,false};
        boolean[] win={false,false,false,false};
        HashMap<String,Tuple<ArrayList<Integer>,ArrayList<Integer>>> playerCard=new HashMap<String,Tuple<ArrayList<Integer>,ArrayList<Integer>>>();

    @Override
    public void run()
    {
        playerCard.put(player,new Tuple<ArrayList<Integer>,ArrayList<Integer>>(new ArrayList<Integer>(),new ArrayList<Integer>()));
        playerCard.put(bot1,new Tuple<ArrayList<Integer>,ArrayList<Integer>>(new ArrayList<Integer>(),new ArrayList<Integer>()));
        playerCard.put(bot2,new Tuple<ArrayList<Integer>,ArrayList<Integer>>(new ArrayList<Integer>(),new ArrayList<Integer>()));
        playerCard.put(bot3,new Tuple<ArrayList<Integer>,ArrayList<Integer>>(new ArrayList<Integer>(),new ArrayList<Integer>()));
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<13;j++)
            {
                deck[i][j]=false;
            }
        }
        frame.playerMoney[0].setText(player+"玩家:"+playerTotalMoney[0]);
        //隨機一個人開始抽牌各抽一張
        Random rand=new Random();
        int x=rand.nextInt(4);
        int time=4;
        while(time--!=0)
        {
            
            if(x==0) playerPoint+=drawCard(playerCard,deck,player,0);
            else if(x==1) bot1Point+=drawCard(playerCard,deck,bot1,0);
            else if(x==2) bot2Point+=drawCard(playerCard,deck,bot2,0);
            else if(x==3) bot3Point+=drawCard(playerCard,deck,bot3,0);
            pot+=100;
            playerTotalMoney[x]-=100;
            playerBet[x]+=100;
            x++;
            x%=4;
        }
        time=4;
        //顯示tiger的牌
        for(String key:playerCard.keySet())
        {
            if(key.equals(player))
            {
                Tuple<ArrayList<Integer>,ArrayList<Integer>> tuple=playerCard.get(key);
                for(int i=0;i<tuple.x.size();++i)
                {   
                    ImageIcon icon = new ImageIcon("Java--11points/src/image/"+Suit[tuple.x.get(i)]+(tuple.y.get(i)+1)+".png");
                    Image img = icon.getImage();
                    Image newimg = img.getScaledInstance(120, 160,  java.awt.Image.SCALE_SMOOTH);
                    icon = new ImageIcon(newimg);
                    frame.card[playerhad].setIcon(icon);
                    frame.card[playerhad].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                    frame.card[playerhad].setBounds(260, 450, 120, 160);
                }
            }
        }
        //如果按下加注按鈕就繼續，暫停game20秒
        frame.hint.setText("請選擇是否加注");
        int tempTime=20;
        while(!frame.raise&&tempTime!=-1&&!frame.Draw.isEnabled())
        {
            frame.countdown.setText("剩餘時間:"+tempTime);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tempTime--;
        }
        //加注
        while(time--!=0)
        {
            if(x==0)
            {
                if(frame.raise)
                {
                    int raise = 100;
                    playerBet[x]+=raise;
                    pot+=raise;
                    playerTotalMoney[x]-=raise;
                }
            }
            else if(x==1&&botWantToRaise(bot1Point,playerTotalMoney[x]))
            {
                int raise=rand.nextInt(250);
                playerBet[x]+=raise;
                pot+=raise;
                playerTotalMoney[x]-=raise;
            }
            else if(x==2&&botWantToRaise(bot2Point,playerTotalMoney[x]))
            {
                int raise=rand.nextInt(250);
                playerBet[x]+=raise;
                pot+=raise;
                playerTotalMoney[x]-=raise;
            }
            else if(x==3&&botWantToRaise(bot3Point,playerTotalMoney[x]))
            {
                int raise=rand.nextInt(250);
                playerBet[x]+=raise;
                pot+=raise;
                playerTotalMoney[x]-=raise;
            }
            time--;
            x++;
            x%=4;
        }
        time=4;
        //選擇是否繼續抽牌
        while(time!=0)
        {
            if(x==0&&playerhad<5&&(playerPoint<=11&&playerPoint>=-11))
            {
                frame.hint.setText("請選擇是否抽牌 當前點數為:"+playerPoint);
                tempTime=20;
                while(!frame.draw&&tempTime!=-1&&frame.NoDraw.isEnabled())
                {
                    frame.countdown.setText("剩餘時間:"+tempTime);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tempTime--;
                }
                if(frame.draw)
                {
                    playerPoint=drawCard(playerCard,deck,player,playerPoint);
                    playerhad++;
                    frame.draw=false;
                    for(String key:playerCard.keySet())
                    {
                        if(key.equals(player))
                        {
                            Tuple<ArrayList<Integer>,ArrayList<Integer>> tuple=playerCard.get(key);
                            for(int i=0;i<tuple.x.size();++i)
                            {   
                                ImageIcon icon = new ImageIcon("Java--11points/src/image/"+Suit[tuple.x.get(i)]+(tuple.y.get(i)+1)+".png");
                                Image img = icon.getImage();
                                Image newimg = img.getScaledInstance(120, 160,  java.awt.Image.SCALE_SMOOTH);
                                icon = new ImageIcon(newimg);
                                frame.card[playerhad].setIcon(icon);
                                frame.card[playerhad].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                                frame.card[playerhad].setBounds(260+(playerhad-1)*120+(playerhad-1)*20, 450, 120, 160);
                            }
                        }
                    }
                }
                else
                {
                    time--;
                    x++;
                    x%=4;
                }
            }
            else if(x==1&&botWantToDraw(bot1Point)&&bot1had<=5&&(bot1Point<=11&&bot1Point>=-11))
            {
                bot1Point+=drawCard(playerCard,deck,bot1,0);
                bot1had++;
            }
            else if(x==2&&botWantToDraw(bot2Point)&&bot2had<=5&&(bot2Point<=11&&bot2Point>=-11))
            {
                bot2Point+=drawCard(playerCard,deck,bot2,0);
                bot2had++;
            }
            else if(x==3&&botWantToDraw(bot3Point)&&bot3had<=5&&(bot3Point<=11&&bot3Point>=-11))
            {
                bot3Point+=drawCard(playerCard,deck,bot3,0);
                bot3had++;
            }
            else 
            {
                time--;
                x++;
                x%=4;
            }
        }
        frame.Draw.setEnabled(false);
        frame.NoDraw.setEnabled(false);
        frame.countdown.setText("");
        //印出每個人的牌

        for(String key:playerCard.keySet())
        {
            System.out.println(key+" 's card:");
            Tuple<ArrayList<Integer>,ArrayList<Integer>> tuple=playerCard.get(key);
            for(int i=0;i<tuple.x.size();++i)
            {
                System.out.println(Suit[tuple.x.get(i)]+" "+(tuple.y.get(i)+1));
            }
        }
        //點數最接近0的獲勝 如果有兩位以上則看持有的牌數 再相同則平手
        
        playerPoint=Math.abs(playerPoint);
        bot1Point=Math.abs(bot1Point);
        bot2Point=Math.abs(bot2Point);
        bot3Point=Math.abs(bot3Point);

        frame.hint.setText("玩家點數:"+playerPoint);

        if(playerPoint>11) outBound[0]=true;
        if(bot1Point>11) outBound[1]=true;
        if(bot2Point>11) outBound[2]=true;
        if(bot3Point>11) outBound[3]=true;

        System.out.println(player+" 's point:"+playerPoint);
        System.out.println(bot1+" 's point:"+bot1Point);
        System.out.println(bot2+" 's point:"+bot2Point);
        System.out.println(bot3+" 's point:"+bot3Point);
        int winnable=Math.min(playerPoint,Math.min(bot1Point,Math.min(bot2Point,bot3Point)));
        if(winnable>11) winnable=-1;
        
        if(playerPoint==winnable) win[0]=true;
        if(bot1Point==winnable) win[1]=true;
        if(bot2Point==winnable) win[2]=true;
        if(bot3Point==winnable) win[3]=true;
        
        

        //處理爆的人double
        for(int i=0;i<4;++i)
        {
            if(outBound[i])
            {
                if(i==0) 
                {
                    playerTotalMoney[i]-=playerBet[i];
                    pot+=playerBet[i];
                }
                else if(i==1) 
                {
                    playerTotalMoney[i]-=playerBet[i];
                    pot+=playerBet[i];
                }
                else if(i==2) 
                {
                    playerTotalMoney[i]-=playerBet[i];
                    pot+=playerBet[i];
                }
                else if(i==3) 
                {
                    playerTotalMoney[i]-=playerBet[i];
                    pot+=playerBet[i];
                }
            }
        }
        //分勝負
        for(int i=0;i<4;++i)
        {
            if(win[i]) 
            {
                if(i==0) mostCard=Math.max(mostCard,playerhad);
                else if(i==1) mostCard=Math.max(mostCard,bot1had);
                else if(i==2) mostCard=Math.max(mostCard,bot2had);
                else if(i==3) mostCard=Math.max(mostCard,bot3had);
                temp++;
            }
        }
        if(winnable==-1)
        {
            System.out.println("No one win");
        }
        else if(temp==1)
        {
            for(int i=0;i<4;++i)
            {
                if(win[i]) 
                {
                    if(i==0) 
                    {
                        System.out.println(player+" win");
                        playerTotalMoney[i]+=pot;
                    }
                    else if(i==1) 
                    {
                        System.out.println(bot1+" win");
                        playerTotalMoney[i]+=pot;
                    }
                    else if(i==2) 
                    {
                        System.out.println(bot2+" win");
                        playerTotalMoney[i]+=pot;
                    }
                    else if(i==3) 
                    {
                        System.out.println(bot3+" win");
                        playerTotalMoney[i]+=pot;
                    }
                }
            }
        }
        else if(temp>1)
        {
            int count=0;
            for(int i=0;i<4;++i)
            {
                if(win[i])
                {
                    if(i==0&&playerhad==mostCard) 
                    {
                        tie[i]=true;
                        count++;
                    }
                    else if(i==1&&bot1had==mostCard) 
                    {
                        tie[i]=true;
                        count++;
                    }
                    else if(i==2&&bot2had==mostCard) 
                    {
                        tie[i]=true;
                        count++;
                    }
                    else if(i==3&&bot3had==mostCard) 
                    {
                        tie[i]=true;
                        count++;
                    }
                }
            }
            if(count==1)
            {
                for(int i=0;i<4;++i)
                {
                    if(tie[i]) 
                    {
                        if(i==0) 
                        {
                            System.out.println(player+" win");
                            playerTotalMoney[i]+=pot;
                        }
                        else if(i==1) 
                        {
                            System.out.println(bot1+" win");
                            playerTotalMoney[i]+=pot;
                        }
                        else if(i==2) 
                        {
                            System.out.println(bot2+" win");
                            playerTotalMoney[i]+=pot;
                        }
                        else if(i==3) 
                        {
                            System.out.println(bot3+" win");
                            playerTotalMoney[i]+=pot;
                        }
                    }
                }
            }
            else if(count>1)
            {
                for(int i=0;i<4;++i)
                {
                    if(tie[i]) 
                    {
                        if(i==0) 
                        {
                            System.out.println(player+" tie");
                            playerTotalMoney[i]+=pot/count;
                        }
                        else if(i==1) 
                        {
                            System.out.println(bot1+" tie");
                            playerTotalMoney[i]+=pot/count;
                        }
                        else if(i==2) 
                        {
                            System.out.println(bot2+" tie");
                            playerTotalMoney[i]+=pot/count;
                        }
                        else if(i==3) 
                        {
                            System.out.println(bot3+" tie");
                            playerTotalMoney[i]+=pot/count;
                        }
                    }
                }
            }
        }
        //印出每個人的錢
        for(int i=0;i<4;++i)
        {
            if(i==0) System.out.println(player+" 's money:"+playerTotalMoney[i]);
            else if(i==1) System.out.println(bot1+" 's money:"+playerTotalMoney[i]);
            else if(i==2) System.out.println(bot2+" 's money:"+playerTotalMoney[i]);
            else if(i==3) System.out.println(bot3+" 's money:"+playerTotalMoney[i]);
        }
        pot=0;
    }
    
}

public class Game extends JPanel{
    public static void main(String[] args) {
        MyJFrame window = new MyJFrame();
        window.run();  
    }
}
