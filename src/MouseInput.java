import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private final Vector2d previousPosition;
    private final Vector2d currentPosition;
    private final Vector2f mouseVelocity;
    private boolean onWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    public MouseInput(long window) {
        previousPosition = new Vector2d(-1, -1);
        currentPosition = new Vector2d(0, 0);
        mouseVelocity = new Vector2f();
        setupMouseEvents(window);
    }

    public Vector2f getMouseVelocity() {
        return mouseVelocity;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return  rightButtonPressed;
    }

    public boolean isOnWindow() {
        return onWindow;
    }

    public void setupMouseEvents(long window) {
        glfwSetCursorPosCallback(window, (_window, positionX, positionY) -> {
            currentPosition.x = positionX;
            currentPosition.y = positionY;
        });
        glfwSetCursorEnterCallback(window, (_window, entered) -> {
            onWindow = entered;
        });
        glfwSetMouseButtonCallback(window, (_window, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public void input() {
        mouseVelocity.x = 0;
        mouseVelocity.y = 0;
        if (previousPosition.x > 0 && previousPosition.y > 0 && onWindow) {
            double deltaX = currentPosition.x - previousPosition.x;
            double deltaY = currentPosition.y - previousPosition.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                mouseVelocity.y = (float) deltaX;
            }
            if (rotateY) {
                mouseVelocity.x = (float) deltaY;
            }
        }
        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }
}
