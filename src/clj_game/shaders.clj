(ns clj-game.shaders
  (:require [clj-game.opengl :as opengl]))

(def basic-vertex-shader
  (opengl/make-shader
   :vertex-shader
   "#version 150 core

    uniform float angle;

    in vec2 position;
    in vec3 color;

    out vec3 Color;

    void main () {
        float angle_radians = angle * (3.1415926535/180);
        mat2 rotation_matrix = mat2(cos(angle_radians), -sin(angle_radians),
                                    sin(angle_radians), cos(angle_radians));
        gl_Position = mat4(rotation_matrix) * vec4(position, 0.0, 1.0);
        Color = color;
    }"))

(def basic-fragment-shader
  (opengl/make-shader
   :fragment-shader
   "#version 150 core

    in vec3 Color;

    out vec4 outColor;

    void main() {
        outColor = vec4(Color, 1.0);
    }"))
