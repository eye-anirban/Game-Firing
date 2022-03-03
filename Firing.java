import java.awt.*;
import java.awt.event.*; 
import javax.swing.*;
import java.awt.Rectangle;
class Gun
{   private int x1, x2, y1, y2;
    private int angle, radius;
    Gun()
    {
        x1 = 250;
        y1 = 430;
        angle = 270;
        radius = 30;
        update();
    }
    
    private void update()
    {
        x2 = (int) (radius * Math.cos(Math.toRadians(angle)) + x1);
        y2 = (int) (radius * Math.sin(Math.toRadians(angle)) + y1);
    }
    
    public int getX()
    { return x2; }
    public int getY()
    { return y2; }
    public int getAngle()
    { return angle ; }
    public void moveleft()
    {
        if (angle > 190)
        {
            angle -= 10;
            update();
        }
    }
    
    public void moveright()
    {
        if (angle < 350)
        {
            angle += 10;
            update();
        }
    }
    
    public void draw(Graphics2D g)
    {
        g.setColor(Color.blue);
        g.fillArc(230, 430, 40, 40, 0, 180);
        g.setColor(Color.red);
        BasicStroke wideStroke = new BasicStroke(8.0f); 
        g.setStroke( wideStroke );
        g.drawLine(x1, y1, x2 , y2);
    }
    
    public Rectangle getObject()
    { 
        Rectangle r = new Rectangle(250,430,40,40);
        return r;
    }
    
    public boolean collides(Enemy e)
    {
        Rectangle r1= this.getObject();
        Rectangle r2= e.getObject();
        return (r1.intersects(r2));
    }
}
class Enemy
{
    int x ,y , dy;
    Image pic;
    Enemy(int x)
    {
        this.x = x;
        y = 0;
        dy = 4;
        pic = Toolkit.getDefaultToolkit().getImage("virus1.png");
      }
     public void update()
    {
        y += dy;
    }
    
    public void drawEnemy(Graphics2D g )
    {
     g.drawImage(pic, x, y, 40 , 40 , null);
        
    }
    
    public boolean outOfBounds()
    {
        if ((y + 40) > 470)
               return true;
        return false;
    }
    
    public Rectangle getObject()
    { 
        Rectangle r = new Rectangle(x,y,40,40);
        return r;
    }
}
class Bullet
{
    int x, y , dx , dy, radius ;
    boolean fired ;
    
    public Bullet( )
    {
       x = y = dx = dy = 0 ; radius = 10 ;
       fired = false ;
    }
    
    public void fire(int i , int j, int a)
    {
     int angle ; 
     x = i ; y = j ; angle = a ;  
     double r = Math.toRadians(angle) ;
     dx = (int) ( radius*Math.cos(r) ) ;
     dy = (int) ( radius*Math.sin(r) ) ;
    }
    public void update()
    {
       x = x+dx ; y = y+dy ;
    }
    
    public void draw(Graphics2D g)
    {
        g.setColor(Color.green);
        g.fillOval(x,y,radius,radius);
    }
    
    public Rectangle getObject()
    { 
        Rectangle r = new Rectangle(x,y,radius,radius);
        return r;
    }
    
    public boolean collides(Enemy e)
    {
        Rectangle r1= this.getObject();
        Rectangle r2= e.getObject();
        return (r1.intersects(r2));
    }
    
    public int getX()
    { return x;
    }
    
    public int getY()
    { return y;
    }
}

class Explosion
 {
  private int x ,y,radius , maxradius;
  private Color color;
  boolean done , expand ;
  public Explosion( int x, int y , Color c)
  {
      this.x = x;
      this.y = y;
      maxradius = 40 ;
      radius = 0;
      color = c ;
      done = false; expand = true;
  }
  
  public void update( )
  {
      if(done)
        { return ; }
      if(expand )
       {  
       radius++; 
       if(radius>maxradius)
         { expand =false ; }
        } 
       else 
        { 
            radius--;
            if ( radius <0)
             done = true ;
        }
       
   }
  public void draw(Graphics2D g)
    {
        g.setColor(color);
        BasicStroke wideStroke = new BasicStroke(4.0f); 
        g.setStroke( wideStroke );
        g.fillOval(x, y, radius, radius);
     }
  public boolean status( )
  { return done ; }
    
}   

class GamePanel extends JPanel implements  ActionListener , KeyListener
{
    Timer t;
    Gun gun; 
    Label l;
    Image pic ;
    Enemy e;
    Bullet bullet;
    Explosion ex;
    int score;
    String str;
    boolean gameover;
    public GamePanel()
    {  
        setSize(500,600);
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.gray);
        pic = Toolkit.getDefaultToolkit().getImage("night_sky1.jpg");
        score = 0;
        str = "SCORE = "+score ;
        gameover= false;
        gun = new Gun ();
        t=new Timer(7,this);
        t.start();
    }

    public void actionPerformed(ActionEvent ae)
    {
        if (gameover)
        {
            str="GAMEOVER ! YOUR SCORE :"+score;
            t.stop();
        }
          if(e==null || e.outOfBounds()) 
            {
             int x = ((int)(Math.random() * 460)) ;
             e = new Enemy(x) ;
            }
            e.update();
            if(e!=null)
            {
                if(gun.collides(e))
                {
                    gameover = true;
                }
            }
          if(ex!=null) 
          {
              ex.update();
          }
        if(bullet!=null &&  e!=null)
        {
          if(bullet.collides(e))
          {
              score++;
              str = "SCORE =" +score;
              e=null;
              int x1,y1;
              x1=bullet.getX();
              y1=bullet.getY();
              ex= new Explosion(x1,y1,Color.yellow);
              
          }
        }
          repaint();
    }

    public void paint(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(pic, 0, 0, 500, 500, this);
        Graphics2D  g2d = (Graphics2D)g;
        gun.draw(g2d);
        if(e!=null)
        e.drawEnemy(g2d);
        if(bullet!=null)
        {
            bullet.draw(g2d);
            bullet.update();
        }
        if(ex!=null)
        {
            ex.draw(g2d);
        }
        g.setColor(Color.blue);
        g.setFont(new Font("arial",Font.BOLD ,15));
        g.drawString(str,180,540);
       
    }

    public void keyTyped(KeyEvent ke)
    {
    }

    public void keyReleased(KeyEvent ke)
    {
    } 
    public void keyPressed(KeyEvent ke)
    {int a=ke.getKeyCode();
        switch(a)
        {
            case KeyEvent.VK_LEFT :
            gun.moveleft();
            break;
            case KeyEvent.VK_RIGHT :
            gun.moveright();
            break;
            case KeyEvent.VK_SPACE :
            bullet = new Bullet();
            bullet.fire(gun.getX()-5,gun.getY()-5,gun.getAngle());
            break;
            
        }
        
    }
} 
class MyGameWindow extends JFrame
{
    public MyGameWindow()
    {
        getContentPane().add(new GamePanel());
        pack();
    }
}
public class Firing
{
    public static void main(String args[])
    {
        MyGameWindow obj=new MyGameWindow();
        obj.setSize(500,600);
        obj.setBackground(Color.orange);
        obj.setTitle("Platformer Game");
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.setResizable(false);
    }
}