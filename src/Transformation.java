import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {
    private final Matrix4f projectionMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f viewMatrix;

    //Remember this need to apply MVP strategy which is Model - View - Perspective
    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        return projectionMatrix.setPerspective(fov,width/height,zNear, zFar);
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f cameraRotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(cameraRotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(cameraRotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        return viewMatrix;
    }

    public Matrix4f getModelViewMatrix(Gameobject gameobject, Matrix4f viewMatrix) {
        Vector3f gameobjectRotation = gameobject.getRotation();
        Vector3f gameobjectPosition = gameobject.getPosition();
        modelViewMatrix.identity().translate(gameobjectPosition).
                rotateX((float)Math.toRadians(-gameobjectRotation.x)).
                rotateY((float)Math.toRadians(-gameobjectRotation.y)).
                rotateZ((float)Math.toRadians(-gameobjectRotation.z)).
                scale(gameobject.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }



}
