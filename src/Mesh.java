import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

public class Mesh {
    private final int vaoId;
    private final int[] vboId = new int[3];
    private final int vertexCount;

    public Mesh(float[] positions, int[] indices, float[] colors) {
        FloatBuffer positionsBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer colorsBuffer = null;
        vertexCount = indices.length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        //Position
        int positionVboId = glGenBuffers();
        vboId[0] = positionVboId;
        positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
        positionsBuffer.put(positions).flip();
        glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //Index
        int indexVboId = glGenBuffers();
        vboId[1] = indexVboId;
        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        //Color
        int colorVboId = glGenBuffers();
        vboId[2] = colorVboId;
        colorsBuffer = MemoryUtil.memAllocFloat(colors.length);
        colorsBuffer.put(colors).flip();
        glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);


        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        if (positionsBuffer  != null) {
            MemoryUtil.memFree(positionsBuffer);
        }
        if (indicesBuffer  != null) {
            MemoryUtil.memFree(indicesBuffer);
        }
        if (colorsBuffer  != null) {
            MemoryUtil.memFree(colorsBuffer);
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int[] getVboId() {
        return vboId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {
        glBindVertexArray(getVaoId());

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    public void destroy() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId[0]);
        glDeleteBuffers(vboId[1]);
        glDeleteBuffers(vboId[2]);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
