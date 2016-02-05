package com.mooduck.games.hungrysnake;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by marakaido on 02.02.16.
 */
public class ShaderHandler
{
    private static final String SHADER_LOCATION = "shaders/";

    public static HashMap<String, Shader> shaders = new HashMap<>();

    public static class Shader {
        private int handle;

        private Shader(int handle)
        {
            if(GLES20.glIsProgram(handle)) this.handle = handle;
            else throw new IllegalArgumentException("Not a shader program");
        }

        public int getHandle() { return handle; }
    }

    public static Shader loadShaderProgram(Context context, String shaderName, String vertexShaderFileName, String fragmentShaderFileName)
    {
        String vertexShaderSource = loadShaderSource(context, SHADER_LOCATION + shaderName + "/" + vertexShaderFileName);
        String fragmentShaderSource = loadShaderSource(context, SHADER_LOCATION + shaderName + "/" + fragmentShaderFileName);

        int vertexShader = compileShaderSource(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShaderSource(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);

        // create empty OpenGL ES Program
        int handle = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(handle, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(handle, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(handle);

        // Create new Shader object for this handle
        Shader shader = null;
        try { shader = new Shader(handle); }
        catch (IllegalArgumentException e)
        {
            Log.e("OpenGL", "Failed to load shader program");
            e.printStackTrace();
        }

        // Add new shader program to map 'shaders'
        shaders.put(shaderName, shader);

        return shader;
    }

    // Load shader's code from file
    private static String loadShaderSource(Context context, String fileName)
    {
        StringBuffer stringBuffer = new StringBuffer();
        try
        {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader fin = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = fin.readLine()) != null)
            {
                stringBuffer.append(line);
            }
        }
        catch (IOException e)
        {
            Log.e("File input", "Failed to load shader");
            e.printStackTrace();
        }
        Log.v("File input", "Loaded .glsl file:\n" + stringBuffer.toString());

        return stringBuffer.toString();
    }

    private static int compileShaderSource(int type, String source)
    {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
