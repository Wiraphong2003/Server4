import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class PlayerFrame extends JFrame {
    private JPanel panel;
    private int width, height;
    private Container contaentPane;
    private PlayerSprite me;
    private PlayerSprite enemy;
    private DrawingComponent dc;
    private Timer animationTimer;
    private boolean up, down, left, rigth;
    private Socket socket;
    private int playerID, KeyCode;
    private ReadFromserver rfsRunnable;
    private WriteToserver wtsRunnable;
    private boolean isAleveT1 = false;

    private Graphics2D gg;
    private Image img;

    public PlayerFrame(int w, int h) {
        width = w;
        height = h;
        up = false;
        down = false;
        left = false;
        rigth = false;

        Font font = new Font("TimesRoman", Font.BOLD, 12);
        setLayout(new BorderLayout());
        ButtonGroup g1 = new ButtonGroup();
        panel = new JPanel(new FlowLayout());
        panel.setPreferredSize(new Dimension(640, 60));
        JToggleButton buttonAtt = new JToggleButton("Attack");
        JToggleButton buttonflee = new JToggleButton("Flee");

        buttonAtt.setFont(font);
        buttonflee.setFont(font);

        buttonflee.setPreferredSize(new Dimension(200, 60));
        buttonAtt.setPreferredSize(new Dimension(200, 60));
        g1.add(buttonflee);
        g1.add(buttonAtt);

        buttonAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (playerID == 1) {
                        ThreadAttMove(1,-1);
                        dc.repaint();
                        System.out.println("P1");
                    } else if (playerID == 2) {
                        ThreadAttMove(-1,1);
                        dc.repaint();
                        System.out.println("P2");
                    } else {
                        ThreadAttMove(-1, 1);
                        dc.repaint();
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonflee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (playerID == 1) {
                        ThreadFellMove(-1);
                        System.out.println("P1");
                    } else if (playerID == 2) {
                        ThreadFellMove(1);
                        System.out.println("P2");
                    } else {
                        ThreadFellMove(1);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        panel.add(buttonflee, BorderLayout.EAST);
        panel.add(buttonAtt, BorderLayout.WEST);

        // add(panel, BorderLayout.SOUTH);

    }

    private void ThreadFellMove(int move) {
        Thread t2 = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < 360; i++) {
                        me.moveH(move);
                        dc.repaint();
                        // System.out.println("T0: " + i);
                        Thread.sleep(5);
                    }
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        t2.start();
        System.out.println(t2.isAlive());
    }

    private void ThreadAttMove(int Move1, int Move2) {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < 360; i++) {
                        me.moveH(Move1);
                        dc.repaint();
                        // System.out.println("T1: " + i + "\tisAlive: " + isAlive());
                        if (i == 359) {
                            Thread t2 = new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        for (int i = 0; i < 360; i++) {
                                            me.moveH(Move2);
                                            dc.repaint();
                                            // System.out.println("T2: " + i);
                                            Thread.sleep(5);
                                        }
                                    } catch (InterruptedException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            };
                            dc.repaint();
                            t2.start();
                            // System.out.println(t2.isAlive());
                        }
                        // System.out.println(isAleveT1);
                        Thread.sleep(5);
                    }
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        t.start();
        // System.out.println(isAleveT1);
    }

    public void setUpGUI() {

        contaentPane = this.getContentPane();
        // protected void paintComponent(Graphics g) {
        // Graphics2D g2d = (Graphics2D) g;
        // enemy.drawSperite(g2d);
        // me.drawSperite(g2d);
        // }
        // contaentPane.setBackground(new Color(75, 241, 197, 163));

        contaentPane.setLayout(new BorderLayout());
        img = Toolkit.getDefaultToolkit()
                .createImage(System.getProperty("user.dir") + File.separator + "BackgroundCharacter.JPG");
        Image img1 = Toolkit.getDefaultToolkit()
                .createImage(System.getProperty("user.dir") + File.separator + "ch2_default.GIF");
        // gg.drawImage(img,0,0,700,500,0,0,600,360,this);
        // contaentPane.paintComponents(gg);
        contaentPane.prepareImage(img, this);
        contaentPane.add(panel, BorderLayout.SOUTH);
        contaentPane.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (playerID==1){
                    me.setX(e.getX()-25);
                    me.setY(e.getY()-25);
                    dc.repaint();
                }
                else if (playerID==2){
                    me.setX(e.getX()-25);
                    me.setY(e.getY()-25);
                    dc.repaint();
                }
                else{
                    me.setX(e.getX()+25);
                    me.setY(e.getY()+25);

                    enemy.setX(e.getX()-25);
                    enemy.setY(e.getY()-25);
                    dc.repaint();
                }

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //System.out.println("X: "+e.getX()+"\tY: "+e.getY());
            }
        });
        this.setTitle("Player#" + playerID);
        contaentPane.setPreferredSize(new Dimension(width, height));
        createSprites();
        dc = new DrawingComponent();
        contaentPane.add(dc);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        setUpAnimationTimer();
        setUpKeyListnener();
        setMouseListnener();
    }

    private void createSprites() {
        if (playerID == 1) {
            me = new PlayerSprite(100, 350, 50, Color.BLUE);
            enemy = new PlayerSprite(490, 350, 50, Color.RED);
        } else {
            enemy = new PlayerSprite(100, 350, 50, Color.BLUE);
            me = new PlayerSprite(490, 350, 50, Color.RED);
        }

    }

    private void setUpAnimationTimer() {
        int inervar = 10;
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double speed = 5;
                if (up) {
                    me.moveV(-speed);
                } else if (down) {
                    me.moveV(speed);
                } else if (left) {
                    me.moveH(-speed);
                } else if (rigth) {
                    me.moveH(speed);
                }
                dc.repaint();
            }
        };
        animationTimer = new Timer(inervar, al);
        animationTimer.start();
    }

    private void setMouseListnener() {
        MouseMotionListener Ml = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // System.out.println("Mouse: " + e.getX() + "\t" + e.getY());
                me.x = e.getX();

            }
        };
    }

    private void setUpKeyListnener() {
        KeyListener kl = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                KeyCode = e.getKeyCode();
                System.out.println("KeyCode: " + KeyCode);
                switch (KeyCode) {
                    case KeyEvent.VK_UP:
                        up = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        down = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        left = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rigth = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("KeyCode: " + KeyCode);
                KeyCode = e.getKeyCode();
                switch (KeyCode) {
                    case KeyEvent.VK_UP:
                        up = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        down = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        left = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rigth = false;
                        break;
                }
            }
        };
        contaentPane.addKeyListener(kl);
        contaentPane.setFocusable(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("192.168.100.154", 12345);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            playerID = in.readInt();
            System.out.println("You are Player#" + playerID);
            if (playerID == 1) {
                System.out.println("Waiting fro Player #2 to Connect..");
            }
            rfsRunnable = new ReadFromserver(in);
            wtsRunnable = new WriteToserver(out);
            rfsRunnable.waitForStarMsg();
        } catch (IOException ex) {
            System.out.println("IOExecption from connectToServer()");
        }
    }

    private class DrawingComponent extends JComponent {
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            enemy.drawSperite(g2d);
            me.drawSperite(g2d);
        }
    }

    private class ReadFromserver implements Runnable {
        private DataInputStream dataIn;

        public ReadFromserver(DataInputStream in) {
            dataIn = in;
            System.out.println("RFS Runnable");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (enemy != null) {
                        enemy.setX(dataIn.readDouble());
                        enemy.setY(dataIn.readDouble());
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOEcrption From RFS Run()");
            }
        }

        public void waitForStarMsg() {
            try {
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server " + startMsg);
                Thread readThread = new Thread(rfsRunnable);
                Thread writeThread = new Thread(wtsRunnable);
                readThread.start();
                writeThread.start();
            } catch (IOException ex) {

            }
        }
    }

    private class WriteToserver implements Runnable {
        private DataOutputStream dataOut;

        public WriteToserver(DataOutputStream in) {
            dataOut = in;
            System.out.println("WTS Runnable");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (me != null) {
                        dataOut.writeDouble(me.getX());
                        dataOut.writeDouble(me.getY());
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedException");
                    }
                }
            } catch (IOException ex) {
                System.out.println("from WTS run()");
            }

        }
    }

    public static void main(String[] args) {
        PlayerFrame playerFrame = new PlayerFrame(700, 500);
        playerFrame.connectToServer();
        playerFrame.setUpGUI();
    }
}
