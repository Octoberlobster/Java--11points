package src;

import java.util.Scanner;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

public class Game {
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
    //Pair
    public static class Tuple<X, Y> {
        public X x;
        public Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
    //機器人是否要抽牌
    static boolean botWantToDraw(int point)
    {
        if(point>=6||point<=-5) return true;
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


    public static void main(String[] args) {
        String player="tiger";
        String bot1="bot1",bot2="bot2",bot3="bot3";
        Boolean[][] deck=new Boolean[4][13];
        String[] Suit= {"Spade","Heart","Diamond","Club"};
        int playerPoint=0,bot1Point=0,bot2Point=0,bot3Point=0;
        int playerhad=1,bot1had=1,bot2had=1,bot3had=1;
        HashMap<String,Tuple<ArrayList<Integer>,ArrayList<Integer>>> playerCard=new HashMap<String,Tuple<ArrayList<Integer>,ArrayList<Integer>>>();
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
            x++;
            x%=4;
        }
        time=4;
        //選擇是否繼續抽牌
        while(time!=0)
        {
            if(x==0&&playerhad<5&&(playerPoint<=11&&playerPoint>=-11))
            {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Do you want to draw a card? (Y/N)");
                System.out.println(player+" 's point:"+playerPoint);
                String input = scanner.nextLine();
                if(input.equals("Y"))
                {
                    playerPoint+=drawCard(playerCard,deck,player,0);
                    playerhad++;
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
        System.out.println(player+" 's point:"+playerPoint);
        System.out.println(bot1+" 's point:"+bot1Point);
        System.out.println(bot2+" 's point:"+bot2Point);
        System.out.println(bot3+" 's point:"+bot3Point);
        int winnable=Math.min(playerPoint,Math.min(bot1Point,Math.min(bot2Point,bot3Point)));
        if(winnable>11) winnable=-1;
        boolean[] win={false,false,false,false};
        if(playerPoint==winnable) win[0]=true;
        if(bot1Point==winnable) win[1]=true;
        if(bot2Point==winnable) win[2]=true;
        if(bot3Point==winnable) win[3]=true;
        int mostCard=0,temp=0;
        boolean[] draw={false,false,false,false};
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
        if(temp==1)
        {
            for(int i=0;i<4;++i)
            {
                if(win[i]) 
                {
                    if(i==0) System.out.println(player+" win");
                    else if(i==1) System.out.println(bot1+" win");
                    else if(i==2) System.out.println(bot2+" win");
                    else if(i==3) System.out.println(bot3+" win");
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
                        draw[i]=true;
                        count++;
                    }
                    else if(i==1&&bot1had==mostCard) 
                    {
                        draw[i]=true;
                        count++;
                    }
                    else if(i==2&&bot2had==mostCard) 
                    {
                        draw[i]=true;
                        count++;
                    }
                    else if(i==3&&bot3had==mostCard) 
                    {
                        draw[i]=true;
                        count++;
                    }
                }
            }
            if(count==1)
            {
                for(int i=0;i<4;++i)
                {
                    if(draw[i]) 
                    {
                        if(i==0) System.out.println(player+" win");
                        else if(i==1) System.out.println(bot1+" win");
                        else if(i==2) System.out.println(bot2+" win");
                        else if(i==3) System.out.println(bot3+" win");
                    }
                }
            }
            else if(count>1)
            {
                for(int i=0;i<4;++i)
                {
                    if(draw[i]) 
                    {
                        if(i==0) System.out.println(player+" draw");
                        else if(i==1) System.out.println(bot1+" draw");
                        else if(i==2) System.out.println(bot2+" draw");
                        else if(i==3) System.out.println(bot3+" draw");
                    }
                }
            }
        }
        else if(winnable==-1)
        {
            System.out.println("No one win");
        }

    }
}
