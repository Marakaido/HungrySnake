package com.mooduck.games.hungrysnake;

import android.opengl.GLES20;

/**
 * Created by marakaido on 02.02.16.
 */
public class ShaderProgram {
    private int handle;

    public ShaderProgram(String vertexShaderCode, String fragmentShaderCode)
    {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        handle = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(handle, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(handle, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(handle);
    }

    public static int loadShader(int type, String shaderCode)
    {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
