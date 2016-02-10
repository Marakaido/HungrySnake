package com.mooduck.games.hungrysnake;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by marakaido on 02.02.16.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer
{
    private Context context;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    float[] lightColor = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
    float[] lightPos = new float[] {-2.0f, 0.0f, 3.0f, 1.0f};
    float[] cameraPos = new float[] {0.0f, 0.0f, 3.0f, 1.0f};
    public long last_frame_time = 0;
    public double time = 0;

    OpenGLRenderer(Context context) { this.context = context; }

    public Context getContext() { return context; }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        //GLES20.glDepthMask(true);
        // Load a model
        ModelHandler.loadModel(this.context, R.raw.box);
        ModelHandler.loadModel(this.context, R.raw.rock);
        ModelHandler.loadModel(this.context, R.raw.lizard);

        //context.getAssets().list("shaders");
        // Load a shader program
        ShaderHandler.loadShaderProgram(context, "light", "vertex.glsl", "fragment.glsl");
        ShaderHandler.loadShaderProgram(context, "lightSource", "vertex.glsl", "fragment.glsl");
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, -3, 3, 9, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //Matrix.setLookAtM();
    }

    public void onDrawFrame(GL10 unused)
    {
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        // Clear color and depth buffers
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        time = SystemClock.uptimeMillis();
        time /= 10000;
        cameraPos[0] = (float)(9*Math.cos(time));
        cameraPos[2] = (float)(9*Math.sin(time));
        Matrix.setLookAtM(mViewMatrix, 0, cameraPos[0], cameraPos[1], cameraPos[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Draw scene
        drawScene(mMVPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 0.1f, 100f);
    }

    public void drawScene(float[] MVP_matrix)
    {
        // Draw a cube
        Matrix.setIdentityM(mModelMatrix, 0);
        int currentProgram = ShaderHandler.shaders.get("light").getHandle();
        GLES20.glUseProgram(currentProgram);
        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(currentProgram, "in_vertex");
        int mNormalHandle = GLES20.glGetAttribLocation(currentProgram, "in_normal");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(currentProgram, "in_Projection_matrix"), 1, false, mProjectionMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(currentProgram, "in_View_matrix"), 1, false, mViewMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(currentProgram, "in_Model_matrix"), 1, false, mModelMatrix, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(currentProgram, "lightColor"), 1, lightColor, 0);
        GLES20.glUniform4f(GLES20.glGetUniformLocation(currentProgram, "objectColor"), 1.0f, 0.5f, 0.31f, 1.0f);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(currentProgram, "lightPos"), 1, lightPos, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(currentProgram, "view_position"), 1, cameraPos, 0);
        // Send data to opengl
        ModelHandler.Model cube = ModelHandler.get("Lizard");
        cube.buffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, cube.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                cube.VERTEX_STRIDE, cube.buffer);
        cube.buffer.position(3);
        GLES20.glVertexAttribPointer(mNormalHandle, cube.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                cube.VERTEX_STRIDE, cube.buffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cube.vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);

        // Draw light source
        // Set new Mvp
        cube = ModelHandler.get("Box");
        Matrix.translateM(mModelMatrix, 0, lightPos[0], lightPos[1], lightPos[2]);
        Matrix.scaleM(mModelMatrix, 0, 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mModelMatrix, 0, mMVPMatrix, 0, mModelMatrix, 0);
        currentProgram = ShaderHandler.shaders.get("lightSource").getHandle();
        GLES20.glUseProgram(currentProgram);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(currentProgram, "in_vertex");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(currentProgram, "in_MVP_matrix"), 1, false, mModelMatrix, 0);
        // Send data to opengl
        cube.buffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, cube.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                cube.VERTEX_STRIDE, cube.buffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cube.vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
