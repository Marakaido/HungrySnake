attribute vec4 in_vertex;
uniform mat4 in_MVP_matrix;

void main()
{
    gl_Position = in_MVP_matrix * in_vertex;
}