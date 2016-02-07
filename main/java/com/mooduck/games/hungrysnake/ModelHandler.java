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
        /*
        * buffer - holds vertexes and normals for model
        * Data layout:
        * { vertex data, normal}
        */
        public FloatBuffer buffer;
        public ShortBuffer indexes;

        public int vertexCount;

        public static final int COORDS_PER_VERTEX = 3;
        public static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 2 * 4;

        private Model(float[] buffer_data, short[] indexes)
        {
            this.indexes = ByteBuffer.allocateDirect(indexes.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            this.indexes.put(indexes);
            this.indexes.position(0);
            
            // buffer will incorporate both vertexes vertices and normals
            buffer = ByteBuffer.allocateDirect(buffer_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            // Write all vertices and normals
            buffer.put(buffer_data);
            // Set position to 0
            buffer.position(0);
            
            // Calculate number of vertices for the model
            vertexCount = buffer_data.length / COORDS_PER_VERTEX / 2;
        }

        public FloatBuffer getbuffer() { return buffer; }
        public ShortBuffer getIndexes() { return indexes; }
    }

    public static Model get(String modelName)
    {
        return models.get(modelName);
    }

    // Load model from .obj file into map "models"
    // Only one object can be loaded at a time
    public static Model loadModel(Context context, int resourceHandle)
    {
        Model result = null; // A reference to a model that is being created

        Vector<Float> verteces = new Vector<>(); // Temporary container for vertex data
        Vector<Float> normals = new Vector<>(); // Temporary container for normal data
        Vector<Short> indexes = new Vector<>(); // Temporary container for index data
        Vector<Short> normal_indexes = new Vector<>(); // Temporary container for normal index data

        InputStream inputStream = context.getResources().openRawResource(resourceHandle);
        // Populate vectors with data
        // Name of the model, specified in .obj file
        String modelName = parse_obj_file(inputStream, verteces, normals, indexes, normal_indexes);
        
        // Form index_data
        int i = 0;
        short[] index_data = new short[indexes.size()];
        for(Short val : indexes) { index_data[i] = val; i++; }

        /* Form buffer_data */
        i=0;
        float[] buffer_data = new float[verteces.size() * 2];
        // Write all vertex data
        for(int j = 0; i < verteces.size(); i++, j+=4)
        {
            buffer_data[j] = verteces.elementAt(i);
            buffer_data[++j] = verteces.elementAt(++i);
            buffer_data[++j] = verteces.elementAt(++i);
        }
        // Write all normal data
        i = 0;
        for(Short index : normal_indexes)
        {
            buffer_data[index_data[i] * 6 + 3] = normals.elementAt(index*3);
            buffer_data[index_data[i] * 6 + 4] = normals.elementAt(index*3 + 1);
            buffer_data[index_data[i] * 6 + 5] = normals.elementAt(index*3 + 2);
            i++;
        }
        
        // Form new Model object
        result = new Model(buffer_data, index_data);
        // Register new model in ModelHandler
        models.put(modelName, result);
        
        Log.v("Loaded", modelName);
        
        return result;
    }
    
    private static String parse_obj_file(InputStream inputStream,
                                       Vector<Float> verteces,
                                       Vector<Float> normals,
                                       Vector<Short> indexes,
                                       Vector<Short> normal_indexes)
    {
        String modelName = "none";
        try
        {
            BufferedReader fin = new BufferedReader(new InputStreamReader(inputStream));
            try 
            {
                String line; // Hold a single line of file
                String[] data; // Hold parts of line, separated by ' '
                String[] face_data;

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
                        case "vn": // Add vertex normal data
                            for(int i = 1; i < data.length; i++) normals.add(new Float(data[i]));
                            break;
                        case "f": // Add index data
                            for(int i = 1; i < data.length; i++)
                            {
                                face_data = data[i].split("/");

                                // Vertex index
                                Short index = new Short(face_data[0]);
                                index--;
                                indexes.add(index);

                                // Normal index
                                index = new Short(face_data[2]);
                                index--;
                                normal_indexes.add(index);
                            }
                            break;
                        default: continue;
                    }
                }
            }
            finally { fin.close(); }
        }
        catch (IOException e)
        {
            Log.e("Resource", "failed to load model");
            e.printStackTrace();
        }
        return modelName;
    }
    
}
