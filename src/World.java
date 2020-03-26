import com.sun.prism.impl.VertexBuffer;
import org.joml.Matrix4f;
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

    boolean running = true;
    Timer timer;
    Shader shader;
    Mesh mesh;
    Transformation transformation;
    Gameobject[] gameobjects;

//    float[] positions = new float[]{
//            -0.5f,  0.5f, 0.0f,
//            -0.5f, -0.5f, 0.0f,
//            0.5f, -0.5f, 0.0f,
//            0.5f,  0.5f, 0.0f,
//    };
    float[] positions = new float[]{
            -1.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f,  1.0f, 0.0f,
    };
    int[] indices = new int[]{
            0, 1, 3, 3, 1, 2,
    };
    float[] colors = new float[]{
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
    };

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        init();
        gameLoop();
        dispose();
    }

    private void init() {
        Window.create(600,800,"World Experiments", true);
        timer = new Timer();
        transformation = new Transformation();

        try {
            shader = new Shader("./shaders/rainbow.vs","./shaders/rainbow.fs");
        } catch(Exception e) {
            System.out.println("Shader loading error: " + e);
        }

        mesh = new Mesh(positions,indices,colors);
        Gameobject testGameObject = new Gameobject(mesh);
        testGameObject.setPosition(0, 0, -10);
        gameobjects = new Gameobject[] { testGameObject };
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        if ( Window.isResized() ) {
            glViewport(0, 0, Window.getWidth(), Window.getHeight());
            Window.setResized(false);
        }

        shader.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix((float) Math.toRadians(60.0f), Window.getWidth(), Window.getHeight(), 0.01f, 1000.f);
        shader.setUniform("projectionMatrix", projectionMatrix);

        for(Gameobject gameobject: gameobjects) {
            ///outside of the scope for rendering
            // Set world matrix for this
            Matrix4f worldMatrix = transformation.getWorldMatrix(
                    gameobject.getPosition(),
                    gameobject.getRotation(),
                    gameobject.getScale());
            shader.setUniform("worldMatrix", worldMatrix);
            // Render the mesh for this
            gameobject.getMesh().render();
        }

        shader.unbind();

        Window.update();
    }

    private static void dispose() {
        Window.destroy();
    }

    public static void main(String[] args) {
        new World().run();
    }

}