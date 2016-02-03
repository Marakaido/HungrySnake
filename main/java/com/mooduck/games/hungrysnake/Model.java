package com.mooduck.games.hungrysnake;

import java.nio.FloatBuffer;

/**
 * Created by marakaido on 02.02.16.
 */
public class Model {
    private FloatBuffer VBO;

    public Model()
    {

    }

    public static Model loadModel(String filePath)
    {
        return new Model();
    }
}
