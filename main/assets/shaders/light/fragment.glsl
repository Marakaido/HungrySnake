precision mediump float;

uniform vec4 lightColor;
uniform vec4 objectColor;
uniform vec4 lightPos;
uniform vec4 view_position;

varying vec4 normal;
varying vec4 fragment_pos;

void main()
{
    vec4 ambient_light = vec4(0.1) * lightColor;

    vec4 normilized_normal = normalize(normal);
    vec4 light_direction = normalize(lightPos - fragment_pos);

    vec4 diffuse_light = max(dot(normilized_normal, light_direction), 0.0) * lightColor;
    vec4 reflected_light_direction = reflect(-light_direction, normal);
    vec4 view_direction = normalize(view_position - fragment_pos);

    float specular_intensity = float(0.5);
    vec4 specular_light = pow(max(dot(reflected_light_direction, view_direction), 0.0), float(32)) * lightColor * specular_intensity;

    gl_FragColor = (ambient_light + diffuse_light + specular_light) * objectColor;
}