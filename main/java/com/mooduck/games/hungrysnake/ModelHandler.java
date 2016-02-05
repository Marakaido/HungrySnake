package com.mooduck.games.hungrysnake;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by marakaido on 02.02.16.
 */

public class ModelHandler
{
    private static HashMap<String, Model> models = new HashMap<>();

    public static class Model
    {
        public FloatBuffer VBO;
        public ShortBuffer indeces;

        public int vertexCount;

        public static final int COORDS_PER_VERTEX = 3;
        public static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

        private Model(float[] vertices, short[] indeces)
        {
            VBO = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            VBO.put(vertices);
            VBO.position(0);

            this.indeces = ByteBuffer.allocateDirect(indeces.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            this.indeces.put(indeces);
            this.indeces.position(0);

            vertexCount = vertices.length / COORDS_PER_VERTEX;
        }

        public FloatBuffer getVBO() { return VBO; }
        public ShortBuffer getIndeces() { return indeces; }
    }

    public static Model get(String modelName)
    {
        return models.get(modelName);
    }

    // Load model from .obj file into map "models"
    // Only one object can be loaded at a time
    public static Model loadModel(Context context, int resourceHandle)
    {
        Model result = null;
        String modelName = "none"; // Name of the model, specified in .obj file
        Vector<Float> verteces = new Vector<>();
        Vector<Short> indeces = new Vector<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceHandle);
            BufferedReader fin = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line; // Hold a single line of file
                String[] data; // Hold parts of line, separated by ' '

                for(line = fin.readLine(); line != null; line = fin.readLine())
                {
                    data = line.split(" ");
                    switch(data[0])
                    {
                        case "o": // Set model name
                            modelName = data[1]; break;
                        case "v": // Add vertex data
                            //Log.v("Vertex data", (new Float(data[1])).toString());
                            for(int i = 1; i < data.length; i++) verteces.add(new Float(data[i]));
                            break;
                        case "f": // Add index data
                            for(int i = 1; i < data.length; i++)
                            {
                                Short val = new Short(data[i]);
                                val--;
                                indeces.add(val);
                            }
                            break;
                        default: continue;
                    }
                }

                /* Load data to model */
                float[] raw_vert = new float[verteces.size()];
                int i = 0;
                for(Float val : verteces)
                {
                    raw_vert[i] = val;
                    i++;
                }
                short[] raw_ind = new short[indeces.size()];
                i = 0;
                for(Short val : indeces)
                {
                    raw_ind[i] = val;
                    i++;
                }
                result = new Model(raw_vert, raw_ind);
                Log.v("Loaded", modelName);
                models.put("Cube", result); // Put new model to map 'models'
            }
            finally { fin.close(); }
        }
        catch (IOException e)
        {
            Log.e("Resource", "failed to load model");
            e.printStackTrace();
        }
        return result;
    }
}
