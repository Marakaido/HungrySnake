attribute vec4 in_vertex;
attribute vec4 in_normal;

uniform mat4 in_Projection_matrix;
uniform mat4 in_View_matrix;
uniform mat4 in_Model_matrix;

varying vec4 normal;
varying vec4 fragment_pos;

void main()
{
    vec4 model = in_Model_matrix * in_vertex;
    gl_Position = in_Projection_matrix * in_View_matrix * model;
    normal = in_normal;
    fragment_pos = model;
}