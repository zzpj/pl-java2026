package com.example.pacmanPlugin;

import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.event.KeyListener;

public class PanelLogic extends JBPanel<PanelLogic> {
    private static final int RIGHT = 1;
    private static final int LEFT = 2;
    private static final int UP = 3;
    private static final int DOWN = 4;

    private boolean winSoundPlayed = false;
    private boolean loseSoundPlayed = false;

    private int pacX = 13;
    private int pacY = 7;

    private int dx = 0;
    private int dy = 0;

    private int ghost1X = 9;
    private int ghost1Y = 5;

    private int ghost2X = 10;
    private int ghost2Y = 5;

    private boolean isGameOver = false;

    private ImageIcon currentPacman = Icons.PacRight;
    private ImageIcon PinkGhost = Icons.PinkGhost;
    private ImageIcon BlueGhost = Icons.BlueGhost;

    private final int[][] gameMap;
    private boolean isWon = false;
    private int dotsLeft = 0;//?????????????

    public PanelLogic() {
        this.gameMap = new int[Map.MaP.length][Map.MaP[0].length];
        for (int i = 0; i < Map.MaP.length; i++) {
            for (int j = 0; j < Map.MaP[i].length; j++) {
                gameMap[i][j] = Map.MaP[i][j];
                if (gameMap[i][j] == 0) dotsLeft++;
            }
        }


        setFocusable(true);//??? get keybord focus

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isWon) return;

                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP && camMove(pacX, pacY, 0, -1)) {
                    dx = 0; dy = -1;
                    currentPacman = Icons.PacUp;
                } else if (key == KeyEvent.VK_DOWN && camMove(pacX, pacY, 0, 1)) {
                    dx = 0; dy = 1;
                    currentPacman = Icons.PacDown;
                } else if (key == KeyEvent.VK_LEFT && camMove(pacX, pacY, -1, 0)) {
                    dx = -1; dy = 0;
                    currentPacman = Icons.PacLeft;
                } else if (key == KeyEvent.VK_RIGHT && camMove(pacX, pacY, 1, 0)) {
                    dx = 1; dy = 0;
                    currentPacman = Icons.PacRight;
                }
            }
        });


        Timer timer = new Timer(150, e -> {
            if (!isWon && !isGameOver) {

                movePAcMAn();
                moveGhosts();
                checkCollision();
                repaint();

            }
        });
        timer.start();
    }

    private void movePAcMAn(){
        if(isGameOver) return;

        if(camMove(pacX, pacY, dx, dy)) {
            pacX += dx;
            pacY += dy;

            if(gameMap[pacY][pacX]==0){
                gameMap[pacY][pacX]=2;
                dotsLeft--;
                Sounds.playCoin();
                if(dotsLeft<=0)isWon=true;
            }

        }
    }

    private void moveGhostRandomly(int ghost) {

        int[] dxs = {1,-1,0,0};
        int[] dys = {0,0,1,-1};

        int r = (int)(Math.random()*4);

        int dx = dxs[r];
        int dy = dys[r];

        if (ghost == 1) {
            int nx = ghost1X + dx;
            int ny = ghost1Y + dy;

            if (camMove(pacX, pacY, dx, dy)) {
                ghost1X = nx;
                ghost1Y = ny;
            }

        } else {

            int nx = ghost2X + dx;
            int ny = ghost2Y + dy;

            if (camMove(pacX, pacY, dx, dy)) {
                ghost2X = nx;
                ghost2Y = ny;
            }

        }
    }

    private void checkCollision(){

        if(pacX == ghost1X && pacY == ghost1Y){
            isGameOver = true;

        }

        if(pacX == ghost2X && pacY == ghost2Y){
            isGameOver = true;

        }

    }
    private boolean camMove(int x, int y, int dx, int dy) {

        int nextX = x + dx;
        int nextY = y + dy;

        return nextY >= 0 &&
                nextY < gameMap.length &&
                nextX >= 0 &&
                nextX < gameMap[0].length &&
                gameMap[nextY][nextX] != 1;
    }

    private void movePinkGhost() {

        int[] dxs = {1,-1,0,0};
        int[] dys = {0,0,1,-1};

        while(true) {
            int r = (int) (Math.random() * 4);

            int dx = dxs[r];
            int dy = dys[r];

            if (camMove(ghost1X, ghost1Y, dx, dy)) {
                ghost1X += dx;
                ghost1Y += dy;
                break;
            }
        }
    }
    private void moveBlueGhost() {

        int[] dxs = {1,-1,0,0};
        int[] dys = {0,0,1,-1};

        while(true) {
            int r = (int)(Math.random()*4);

            int dx = dxs[r];
            int dy = dys[r];

            if (camMove(ghost2X, ghost2Y, dx, dy)) {
                ghost2X += dx;
                ghost2Y += dy;
                break;
            }
        }
    }

    private void moveGhosts() {

        movePinkGhost();
        moveBlueGhost();

    }

    @Override
        protected void paintComponent(Graphics G){
        super.paintComponent(G);
        Graphics2D G2=(Graphics2D) G;
        for (int i = 0; i < Map.MaP.length; i++) {
            for (int j = 0; j < Map.MaP[i].length; j++) {
                int X=j*Map.CellSize;
                int Y=i*Map.CellSize;

                if(gameMap[i][j]==1){
                    G2.setColor(new Color(13, 13, 130));
                    G2.fillRect(X+2,Y+2,Map.CellSize-4,Map.CellSize-4);
                } else if (gameMap[i][j]==0){
                    G2.setColor(new Color(241, 186, 235));
                    G2.fillOval(X+8,Y+8,4,4);
                }
            }
        }
        //Icons.PacMan.paintIcon(this,G2,pacX*Map.CellSize,pacY*Map.CellSize);
        //G2.drawImage(((ImageIcon)Icons.PacMan).getImage(),pacX*Map.CellSize,pacY*Map.CellSize, Map.CellSize,Map.CellSize,this);
        G2.drawImage(currentPacman.getImage(), pacX * Map.CellSize, pacY * Map.CellSize, Map.CellSize, Map.CellSize, this);
        G2.drawImage(PinkGhost.getImage(), ghost1X*Map.CellSize,ghost1Y*Map.CellSize, Map.CellSize, Map.CellSize, this);
        G2.drawImage(BlueGhost.getImage(), ghost2X*Map.CellSize,ghost2Y*Map.CellSize, Map.CellSize, Map.CellSize, this);

        if(isWon){
            if(!winSoundPlayed){
                Sounds.playWin();
                winSoundPlayed = true;
            }

            G2.setColor(Color.YELLOW);
            G2.setFont(new Font("Monospaced",Font.BOLD,40));
            G2.drawString("You Win!",100,110);
        }

        if(isGameOver){
            if(!loseSoundPlayed){
                Sounds.playLose();
                loseSoundPlayed = true;
            }
            G2.setColor(Color.RED);
            G2.setFont(new Font("Monospaced",Font.BOLD,40));
            G2.drawString("Game Over",85,110);
        }

    }

    public void restartGame() {
        pacX = 13;
        pacY = 7;
        dx = 0;
        dy = 0;

        ghost1X = 9;
        ghost1Y = 5;
        ghost2X = 10;
        ghost2Y = 5;

        winSoundPlayed = false;
        loseSoundPlayed = false;

        isGameOver = false;
        isWon = false;
        currentPacman = Icons.PacRight;

        dotsLeft = 0;
        for (int i = 0; i < Map.MaP.length; i++) {
            for (int j = 0; j < Map.MaP[i].length; j++) {
                gameMap[i][j] = Map.MaP[i][j];
                if (gameMap[i][j] == 0) dotsLeft++;
            }
        }

        repaint();
        requestFocusInWindow();
    }

    }