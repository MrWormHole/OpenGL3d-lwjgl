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
    Gameobject[] gameobjects = new Gameobject[5];

    //square
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
    //square

    //cube
    float[] positions_cube = new float[] {
            // VO
            -0.5f,  0.5f,  0.5f,
            // V1
            -0.5f, -0.5f,  0.5f,
            // V2
            0.5f, -0.5f,  0.5f,
            // V3
            0.5f,  0.5f,  0.5f,
            // V4
            -0.5f,  0.5f, -0.5f,
            // V5
            0.5f,  0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,
    };

    float[] colors_cube = new float[]{
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
    };

    int[] indices_cube = new int[] {
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            4, 0, 3, 5, 4, 3,
            // Right face
            3, 2, 7, 5, 3, 7,
            // Left face
            6, 1, 0, 6, 0, 4,
            // Bottom face
            2, 1, 6, 2, 6, 7,
            // Back face
            7, 6, 4, 7, 4, 5,
    };
    //cube

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

        mesh = new Mesh(positions_cube,indices_cube,colors_cube);
        Gameobject testGameObject = new Gameobject(mesh);
        Gameobject testGameObject2 = new Gameobject(mesh);
        Gameobject testGameObject3 = new Gameobject(mesh);

        gameobjects = new Gameobject[5];
        gameobjects[0] = new Gameobject(mesh);
        gameobjects[1] = new Gameobject(mesh);
        gameobjects[2] = new Gameobject(mesh);
        gameobjects[3] = new Gameobject(mesh);
        gameobjects[4] = new Gameobject(mesh);

        gameobjects[0].setPosition(0,0,-10);
        gameobjects[1].setPosition(0,3,-10);
        gameobjects[2].setPosition(0,-3,-10);
        gameobjects[3].setPosition(3,0,-10);
        gameobjects[4].setPosition(-3,0,-10);
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
        for(Gameobject gameobject: gameobjects) {
            float rotation = gameobject.getRotation().x + 1.5f;
            if ( rotation > 360 ) {
                rotation = 0;
            }
            gameobject.setRotation(rotation, rotation, rotation);
        }
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