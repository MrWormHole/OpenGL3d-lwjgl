import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static long window;
    private static String title;
    private static int width;
    private static int height;
    private static boolean resized;

    public static long create(int windowWidth, int windowHeight, String windowTitle, boolean isVsyncEnabled) {
        configure();
        title = windowTitle;
        width = windowWidth;
        height = windowHeight;
        resized = false;

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        makeCurrentContext(isVsyncEnabled);
        return window;
    }

    public static void configure() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_SAMPLES, 8); //8x anti-aliasing
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); //major version of context is set to 3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2); //minor version of context is set to 2
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); //openGL core profile
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); //good to have openGL forward compatibility
    }

    public static void makeCurrentContext(boolean isVsyncEnabled) {
        // Setup resize callback
        glfwSetFramebufferSizeCallback(window, (_window, windowWidth, windowHeight) -> {
            width = windowWidth;
            height = windowHeight;
            resized = true;
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (_window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(_window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        if(isVsyncEnabled) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(window);
        GLCapabilities caps = GL.createCapabilities();
        if (caps.OpenGL30) {
            System.out.println("WE ARE USING OPENGL 3.0 VERSION");
        }
        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    public static void destroy() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }


    public static boolean isResized() {
        return  resized;
    }

    public static void setResized(boolean value) {
        resized = value;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void clear() {
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    public static void update() {
        glfwSwapBuffers(window); // swap the color buffers
        glfwPollEvents();
    }

}
