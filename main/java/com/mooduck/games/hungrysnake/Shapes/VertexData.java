package com.mooduck.games.hungrysnake.Shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by marakaido on 02.02.16.
 *
 * This class represents vertices for a particular shape
 * and is used in renderer to draw it.
 */
public class VertexData {
    private FloatBuffer VBO;
    private int coords_per_vertex;

    public VertexData(float[] coords, int coords_per_vertex)
    {
        VBO = ByteBuffer.allocateDirect(coords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        VBO.put(coords);
        VBO.position(0);

        this.coords_per_vertex = coords_per_vertex;
    }

    public int getCoordsPerVertex() { return coords_per_vertex; }
    public FloatBuffer getBuffer() { return VBO;}
}
