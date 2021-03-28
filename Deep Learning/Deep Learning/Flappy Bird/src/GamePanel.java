
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class GamePanel extends JPanel implements Runnable {

    private Game game;
    int max = 0;
    static boolean bool = true;

    public GamePanel() {
        game = new Game();
        new Thread(this).start();
    }

    public void update() {
        game.update();
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;
        for (Render r : game.getRenders()) {
            if (r.transform != null) {
                g2D.drawImage(r.image, r.transform, null);
            } else {
                g.drawImage(r.image, r.x, r.y, null);
            }
        }

        g2D.setColor(Color.BLACK);

        if (!game.started) {
            g2D.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g2D.drawString("Press SPACE to start", 150, 240);
        } else {
            g2D.setFont(new Font("TimesRoman", Font.PLAIN, 24));

            //========================================================
            int count = 30;
            int num = 0;
            int h0, h1;
            for (int i = 0; i < App.numBird; i++) {
                g2D.setColor(Game.bird[i].dead ? Color.red : Color.black);
                g2D.drawString(Integer.toString(Game.bird[i].distance), 500, count);
                count += 50;
            }
            //==========================================================
        }

    }

    public void run() {
        try {
            while (true) {
                update();
                Thread.sleep(25);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
