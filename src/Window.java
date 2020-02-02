import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static long window;

    public static long create(int width, int height, String title) {
        configure();
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        return window;
    }

    public static void configure() {
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); //major version of context is set to 3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2); //minor version of context is set to 2
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); //openGL core profile
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); //good to have openGL forward compatibility
    }

    public static void destroy() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    public static boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public static void clear() {
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    public static void update() {
        glfwSwapBuffers(window); // swap the color buffers

        glfwPollEvents();
    }

}
