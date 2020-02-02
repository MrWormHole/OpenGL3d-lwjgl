import com.sun.prism.impl.VertexBuffer;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class World {

    boolean running;
    Timer timer;
    Shader shader;
    Mesh mesh;

    float[] vertices = new float[]{
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        init();
        gameLoop();
        dispose();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        running = true;
        Window.create(600,800,"Hello World");
        timer = new Timer();

        try {
            shader = new Shader();
            //do some work on Utils
            shader.createVertexShader(Utils.loadResource("./shaders/vertex.vs"));
            //do some work on Utils
            shader.createFragmentShader(Utils.loadResource("./shaders/fragment.fs"));
            shader.link();
        } catch(Exception e) {
            System.out.println(e);
        }

        mesh = new Mesh(vertices);
    }

    private void gameLoop() {
        float delta;
        float accumulator = 0f;
        float interval = 1f / 30f;
        float alpha;

        while(running && !Window.windowShouldClose()) {
            delta = timer.getDelta();
            accumulator += delta;

            input();
            while(accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }
            alpha = accumulator / interval;
            render(alpha);

            Window.clear();
        }
    }

    private void input() {

    }

    private void update(float delta) {

    }

    private void render(float alpha) {
        shader.bind(); //for now testing only one shader
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glBindVertexArray(mesh.getVaoId());
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.unbind(); //for now testing only one shader

        Window.update();
    }

    private void dispose() {
        Window.destroy();
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new World().run();
    }

}