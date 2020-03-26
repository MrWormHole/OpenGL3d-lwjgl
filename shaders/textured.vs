#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoordinate;

out vec2 outTextureCoordinate;

uniform mat4 worldMatrix;
uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1.0);
    outTextureCoordinate = textureCoordinate;
}