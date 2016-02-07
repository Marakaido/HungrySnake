precision mediump float;

uniform vec4 lightColor;
uniform vec4 objectColor;
uniform vec4 lightPos;

varying vec4 normal;
varying vec4 fragment_pos;

void main()
{
    vec4 ambient_light = 0.1 * lightColor;
    vec4 diffuse_light = max(dot(normalize(normal), normalize(fragment_pos - lightPos)), 0.0) * lightColor;
    gl_FragColor = (ambient_light + diffuse_light) * objectColor;
}