package mainpack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;

import static java.lang.Math.*;

//Класс реализует компонент - круговой регулятор
public class CircleComponent extends JComponent {

    //Предпочтительные размеры компонента
    private final int PREFFERED_WIDTH = 150;
    private final int PREFFERED_HEIGHT = 150;

    //Границы цветовых диапазонов
    private final int RED_START_INTERVAL = 0;
    private final int RED_STOP_INTERVAL = 255;
    private final int GREEN_START_INTERVAL = 10;
    private final int GREEN_STOP_INTERVAL = 100;
    private final int BLUE_START_INTERVAL = 255;
    private final int BLUE_STOP_INTERVAL = 0;

    //Цвет указателя (риски) на регуляторе
    private final Color pointerColor = Color.WHITE;

    //Границы возможных значений регулятора
    private final int VALUE_START_INTERVAL = 0;
    private final int VALUE_STOP_INTERVAL = 100;

    //Границы возможных значений угла поворота регулятора
    private final double ANGLE_START_INTERVAL = 240;
    private final double ANGLE_STOP_INTERVAL = -60;

    //Положение регулятора
    private int value;

    //Специализированный класс для хранения информации о положении указателя (риски) регулятора
    private class RegulatorPointsCoord {
        int x1, y1;
        int x2, y2;
        int x3, y3;

        public RegulatorPointsCoord(int x1, int y1, int x2, int y2, int x3, int y3) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.x3 = x3;
            this.y3 = y3;
        }
    }

    //Список слушателей для событий нашего компонента
    private LinkedList<CircleComponentListener> listeners = new LinkedList<>();

    //Слушатель для событий мыши, управляющих данным компонентом
    private MouseListener mouseListener;

    //Конструктор
    public CircleComponent() {
        value = VALUE_START_INTERVAL;
        mouseListener = new MouseListener();
        addMouseListener(mouseListener);
        addMouseWheelListener(mouseListener);
        setPreferredSize(new Dimension(PREFFERED_WIDTH, PREFFERED_HEIGHT));
    }

    //Перегруженный конструктор, инициализирующий компонент начальным значением value
    public CircleComponent(int value) {
        this();
        setValue(value);
    }

    //Метод устанавливает положение регулятора
    public void setValue(int value) {
        if (value < VALUE_START_INTERVAL) value = VALUE_START_INTERVAL;
        if (value > VALUE_STOP_INTERVAL) value = VALUE_STOP_INTERVAL;
        this.value = value;
        super.repaint();
    }

    //Метод, присоединяющий слушателей
    public void addComponentMouseListener(CircleComponentListener listener) {
        listeners.add(listener);
    }

    //Метод, удаляющий слушателей
    public void removeComponentMouseListener(CircleComponentListener listener) {
        listeners.remove(listener);
    }

    //Метод отрисовки компонента
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(getCurrentRegulatorColor());
        g2d.fillOval(0, 0, getRadius() * 2, getRadius() * 2);

        g2d.setColor(Color.white);
        g2d.fillOval((int) (getRadius() * 0.5), (int) (getRadius() * 0.5), (int) (getRadius()), (int) (getRadius()));

        g2d.setColor(pointerColor);
        int[] x = new int[3];
        int[] y = new int[3];
        RegulatorPointsCoord pointsCoord = getRegulatorPointsCoord();
        x[0] = pointsCoord.x1;
        y[0] = pointsCoord.y1;
        x[1] = pointsCoord.x2;
        y[1] = pointsCoord.y2;
        x[2] = pointsCoord.x3;
        y[2] = pointsCoord.y3;
        g2d.fillPolygon(x, y, 3);

    }

    //Метод, рассылающий слушателям данного компонента уведомление об изменении значения value
    protected void fireComponentChangeValue() {
        CircleComponentEvent event;
        event = new CircleComponentEvent(this, value);
        for (CircleComponentListener listener : listeners) listener.componentClick(event);
    }

    //Обработчик нажатия на кнопки мыши
    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1 & isPointInsideCircle(e.getX(), e.getY())) {
                double angle;
                angle = getAnglePoint(e.getX(), e.getY());
                if (angle > ANGLE_START_INTERVAL) {
                    setValue(VALUE_START_INTERVAL);
                    fireComponentChangeValue();
                    return;
                }
                if (angle < ANGLE_STOP_INTERVAL) {
                    setValue(VALUE_STOP_INTERVAL);
                    fireComponentChangeValue();
                    return;
                }
                double deltaAngle = (ANGLE_STOP_INTERVAL - ANGLE_START_INTERVAL) / (VALUE_STOP_INTERVAL - VALUE_START_INTERVAL);
                setValue((int)((angle - ANGLE_START_INTERVAL) / deltaAngle));
                fireComponentChangeValue();
            }
        }


        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (isPointInsideCircle(e.getX(), e.getY())) {
                if (e.getWheelRotation() == -1) {
                    if (value == VALUE_STOP_INTERVAL) return;
                    setValue(++value);
                    fireComponentChangeValue();
                    return;
                }
                if (e.getWheelRotation() == 1) {
                    if (value == VALUE_START_INTERVAL) return;
                    setValue(--value);
                    fireComponentChangeValue();
                    return;
                }

            }
        }
    }

    //Метод возвращает текущее положение указателя (риски) регулятора
    public int getValue() {
        return value;
    }

    //Метод возвращает радиус регулятора
    private int getRadius() {
        return (int) (Math.min(getWidth(), getHeight()) / 2);
    }

    //Метод возвращает true, если точка с координатами x,y попадает в регулятор и false - в противном случае
    private boolean isPointInsideCircle(int x, int y) {
        int xCenter, yCenter;
        int distance;
        xCenter = getXCoordCenter();
        yCenter = getYCoordCenter();
        distance = (int) (Math.sqrt((xCenter - x) * (xCenter - x) + (yCenter - y) * (yCenter - y)));
        return (distance >= getRadius() * 0.5) & (distance <= getRadius());
    }

    //Метод возвращает x-координату центра регулятора
    private int getXCoordCenter() {
        return getRadius();
    }

    //Метод возвращает y-координату центра регулятора
    private int getYCoordCenter() {
        return getRadius();
    }

    //Метод возвращает текущий цвет регулятора (цвет зависит от значения volume)
    private Color getCurrentRegulatorColor() {
        double deltaRed;
        double deltaGreen;
        double deltaBlue;
        int red;
        int green;
        int blue;
        deltaRed = (RED_STOP_INTERVAL - RED_START_INTERVAL) / (VALUE_STOP_INTERVAL - VALUE_START_INTERVAL);
        deltaGreen = (GREEN_STOP_INTERVAL - GREEN_START_INTERVAL) / (VALUE_STOP_INTERVAL - VALUE_START_INTERVAL);
        deltaBlue = (BLUE_STOP_INTERVAL - BLUE_START_INTERVAL) / (VALUE_STOP_INTERVAL - VALUE_START_INTERVAL);
        red = (int) (RED_START_INTERVAL + value * deltaRed);
        green = (int) (GREEN_START_INTERVAL + value * deltaGreen);
        blue = (int) (BLUE_START_INTERVAL + value * deltaBlue);
        return new Color(red, green, blue);
    }

    //Метод возвращает координаты точек, необходимые для отрисовки указателя регулятора
    private RegulatorPointsCoord getRegulatorPointsCoord() {
        double x1, y1, angle1;
        double x2, y2, angle2;
        double x3, y3, angle3;
        double deltaAngle;

        deltaAngle = (ANGLE_STOP_INTERVAL - ANGLE_START_INTERVAL) / (VALUE_STOP_INTERVAL - VALUE_START_INTERVAL);
        angle1 = ANGLE_START_INTERVAL + value * deltaAngle;
        angle2 = angle1 - 10;
        angle3 = angle1 + 10;

        x1 = getXCoordCenter() + getRadius() * 0.95 * cos(toRadians(angle1));
        y1 = getYCoordCenter() - getRadius() * 0.95 * sin(toRadians(angle1));

        x2 = getXCoordCenter() + getRadius() * 0.6 * cos(toRadians(angle2));
        y2 = getYCoordCenter() - getRadius() * 0.6 * sin(toRadians(angle2));

        x3 = getXCoordCenter() + getRadius() * 0.6 * cos(toRadians(angle3));
        y3 = getYCoordCenter() - getRadius() * 0.6 * sin(toRadians(angle3));

        return new RegulatorPointsCoord((int) (x1), (int) y1, (int) x2, (int) y2, (int) x3, (int) y3);
    }

    //Метод возвращает угол (в градусах) отклонения точки с координатами x,y
    private double getAnglePoint(int x, int y) {
        double cosAngle;
        double angle;
        double r;
        r = sqrt((x - getXCoordCenter()) * (x - getXCoordCenter()) + (y - getYCoordCenter()) * (y - getYCoordCenter()));
        cosAngle = (x - getXCoordCenter()) / r;
        angle = acos(cosAngle);

        if (y <= getYCoordCenter()) return toDegrees(angle);
        if (y > getYCoordCenter()) {
            if (angle > (PI / 2)) return toDegrees(2 * PI - angle);
            if (angle <= (PI / 2)) return toDegrees(-angle);
        }
        return ANGLE_START_INTERVAL;
    }

}
