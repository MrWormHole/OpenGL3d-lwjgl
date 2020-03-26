import com.sun.prism.impl.VertexBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
    Texture texture;
    Mesh mesh;
    Transformation transformation;
    Gameobject[] gameobjects;

    private int displxInc;
    private int displyInc;
    private int displzInc;
    private int scaleInc;

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
            // V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,

            // For text coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,

            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,

            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,

            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f,
    };

    int[] indices_cube = new int[] {
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7
    };

    float[] texture_coordinates_cube = new float[] {
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,

            // For text coords in top face
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,

            // For text coords in right face
            0.0f, 0.0f,
            0.0f, 0.5f,

            // For text coords in left face
            0.5f, 0.0f,
            0.5f, 0.5f,

            // For text coords in bottom face
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f,
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
            shader = new Shader("./shaders/textured.vs","./shaders/textured.fs");
            texture = new Texture("./textures/grassblock.png");
        } catch(Exception e) {
            System.out.println("Shader loading error: " + e);
        }
        mesh = new Mesh(positions_cube,indices_cube,texture_coordinates_cube, texture);

        gameobjects = new Gameobject[5];
        gameobjects[0] = new Gameobject(mesh);
        gameobjects[1] = new Gameobject(mesh);
        gameobjects[2] = new Gameobject(mesh);
        gameobjects[3] = new Gameobject(mesh);
        gameobjects[4] = new Gameobject(mesh);

        gameobjects[0].setPosition(0,0,-10);
        gameobjects[1].setPosition(0,1,-10);
        gameobjects[2].setPosition(0,-1,-10);
        gameobjects[3].setPosition(1,0,-10);
        gameobjects[4].setPosition(-1,0,-10);
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
        displyInc = 0;
        displxInc = 0;
        displzInc = 0;
        scaleInc = 0;
        if (Window.isKeyPressed(GLFW_KEY_UP)) {
            displyInc = 1;
        } else if (Window.isKeyPressed(GLFW_KEY_DOWN)) {
            displyInc = -1;
        } else if (Window.isKeyPressed(GLFW_KEY_LEFT)) {
            displxInc = -1;
        } else if (Window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displxInc = 1;
        } else if (Window.isKeyPressed(GLFW_KEY_A)) {
            displzInc = -1;
        } else if (Window.isKeyPressed(GLFW_KEY_Q)) {
            displzInc = 1;
        } else if (Window.isKeyPressed(GLFW_KEY_Z)) {
            scaleInc = -1;
        } else if (Window.isKeyPressed(GLFW_KEY_X)) {
            scaleInc = 1;
        }
    }

    private void update(float delta) {
        for(Gameobject gameobject: gameobjects) {
            // Update position
            Vector3f gameobjectPosition = gameobject.getPosition();
            float posx = gameobjectPosition.x + displxInc * 0.01f;
            float posy = gameobjectPosition.y + displyInc * 0.01f;
            float posz = gameobjectPosition.z + displzInc * 0.01f;
            gameobject.setPosition(posx, posy, posz);

            float scale = gameobject.getScale();
            scale += scaleInc * 0.05f;
            if (scale < 0) {
                scale = 0;
            }
            gameobject.setScale(scale);

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
        shader.setUniform("textureSampler",0);

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