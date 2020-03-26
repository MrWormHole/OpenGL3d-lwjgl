import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private final int id;

    public Texture(String filePath) throws Exception {
        id = loadTexture(filePath);
    }

    public int getId() {
        return id;
    }

    private static int loadTexture(String filePath) throws Exception {
        int width;
        int height;
        ByteBuffer imageBuffer;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer u = stack.mallocInt(1);
            IntBuffer v = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            imageBuffer = stbi_load(filePath, u, v, channels, 4);

            if(imageBuffer == null) {
                throw new Exception("Image from [" + filePath  + "] not loaded: " + stbi_failure_reason());
            }

            width = u.get();
            height = v.get();
        }

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); we are skipping this because we will use mipmaps for smoothing
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); we are skipping this because we will use mipmaps for smoothing
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);
        stbi_image_free(imageBuffer);

        return textureId;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        unbind();
        glDeleteTextures(id);
    }
}
