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

// Пункты меню
    private static Image game_start;
    private static float game_start_left = 100;
    private static float game_start_top = 150;

    private static Image game_exit;
    private static float game_exit_left = 320;
    private static float game_exit_top = 350;

    private static Image game_restart;
    private static float game_restart_left = 200;
    private static float game_restart_top = 250;

// Начальные значения при запуске игры
// Флаги для управления игрой
    private static boolean start_Game = false;
    private static boolean show_menu = true;
    private static boolean end_game = false;

// Параметры капли
    private static float drop_top__default = -100;
    private static float drop_v__default=100;

    private static float drop_left;
    private static float drop_top;
    private static float drop_v;
    private static long last_frame_time;
    private static int score=0;

    private static void setImages() throws IOException{ // Загрузка изображений в наши переменные
        background = ImageIO.read(GameWindow.class.getResourceAsStream("background.png"));
        drop = ImageIO.read(GameWindow.class.getResourceAsStream("drop.png"));
        game_over = ImageIO.read(GameWindow.class.getResourceAsStream("game_over.png"));
        game_restart = ImageIO.read(GameWindow.class.getResourceAsStream("restart.png"));
        game_start = ImageIO.read(GameWindow.class.getResourceAsStream("start_game.png"));
        game_exit = ImageIO.read(GameWindow.class.getResourceAsStream("exit.png"));
    }

    private static void initGame(){   // Установка значений по умолчанию при запуске игры
        drop_top = drop_top__default;
        drop_left = (int) (Math.random()*(game_window.getWidth() - drop.getWidth(null)));
        drop_v = drop_v__default;
        score=0;
        last_frame_time = System.nanoTime();
    }

    private static void playGame() {  // Основная процедура управления игрой

        // Вызываем загрузку изображений
        try { setImages(); }
        catch (IOException e) {
            JOptionPane.showMessageDialog(game_window, "Ошибка загрузки данных");
        }

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

                // если щелчок по капле
                boolean in_drop = x>=drop_left && x<=drop_right && y>=drop_top && y<=drop_bottom;

                // в "старт"
                boolean in_start = x>=game_start_left && x<=(game_start_left+game_start.getWidth(null))&&
                        y>=game_start_top && y<= (game_start_top+game_start.getHeight(null));

                // в "Рестарт"
                boolean in_restart = x>=game_restart_left && x<=(game_restart_left+game_restart.getWidth(null))&&
                        y>=game_restart_top && y<= (game_restart_top+game_restart.getHeight(null));

                // в "выход"
                boolean in_exit = x>=game_exit_left && x<=(game_exit_left+game_exit.getWidth(null))&&
                        y>=game_exit_top && y<= (game_exit_top+game_exit.getWidth(null));

                if (in_drop && start_Game){
                    drop_top=-100;
                    drop_left = (int) (Math.random()*(game_window.getWidth() - drop.getWidth(null)));
                    drop_v+=10;
                    score++;
                    game_window.setTitle("Catche a drop. Your score: "+ score); // +"  "+ new Time(System.currentTimeMillis()));
                }

                if (show_menu && in_start) { //если щелчок по картинке "Старт"
                    game_window.setTitle("In Start");
                    initGame();
                    show_menu = false;
                    end_game = false;
                    start_Game = true;
                }

                if ((show_menu || end_game)&& in_exit) { game_window.dispose(); } //если щелчок по картинке "Выход"

                if (end_game && in_restart) { //если щелчок по картинке "Рестарт"
                    game_window.setTitle("In reStart");
                    initGame();
                    end_game = false;
                    start_Game = true;
                    show_menu = false;
                }
            }
        });
        game_window.add(game_field);
        game_window.setVisible(true);
    }

    private static void showMenu(Graphics g){  //Показ стартового меню
        g.drawImage(game_start, (int)game_start_left, (int)game_start_top, null);
        g.drawImage(game_exit, (int) game_exit_left, (int) game_exit_top, null);
    }

    private static void showEndGame(Graphics g){ //показ завершающего меню
        g.drawImage(game_over,280,25,null);
        g.drawImage(game_restart,(int) game_restart_left, (int) game_restart_top, null);
        g.drawImage(game_exit, (int) game_exit_left,(int) game_exit_top, null);
    }

    public static void main(String[] args) {

//todo добавить уровеь сложности
    playGame();
    }

    private static void onRepaint(Graphics g){
        g.drawImage(background,0,0,null);
        if (show_menu) { showMenu(g); }
        if (end_game) { showEndGame(g); }

//  ------- Начало Рисование капель при условии, что игра запущена --------
        if (start_Game) {
            long current_time = System.nanoTime();
            float delta_time = (current_time-last_frame_time)*0.000000001f;
            last_frame_time=current_time;
            drop_top=drop_top+drop_v*delta_time;
            g.drawImage(drop, (int) drop_left, (int) drop_top, null);
            if (drop_top>=game_window.getHeight()) {
                start_Game = false;
                end_game = true;
            }
            game_window.setTitle("Catche a drop. Your score: "+ score); // +"  " + new Time(System.currentTimeMillis()));
        }
//  ------- Конец Рисование капель при условии, что игра запущена --------
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
