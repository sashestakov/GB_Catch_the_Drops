package ru.geekbrains.catch_the_drops;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GameWindow extends JFrame{

    private static GameWindow game_window;
    private static Image background;
    private static Image drop;
    private static Image game_over;
    private static float drop_left = 100;
    private static float drop_top = -100;
    private static long last_frame_time;
    private static float drop_v=100;
    private static int score=0;

// Дополнительные поля для ограничения игры
    private static int game_range;


    public static void main(String[] args) throws IOException {
	    background = ImageIO.read(GameWindow.class.getResourceAsStream("background.png"));
        drop = ImageIO.read(GameWindow.class.getResourceAsStream("drop.png"));
        game_over = ImageIO.read(GameWindow.class.getResourceAsStream("game_over.png"));
        last_frame_time = System.nanoTime();

//todo добавить меню игры

//todo добавить уровеь сложности

        game_window=new GameWindow();
        game_window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game_window.setLocation(200,100);
        game_window.setSize(906,478);
        game_window.setResizable(false);

        GameField game_field = new GameField();
        game_field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                float drop_right = drop_left + drop.getWidth(null);
                float drop_bottom = drop_top + drop.getHeight(null);
                boolean in_drop = x>=drop_left && x<=drop_right && y>=drop_top && y<=drop_bottom;
                if (in_drop){
                    drop_top=-100;
                    drop_left = (int) (Math.random()*(game_window.getWidth() - drop.getWidth(null)));
                    drop_v+=10;
                    score++;
                    game_window.setTitle("Catche a drop. Your score: "+ score);
                }
            }
        });



        game_window.add(game_field);
        game_window.setVisible(true);
        game_window.setTitle("Catche a drop. Your score: "+ score);

    }
    private static void onRepaint(Graphics g){
        long current_time = System.nanoTime();
        float delta_time = (current_time-last_frame_time)*0.000000001f;
        last_frame_time=current_time;
        drop_top=drop_top+drop_v*delta_time;

        g.drawImage(background,0,0,null);
        g.drawImage(drop, (int) drop_left, (int) drop_top, null);
        if (drop_top>=game_window.getHeight()) { g.drawImage(game_over,280,100,null); }
    }

    private static class GameField extends JPanel{
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            onRepaint(g);
            repaint();
        }
    }
}
