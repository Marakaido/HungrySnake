package com.mooduck.games.hungrysnake;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by marakaido on 02.02.16.
 */
public class OpenGLSurfaceView extends GLSurfaceView {

    private final OpenGLRenderer mRenderer;

    public OpenGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new OpenGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }
}